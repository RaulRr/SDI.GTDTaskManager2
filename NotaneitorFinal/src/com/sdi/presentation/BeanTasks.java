package com.sdi.presentation;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.sdi.business.Services;
import com.sdi.business.exception.BusinessException;
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

	private List<Task> tasks;
	private Task task;

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

	public void setTask(Task task) {
		this.task = task;
	}

	public void inboxTask() {

		try {
			tasks = Services.getTaskService().findInboxTasksByUserId(
					user.getId());
			System.out.println("Obtenidas tareas INBOX");
		} catch (BusinessException b) {

		}
	}

	public void todayTask() {

		try {
			tasks = Services.getTaskService().findTodayTasksByUserId(
					user.getId());
			System.out.println("Obtenidas tareas TODAY");
		} catch (BusinessException b) {

		}
	}

	public void weekTask() {

		try {
			tasks = Services.getTaskService().findWeekTasksByUserId(
					user.getId());
			System.out.println("Obtenidas tareas WEEK");
		} catch (BusinessException b) {

		}
	}

	private void createTask() {

	}

	public void cerrarTarea(Long id) {
		try {
			task = Services.getTaskService().findTaskById(id);
			task.setFinished(new Date());
			Services.getTaskService().updateTask(task);
			System.out.println("Tarea: " + id + " cerrada correctamente");
		} catch (BusinessException b) {

		}
	}

}
