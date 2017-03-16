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
	
	@PostConstruct
	public void init() {
		System.out.println("BeanTasks - PostConstruct");
		
		user = (BeanUser)
				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(new
						String("user"));
		
		if(user != null){
			try {
				tasks = Services.getTaskService().findTasksByCategoryId(((Integer)1).longValue());
				listaSeleccionada="Inbox";
				System.out.println("Obtenidas tareas INBOX");
				
				categories = Services.getTaskService().findCategoriesByUserId(user.getId());
				System.out.println("Obtenidas categorias");
			} catch (BusinessException b) {
				System.out.println("Se ha producido algún error en la capa de"
						+ "persistencia buscando las tareas y categorias");
			}
		}
		
		task = (BeanTask)
				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(new
						String("task"));
		
		//si no existe lo creamos e inicializamos
		if (task == null) {
			System.out.println("BeanTask-No existia");
			task = new BeanTask();
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(
					"task", task);
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
			listaSeleccionada="Inbox";
			System.out.println("Obtenidas tareas INBOX");
		} catch (BusinessException b) {

		}
	}

	public void todayTask() {

		try {
			tasks = Services.getTaskService().findTodayTasksByUserId(
					user.getId());
			listaSeleccionada="Today";
			System.out.println("Obtenidas tareas TODAY");
		} catch (BusinessException b) {

		}
	}

	public void weekTask() {

		try {
			tasks = Services.getTaskService().findWeekTasksByUserId(
					user.getId());
			listaSeleccionada="Week";
			System.out.println("Obtenidas tareas WEEK");
		} catch (BusinessException b) {

		}
	}

	private void createTask() {

	}

	public void cerrarTarea(Long id) {
		try {
			task = (BeanTask)Services.getTaskService().findTaskById(id);
			task.setFinished(new Date());
			Services.getTaskService().updateTask(task);
			System.out.println("Tarea: " + id + " cerrada correctamente");
		} catch (BusinessException b) {

		}
	}
	
	public String editarTarea(Task editar){
		try{
			task.setTask(editar);
		}catch(Exception e){
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
			if (task.getCategoryId() <= 0) { // Se selecciono ninguna
				task.setCategoryId(null);
			}
			Services.getTaskService().updateTask(task);
		} catch (BusinessException b) {
			System.out
					.println("Algo ha ocurrido editando la tarea seleccionada");
			return "error";
		}
		
		System.out.println("Se ha modificado la tarea con EXITO");
		return "exito";
	}

}
