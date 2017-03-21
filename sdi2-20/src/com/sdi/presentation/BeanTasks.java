package com.sdi.presentation;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

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
		System.out.println("BeanTasks - PostConstruct");

		user = (BeanUser) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get(new String("user"));

		if (user != null) {
			try {
				tasks = Services.getTaskService().
				// findFinishedInboxTasksByUserId(user.getId());
						findInboxTasksByUserId(user.getId());
				listaSeleccionada = "Inbox";
				System.out.println("Obtenidas tareas INBOX");

				categories = Services.getTaskService().findCategoriesByUserId(
						user.getId());
				System.out.println("Obtenidas categorias");
			} catch (BusinessException b) {
				System.out.println("Se ha producido algún error en la capa de"
						+ "persistencia buscando las tareas y categorias");
			}
		}

		task = (BeanTask) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get(new String("task"));
		for(int i=0; i<tasks.size(); i++){
			tasks.get(i).setCatName(nombreCategoria(tasks.get(i).getCategoryId()));
			tasks.get(i).setRetrasada(estaRetrasada(tasks.get(i).getId()));			
		}
		// si no existe lo creamos e inicializamos
		if (task == null) {
			System.out.println("BeanTask-No existia");
			task = new BeanTask();
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().put("task", task);
		}
	}

	@PreDestroy
	public void end() {
		System.out.println("BeanUsers - PreDestroy");
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

	public void inboxTask() {

		try {
			tasks = Services.getTaskService().findInboxTasksByUserId(
					user.getId());
			listaSeleccionada = "Inbox";
			for(int i=0; i<tasks.size(); i++){
				tasks.get(i).setCatName(nombreCategoria(tasks.get(i).getCategoryId()));
				tasks.get(i).setRetrasada(estaRetrasada(tasks.get(i).getId()));			
			}
			System.out.println("Obtenidas tareas INBOX");
		} catch (BusinessException b) {

		}
	}

	public void todayTask() {

		try {
			tasks = Services.getTaskService().findTodayTasksByUserId(
					user.getId());
			listaSeleccionada = "Today";
			for(int i=0; i<tasks.size(); i++){
				tasks.get(i).setCatName(nombreCategoria(tasks.get(i).getCategoryId()));
				tasks.get(i).setRetrasada(estaRetrasada(tasks.get(i).getId()));			
			}
			System.out.println("Obtenidas tareas TODAY");
		} catch (BusinessException b) {

		}
	}

	public void weekTask() {

		try {
			tasks = Services.getTaskService().findWeekTasksByUserId(
					user.getId());
			listaSeleccionada = "Week";
			
			for(int i=0; i<tasks.size(); i++){
				tasks.get(i).setCatName(nombreCategoria(tasks.get(i).getCategoryId()));
				tasks.get(i).setRetrasada(estaRetrasada(tasks.get(i).getId()));			
			}
			System.out.println("Obtenidas tareas WEEK");
		} catch (BusinessException b) {

		}
	}

	@SuppressWarnings("deprecation")
	public void createTask() {
		TaskService taskService;
		Date date = new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		if (task.getPlanned() == null) {
			System.out.println("Fecha planeada nula");
			return;
		} else {
			Date d = task.getPlanned();
			d.setHours(1);
			if (d.compareTo(date) == -1) {
				System.out.println("Fecha planeada inferior a la actual: " + d
						+ " -- " + date);
				return;
			}
		}
		try {
			taskService = Services.getTaskService();
			task.setUserId(user.getId());
			task.setCreated(new Date());
			if (!category.isEmpty())
				task.setCategoryId(Long.parseLong(category));
			taskService.createTask(task);
			if (task.getCategoryId() == null)
				inboxTask();
			else {
				if (task.getPlanned().getYear() == (date.getYear())
						&& task.getPlanned().getMonth() == (date.getMonth())
						&& task.getPlanned().getDay() == (date.getDay()))
					todayTask();
				else
					weekTask();
				System.out.println(task.getPlanned() + " - " + date);
			}

			// Vaciamos el bean
			task.setCategoryId(null);
			task.setComments(null);
			task.setCreated(null);
			task.setFinished(null);
			task.setPlanned(new Date());
			task.setTitle("New task");
			task.setUserId(null);
		} catch (BusinessException b) {

		}
	}

	public void cerrarTarea(Long id) {
		try {
			Services.getTaskService().markTaskAsFinished(id);
			inboxTask();
			System.out.println("Tarea: " + id + " cerrada correctamente");
		} catch (BusinessException b) {

		}
	}

	public String editarTarea(Task editar) {
		try {
			task.setTask(editar);
		} catch (Exception e) {
			return "error";
		}
		return "exito";
	}

	/*
	 * Este es el método que modifica la tarea a los nuevos valores. Nos
	 * devuelve al listado
	 */
	public String modificarTarea() {
		try {
			Services.getTaskService().updateTask(task);

		} catch (BusinessException b) {
			System.out
					.println("Algo ha ocurrido editando la tarea seleccionada");
			return "error";
		}

		task.iniciaTask(null);
		inboxTask();
		System.out.println("Se ha modificado la tarea con EXITO");
		return "exito";
	}

	@SuppressWarnings("deprecation")
	public boolean estaRetrasada(Long id){
		TaskService taskService = Services.getTaskService();
		Task t;
		try {
			t = taskService.findTaskById(id);
		} catch (BusinessException e) {
			e.printStackTrace();
			return false;
		}
		Date date = new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		Date d = t.getPlanned();
		d.setHours(1);
		if (d.compareTo(date) == -1) {
			System.out.println("Está retrasada: " + id);
			return true;
		}
		System.out.println("NO está retrasada: " + id);
		return false;
	}

	public String nombreCategoria(Long id){
		TaskService taskServices = Services.getTaskService();
		Category c;
		try {
			c = taskServices.findCategoryById(id);
			if(c != null){
				System.out.println(c.getName());
				return c.getName();
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		System.out.println("Sin categoria");
		return "";
	}

	public void getFinishedTask(){
		TaskService taskServices = Services.getTaskService();
		if(terminadas == null)
			setTerminadas(true);
		try {
			if(terminadas)
				tasks.addAll(taskServices.findFinishedInboxTasksByUserId(user.getId()));
			else
				tasks = taskServices.findInboxTasksByUserId(user.getId());
			terminadas = !terminadas;
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	private void setTerminadas(boolean b) {
		this.terminadas=b;
	}
	
}
