package com.sdi.presentation;

import java.io.Serializable;

import javax.faces.bean.*;
import javax.faces.event.ActionEvent;

import com.sdi.dto.User;
import com.sdi.dto.types.UserStatus;

@ManagedBean(name = "user")
@SessionScoped
public class BeanUser extends User implements Serializable {
	private static final long serialVersionUID = 55556L;

	public BeanUser() {
		iniciaUser(null);
	}

	public void setUser(User user) {
		setId(user.getId());
		setLogin(user.getLogin());
		setEmail(user.getEmail());
		setPassword(user.getPassword());
		setStatus(user.getStatus());
		setIsAdmin(user.getIsAdmin());
	}

	// Iniciamos los datos del user con los valores por defecto
	// extraídos del archivo de propiedades correspondiente
	public void iniciaUser(ActionEvent event) {
		setId(null);
		setLogin("login");
		setPassword("");
		setIsAdmin(false);
		setEmail("email");
		setStatus(UserStatus.DISABLED);
	}
}