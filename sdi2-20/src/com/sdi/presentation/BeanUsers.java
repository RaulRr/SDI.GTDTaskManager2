package com.sdi.presentation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;

import alb.util.log.Log;
import alb.util.log.LogLevel;

import com.sdi.business.AdminService;
import com.sdi.business.Services;
import com.sdi.business.TaskService;
import com.sdi.business.UserService;
import com.sdi.business.exception.BusinessException;
import com.sdi.dto.Category;
import com.sdi.dto.User;
import com.sdi.dto.types.UserStatus;

@ManagedBean(name = "controller")
@SessionScoped
public class BeanUsers implements Serializable {

	private static final long serialVersionUID = 55555L;

	@ManagedProperty(value = "#{user}")
	private BeanUser user;

	private List<User> users = null;

	@ManagedProperty(value = "#{btasks}")
	private BeanTasks tasks = null;

	private String pass = "";

	// Se inicia correctamente el MBean inyectado si JSF lo hubiera crea
	// y en caso contrario se crea. (hay que tener en cuenta que es un Bean de
	// sesión)
	// Se usa @PostConstruct, ya que en el contructor no se sabe todavía si el
	// ManagedBean
	// ya estaba construido y en @PostConstruct SI.

	@PostConstruct
	public void init() {
		Log.setLogLevel(LogLevel.DEBUG);

		Log.debug("BeanUsers - PostConstruct");
		// Buscamos el alumno en la sesión. Esto es un patrón factoría
		// claramente.
		user = (BeanUser) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get(new String("user"));
		// si no existe lo creamos e inicializamos
		if (user == null) {
			Log.debug("BeanUser - No existia");
			user = new BeanUser();
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().put("user", user);
		}
	}

	@PreDestroy
	public void end() {
		Log.debug("BeanUsers - PreDestroy");
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

	public BeanTasks getTasks() {
		return tasks;
	}

	public void setTasks(BeanTasks tasks) {
		this.tasks = tasks;
	}

	/**
	 * Método validación de usuarios de nuestra aplicación
	 * 
	 * @return String admin, error, user
	 */
	public String validar() {
		UserService userService;

		try {
			userService = Services.getUserService();
			user.setUser(userService.findLoggableUser(user.getLogin(),
					user.getPassword()));
			user.setPassword(null);// Por seguridad

			if (user.getIsAdmin()) { // Si es admin muestra la lista usuarios
				listadoUsuarios();
				putUserInSession(user);
				Log.debug("Se ha validado como admin");
				return "admin";
			}

		} catch (Exception e) {
			Log.error("Intento fallido de validación");
			user.iniciaUser(null);
			FacesContext.getCurrentInstance().addMessage(
					"mensaje",
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn",
							"User or password incorrect"));
			return "error"; // Se produjo algún error al validar
		}
		putUserInSession(user);
		Log.debug("Se ha validado como usuario");
		return "user"; // Es un usario normal
	}

