package com.sdi.presentation;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.*;
import javax.faces.event.ActionEvent;
import com.sdi.business.AdminService;
import com.sdi.business.Services;
import com.sdi.business.UserService;
import com.sdi.business.exception.BusinessException;
import com.sdi.dto.Task;
import com.sdi.dto.User;
import com.sdi.dto.types.UserStatus;

@ManagedBean(name = "controller")
@SessionScoped
public class BeanUser implements Serializable {

	private static final long serialVersionUID = 55555L;

	// Se aÃ±ade este atributo de entidad para recibir el alumno concreto
	// selecionado de
	// la tabla o de un formulario
	// Es necesario inicializarlo para que al entrar desde el formulario de
	// AltaForm.xml
	// se puedan
	// dejar los avalores en un objeto existente.

	private User user = new User();

	private List<User> users = null;
	
	private List<Task> tasks = null;

	public BeanUser() {
		iniciaUser(null);
	}

	// Inicia el usuario pero aún no tiene valores
	public void iniciaUser(ActionEvent event) {
		user.setId(null);
		user.setLogin("login");
		user.setPassword("password");
		user.setIsAdmin(false);
		user.setEmail("email");
		user.setStatus(UserStatus.DISABLED);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User alumno) {
		this.user = alumno;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Task> getTasks() {
		return tasks;
	}
	
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	public String validar() {
		UserService userService;

		try {
			userService = Services.getUserService();
			user = userService.findLoggableUser(user.getLogin(),
					user.getPassword());
			if (user.getIsAdmin()) { // Si es admin muestra la lista usuarios
				listadoUsuarios();
				return "admin";
			}
			
			listadoTareas();//Si usuario cargamos las tareas desde el inbox
			
		} catch (BusinessException b) {
			return "error"; //Se produjo algún error al validar
		}
		
		return "user"; //Es un usario normal
	}
	
	public String cambiarEstado(User user){
		AdminService adminService;
		if(user == null)
			return "error";
		try{
			adminService= Services.getAdminService();
			if(user !=null && user.getStatus().equals(UserStatus.ENABLED)){
				adminService.disableUser(user.getId());
			}
			else{
				adminService.enableUser(user.getId());
			}
			listadoUsuarios(); //Actualizamos la lista de usuarios
			return "exito"; //Nos volvemos al listado
		}catch(BusinessException b){
			return "error";
		}
	}
	
	public String eliminar(User user){
		AdminService adminService;
		if(user == null)
			return "error";
		try{
			adminService = Services.getAdminService();
			adminService.deepDeleteUser(user.getId());
			listadoUsuarios();
			return "exito"; //Nos volvemos al listado
		} catch (BusinessException b){
			return "error";
		}
	}

	private void listadoUsuarios() throws BusinessException {
		users =  Services.getAdminService().findAllUsers();
	}
	
	private void listadoTareas() throws BusinessException{
		tasks = Services.getTaskService().findInboxTasksByUserId(user.getId());
	}
}