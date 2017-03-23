package com.sdi.presentation;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import alb.util.date.DateUtil;
import alb.util.log.Log;

import com.sdi.business.Services;
import com.sdi.business.TaskService;
import com.sdi.business.exception.BusinessException;
import com.sdi.dto.Category;
import com.sdi.dto.Task;

@ManagedBean(name = "btasks")
@SessionScoped
public class BeanTasks implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{user}")
	private BeanUser user = null;

	@ManagedProperty(value = "#{task}")
	private BeanTask task = null;

	private List<Task> tasks;

	private List<Category> categories;

	private String listaSeleccionada = "Inbox";
	private String category;
	private Boolean terminadas;

	public boolean isTerminadas() {
		return terminadas;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@PostConstruct
	public void init() {
		setTerminadas(true);
		Log.debug("BeanTasks - PostConstruct");

		user = (BeanUser) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get(new String("user"));

		if (user != null) {
			try {
				tasks = Services.getTaskService().
				// findFinishedInboxTasksByUserId(user.getId());
						findInboxTasksByUserId(user.getId());
				listaSeleccionada = "Inbox";
				Log.debug("Obtenidas tareas INBOX");

				categories = Services.getTaskService().findCategoriesByUserId(
						user.getId());
				Log.debug("Obtenidas categorias");
			} catch (BusinessException b) {
				Log.error("Se ha producido algún error en la capa de"
						+ "persistencia buscando las tareas y categorias");
			}
		}

		task = (BeanTask) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get(new String("task"));
		for (int i = 0; i < tasks.size(); i++) {
			tasks.get(i).setCatName(
					nombreCategoria(tasks.get(i).getCategoryId()));
			tasks.get(i).setRetrasada(estaRetrasada(tasks.get(i).getId()));
		}
		// si no existe lo creamos e inicializamos
		if (task == null) {
			Log.debug("BeanTask-No existia");
			task = new BeanTask();
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().put("task", task);
		}
	}

	@PreDestroy
	public void end() {
		Log.debug("BeanUsers - PreDestroy");
	}

	public String getListaSeleccionada() {
		return listaSeleccionada;
	}

	public void setListaSeleccionada(String listaSeleccionada) {
		this.listaSeleccionada = listaSeleccionada;
	}

	public BeanTasks() {
		user = (BeanUser) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get(new String("user"));

		inboxTask();
	}

	public BeanUser getUser() {
		return user;
	}

	public void setUser(BeanUser user) {
		this.user = user;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(BeanTask task) {
		this.task = task;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	/**
	 * Obtiene las tareas inbox del usuario
	 */
	public void inboxTask() {

		try {
			tasks = Services.getTaskService().findInboxTasksByUserId(
					user.getId());
			listaSeleccionada = "Inbox";
			for (int i = 0; i < tasks.size(); i++) {
				tasks.get(i).setCatName(
						nombreCategoria(tasks.get(i).getCategoryId()));
				tasks.get(i).setRetrasada(estaRetrasada(tasks.get(i).getId()));
			}
			Log.debug("Obtenidas tareas INBOX");
		} catch (BusinessException b) {
			Log.error("Error al obtener las tareas INBOX");
		}
	}

	/**
	 * Obtiene las tareas de hoy o restrasadas del usuario
	 */
	public void todayTask() {

		try {
			tasks = Services.getTaskService().findTodayTasksByUserId(
					user.getId());
			listaSeleccionada = "Today";
			for (int i = 0; i < tasks.size(); i++) {
				tasks.get(i).setCatName(
						nombreCategoria(tasks.get(i).getCategoryId()));
				tasks.get(i).setRetrasada(estaRetrasada(tasks.get(i).getId()));
			}
			Log.debug("Obtenidas tareas TODAY");
		} catch (BusinessException b) {
			Log.error("Error al obtener las tareas HOY");
		}
	}

	/**
	 * Obtiene las tareas de dentro de una semana o restrasadas del usuario
	 */
	public void weekTask() {

		try {
			tasks = Services.getTaskService().findWeekTasksByUserId(
					user.getId());
			listaSeleccionada = "Week";

			for (int i = 0; i < tasks.size(); i++) {
				tasks.get(i).setCatName(
						nombreCategoria(tasks.get(i).getCategoryId()));
				tasks.get(i).setRetrasada(estaRetrasada(tasks.get(i).getId()));
			}
			Log.debug("Obtenidas tareas WEEK");
		} catch (BusinessException b) {
			Log.error("Error al obtener las tareas WEEK");
		}
	}

	/**
	 * Metodo que crea una tarea para el usuario en funcion de los parametros
	 * suministrados
	 */
	public void createTask() {
		TaskService taskService;
		if (task.getPlanned() != null
				&& DateUtil.isBefore(task.getPlanned(), DateUtil.today())) {
			Log.debug("Fecha planeada inferior a la actual");
			return;
		}
		try {
			taskService = Services.getTaskService();
			task.setUserId(user.getId());
			if (!category.isEmpty())
				task.setCategoryId(Long.parseLong(category));

			if (task.getPlanned() == null)
				task.setPlanned(DateUtil.today());

			taskService.createTask(task);

			if (task.getCategoryId() == null)
				inboxTask();
			else {
				if (DateUtil.isNotAfter(task.getPlanned(), DateUtil.today()))
					todayTask();
				else
					weekTask();
			}

			// Vaciamos el bean
			task.iniciaTask(null);
			Log.debug("Tarea creada correctamente");
			FacesContext.getCurrentInstance().addMessage(
					"mensaje",
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
							"Task created"));
		} catch (BusinessException b) {
			Log.error("No se ha podido guardar la nueva tarea");
		}
	}

	/**
	 * Permite finalizar la tarea seleccionada
	 * 
	 * @param id
	 */
	public void cerrarTarea(Long id) {
		TaskService taskServices = Services.getTaskService();
		try {
			taskServices.markTaskAsFinished(id);
			inboxTask();
			if (!terminadas)
				tasks.addAll(taskServices.findFinishedInboxTasksByUserId(user
						.getId()));
			Log.debug("Tarea: " + id + " cerrada correctamente");
			FacesContext.getCurrentInstance().addMessage(
					"mensaje",
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
							"Task finished"));
		} catch (BusinessException b) {
			Log.error("No se ha podido cerrar la tarea seleccionada");
		}
	}

	/**
	 * Nos permite acceder a la pagina de ediccion de la tarea
	 * 
	 * @param editar
	 * @return String error, exito
	 */
	public String editarTarea(Task editar) {
		try {
			task.setTask(editar);
		} catch (Exception e) {
			Log.error("No se ha podido acceder a la edición de tarea");
			return "error";
		}
		return "exito";
	}

	/**
	 * Este es el método que modifica la tarea a los nuevos valores. Nos
	 * devuelve al listado
	 * 
	 * @return
	 */
	public String modificarTarea() {
		try {
			Services.getTaskService().updateTask(task);

		} catch (BusinessException b) {
			Log.error("Algo ha ocurrido editando la tarea seleccionada");
			return "error";
		}

		task.iniciaTask(null);
		inboxTask();
		Log.debug("Se ha modificado la tarea con EXITO");
		FacesContext.getCurrentInstance().addMessage(
				"mensaje",
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
						"Task modified"));
		return "exito";
	}

	public boolean estaRetrasada(Long id) {
		TaskService taskService = Services.getTaskService();
		Task t;
		try {
			t = taskService.findTaskById(id);
		} catch (BusinessException e) {
			e.printStackTrace();
			return false;
		}

		if (t.getPlanned() != null
				&& DateUtil.isBefore(t.getPlanned(), DateUtil.today())) {
			return true;
		}
		return false;
	}

	public String nombreCategoria(Long id) {
		TaskService taskServices = Services.getTaskService();
		Category c;
		try {
			c = taskServices.findCategoryById(id);
			if (c != null) {
				return c.getName();
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void getFinishedTask() {
		TaskService taskServices = Services.getTaskService();
		if (terminadas == null)
			setTerminadas(true);
		try {
			if (terminadas)
				tasks.addAll(taskServices.findFinishedInboxTasksByUserId(user
						.getId()));
			else
				tasks = taskServices.findInboxTasksByUserId(user.getId());
			terminadas = !terminadas;
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	private void setTerminadas(boolean b) {
		this.terminadas = b;
	}

}