	/**
	 * Metodo que permite al administrador activar/bloquear a un usuario
	 * 
	 * @param user
	 * @return String exito,error
	 */
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
			Log.debug("Estado del usuario [%s] cambiado con exito a [%s]",
					user.getLogin(), user.getStatus().toString());
			FacesContext.getCurrentInstance().addMessage(
					"mensaje",
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
							"State changed"));
			return "exito"; // Nos volvemos al listado
		} catch (BusinessException b) {
			Log.error("Se ha producido algun error al tratar de cambiar el"
					+ "estado de un usuario");
			return "error";
		}
	}

	/**
	 * Metodo que permite al administrador eliminar a un usuario junto con todas
	 * sus tareas y categorias
	 * 
	 * @param user
	 * @return String exito,error
	 */
	public String eliminar(User user) {
		AdminService adminService;
		if (user == null) {
			return "error";
		}
		try {
			adminService = Services.getAdminService();
			adminService.deepDeleteUser(user.getId());
			listadoUsuarios();
			Log.debug("Se ha eliminado al usuario [%s] con exito",
					user.getLogin());
			FacesContext.getCurrentInstance().addMessage(
					"mensaje",
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
							"User correctly deleted"));
			return "exito"; // Nos volvemos al listado
		} catch (BusinessException b) {
			Log.error("Se ha producido algun error al tratar de eliminar un"
					+ "usuario");
			FacesContext.getCurrentInstance().addMessage(
					"mensaje",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
							"User not deleted"));
			return "error";
		}
	}

	public String registro() {
		return "true";
	}

	public String registrar() {
		UserService userService;

		if (!user.getEmail().matches("[-\\w\\.]+@\\w+\\.\\w+")) {
			Log.error("Email invalido: [%s]", user.getEmail());
			FacesContext.getCurrentInstance().addMessage(
					"mensaje",
					new FacesMessage(FacesMessage.SEVERITY_WARN, "War",
							"Invalid Email"));
			return "false";
		}

		if (!pass.equals(user.getPassword())) {
			Log.error("Las contraseñas no coinciden: [%s] - [%s]", pass,
					user.getPassword());
			FacesContext.getCurrentInstance().addMessage(
					"mensaje",
					new FacesMessage(FacesMessage.SEVERITY_WARN, "War",
							"The two password must match"));
			return "false";
		}

		else {
			if (pass.length() < 8) {
				Log.error("Las contraseñas deben medir al menos 8 caracteres "
						+ "[%s]", pass);
				FacesContext.getCurrentInstance().addMessage(
						"mensaje",
						new FacesMessage(FacesMessage.SEVERITY_WARN, "War",
								"The password must be at least 8 caracteres"));
				return "false";
			}

			if (!pass.matches(".*[a-zA-Z].*") || !pass.matches(".*[0-9].*")) {
				Log.error("Las contraseñas no contiene letras y numeros [%s]",
						pass);
				FacesContext
						.getCurrentInstance()
						.addMessage(
								"mensaje",
								new FacesMessage(FacesMessage.SEVERITY_WARN,
										"War",
										"The password must contain letters and numbers"));
				return "false";
			}
		}

		try {
			userService = Services.getUserService();
			user.setIsAdmin(false);
			user.setStatus(UserStatus.ENABLED);
			userService.registerUser(user);

			// Vaciamos el bean
			User u = userService.findLoggableUser(user.getLogin(),
					user.getPassword());

			// Procedemos a crear las categorías
			TaskService t = Services.getTaskService();
			Category c = new Category();
			c.setName("categoria1");
			c.setUserId(u.getId());
			t.createCategory(c);
			c.setName("categoria2");
			c.setUserId(u.getId());
			t.createCategory(c);
			c.setName("categoria3");
			c.setUserId(u.getId());
			t.createCategory(c);

			// Vaciamos el bean
			user.iniciaUser(null);
		} catch (BusinessException b) {
			FacesContext.getCurrentInstance().addMessage(
					"mensaje",
					new FacesMessage(FacesMessage.SEVERITY_WARN, "War",
							"User already registered"));
			return "false"; // Se produjo algún error al crear usuario
		}
		FacesContext.getCurrentInstance().addMessage(
				"mensaje",
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
						"User registered"));
		return "true"; // Usuario creado correctamente
	}

	/**
	 * Método del administrador que inicaliza la BD a su estado original
	 * 
	 * @return String error, exito
	 */
	public String reiniciarBD() {
		AdminService adminService;
		try {
			adminService = Services.getAdminService();
			adminService.initiateDB();
			listadoUsuarios();

		} catch (BusinessException b) {
			Log.error("Algo ha ocurrido al iniciar la BD");
			FacesContext.getCurrentInstance().addMessage(
					"mensaje",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
							"BBDD not correct reset"));
			return "error";
		}
		Log.debug("BD iniciada con exito");
		FacesContext.getCurrentInstance().addMessage(
				"mensaje",
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
						"BBDD correct reset"));
		return "exito";

	}

	/**
	 * Nos devuelve a la pag. anterior
	 * 
	 * @return boolean
	 */
	public String atras() {
		Log.debug("Pulsado atras");
		return "true";
	}

	/**
	 * Actualiza la lista de usuarios del admin
	 * 
	 * @throws BusinessException
	 */
	private void listadoUsuarios() throws BusinessException {
		users = Services.getAdminService().findAllUsers();
	}

	private void putUserInSession(User user) {
		Map<String, Object> session = FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap();
		session.put("LOGGEDIN_USER", user.getIsAdmin());
	}

	public String cerrarSesion() {
		Map<String, Object> session = FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap();
		session.put("LOGGEDIN_USER", null);
		FacesContext.getCurrentInstance().getExternalContext()
				.invalidateSession();
		Log.debug("Sesion cerrada correctamente");
		FacesContext.getCurrentInstance().addMessage(
				"mensaje",
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
						"Session closed"));
		return "true";
	}

}