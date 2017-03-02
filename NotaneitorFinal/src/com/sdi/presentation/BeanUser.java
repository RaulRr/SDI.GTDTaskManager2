package com.sdi.presentation;

import java.io.Serializable;

import javax.faces.bean.*;
import javax.faces.event.ActionEvent;

import com.sdi.business.Services;
import com.sdi.business.UserService;
import com.sdi.business.exception.BusinessException;
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

	private User[] users = null;

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

	public User[] getUsers() {
		return users;
	}

	public void setUsers(User[] users) {
		this.users = users;
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

		} catch (BusinessException e) {
			return "error"; //Se produjo algún error al validar
		}

		return "user"; //Es un usario normal
	}

	private void listadoUsuarios() throws BusinessException {
		users = (User[]) Services.getAdminService().findAllUsers()
				.toArray(new User[0]);
	}

}