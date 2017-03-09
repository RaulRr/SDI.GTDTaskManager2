package com.sdi.presentation;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;

import com.sdi.business.AdminService;
import com.sdi.business.Services;
import com.sdi.business.UserService;
import com.sdi.business.exception.BusinessException;
import com.sdi.dto.Task;
import com.sdi.dto.User;
import com.sdi.dto.types.UserStatus;

@ManagedBean(name = "controller")
@SessionScoped
public class BeanUsers implements Serializable {

	private static final long serialVersionUID = 55555L;

	
	@ManagedProperty(value="#{user}")
	private BeanUser user;

	private List<User> users = null;

	private List<Task> tasks = null;

	private String pass = "";
	
	//Se inicia correctamente el MBean inyectado si JSF lo hubiera crea
		//y en caso contrario se crea. (hay que tener en cuenta que es un Bean de sesión)
		//Se usa @PostConstruct, ya que en el contructor no se sabe todavía si el ManagedBean
		//ya estaba construido y en @PostConstruct SI.
		
		@PostConstruct
		public void init() {
			System.out.println("BeanUser - PostConstruct");
			//Buscamos el alumno en la sesión. Esto es un patrón factoría claramente.
			user = (BeanUser)
					FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(new
							String("user"));
			//si no existe lo creamos e inicializamos
			if (user == null) {
				System.out.println("BeanUser - No existia");
				user = new BeanUser();
				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(
						"user", user);
			}
		}
		
		@PreDestroy
		public void end() {
			System.out.println("BeanUser - PreDestroy");
		}

	

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public BeanUser getUser() {
		return user;
	}

	public void setUser(BeanUser alumno) {
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
			user.setUser(userService.findLoggableUser(user.getLogin(),
					user.getPassword()));
			user.setPassword(null);//Por seguridad
			
			if (user.getIsAdmin()) { // Si es admin muestra la lista usuarios
				listadoUsuarios();
				return "admin";
			}
			
			listadoTareas();// Si usuario cargamos las tareas desde el inbox

		} catch (BusinessException b) {
			return "error"; // Se produjo algún error al validar
		}

		return "user"; // Es un usario normal
	}

	public String cambiarEstado(User user) {
		AdminService adminService;
		if (user == null)
			return "error";
		try {
			adminService = Services.getAdminService();
			if (user != null && user.getStatus().equals(UserStatus.ENABLED)) {
				adminService.disableUser(user.getId());
			} else {
				adminService.enableUser(user.getId());
			}
			listadoUsuarios(); // Actualizamos la lista de usuarios
			return "exito"; // Nos volvemos al listado
		} catch (BusinessException b) {
			return "error";
		}
	}

	public String eliminar(User user) {
		AdminService adminService;
		if (user == null)
			return "error";
		try {
			adminService = Services.getAdminService();
			adminService.deepDeleteUser(user.getId());
			listadoUsuarios();
			return "exito"; // Nos volvemos al listado
		} catch (BusinessException b) {
			return "error";
		}
	}

	private void listadoUsuarios() throws BusinessException {
		users = Services.getAdminService().findAllUsers();
	}

	private void listadoTareas() throws BusinessException {
		tasks = Services.getTaskService().findInboxTasksByUserId(user.getId());
	}

	public String registro() {
		return "true";
	}

	public String registrar() {
		UserService userService;

		if (!user.getEmail().matches("[-\\w\\.]+@\\w+\\.\\w+")) {
			System.out.println("Email invalido: " + user.getEmail());
			return "false";
		}

		if (!pass.equals(user.getPassword())) {
			System.out.println("Las contraseñas no coinciden: " + pass + " - "
					+ user.getPassword());
			return "false";
		}

		else {
			if (pass.length()<8) {
				System.out
						.println("Las contraseñas deben medir al menos 8 caracteres "
								+ pass);
				return "false";
			}
			
			if (!pass.matches(".*[a-zA-Z].*") || !pass.matches(".*[0-9].*")) {
				System.out
						.println("Las contraseñas no contiene letras y números "
								+ pass);
				return "false";
			}
		}

		try {
			userService = Services.getUserService();
			user.setIsAdmin(false);
			user.setStatus(UserStatus.ENABLED);
			userService.registerUser(user);
			user.setEmail(null);
			user.setPassword(null);
			user.setIsAdmin(false);
			user.setId(null);
		} catch (BusinessException b) {
			return "false"; // Se produjo algún error al validar
		}

		return "true"; // Es un usario normal
	}

	public String atras() {
		System.out.println("Pulsado atras");
		return "true";
	}
}