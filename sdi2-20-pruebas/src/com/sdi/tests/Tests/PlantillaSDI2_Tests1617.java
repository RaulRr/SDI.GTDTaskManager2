package com.sdi.tests.Tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.sdi.tests.pageobjects.PO_Form;
import com.sdi.tests.utils.SeleniumUtils;

//Ordenamos las pruebas por el nombre del método
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlantillaSDI2_Tests1617 {

	WebDriver driver;
	List<WebElement> elementos = null;

	public PlantillaSDI2_Tests1617() {
	}

	@Before
	public void run() {
		// Este código es para ejecutar con la versión portale de Firefox 46.0
		File pathToBinary = new File("S:\\firefox\\FirefoxPortable.exe");
		FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		driver = new FirefoxDriver(ffBinary, firefoxProfile);
		driver.get("http://localhost:8280/sdi2-20");
		// Este código es para ejecutar con una versión instalada de Firex 46.0
		// driver = new FirefoxDriver();
		// driver.get("http://localhost:8180/sdi2-n");
	}

	@After
	public void end() {
		// Cerramos el navegador
		driver.quit();
	}

	private void validarUsuario(String ulogin, String upassword) {

		// Estamos en el login
		// Vamos a rellenar el formulario
		new PO_Form().rellenaLogin(driver, ulogin, upassword);
	}

	private void registrarUsuario(String uLogin, String uMail,
			String uPassword, String uPasswordR) {

		// Estamos en el registro
		// Vamos a rellenar el formulario
		new PO_Form().rellenaRegsitro(driver, uLogin, uMail, uPassword,
				uPasswordR);
	}

	// PRUEBAS
	// ADMINISTRADOR
	// PR01: Autentificar correctamente al administrador.
	@Test
	public void prueba01() {
		// Estamos en el login-validamos al admin
		validarUsuario("admin1", "admin1");

		// Esperamos a que se cargue la pagina del admin
		// concretamente la tablalistado del admin
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:tablalistado", 10);
		// En el listado podremos ver al admin
		SeleniumUtils.textoPresentePagina(driver, "admin1");
		SeleniumUtils.textoPresentePagina(driver, "me@system.gtd");

	}

	// PR02: Fallo en la autenticación del administrador por introducir mal el
	// login.
	@Test
	public void prueba02() {
		// Estamos en el login-introducimos mal el login
		validarUsuario("admn", "admin1");

		// No accedemos al listado por tanto no podemos ver al admin1!
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "admin1", 10);
		SeleniumUtils.textoNoPresentePagina(driver, "me@system.gtd");
	}

	// PR03: Fallo en la autenticación del administrador por introducir mal la
	// password.
	@Test
	public void prueba03() {
		// Estamos en el login-introducimos mal la password
		validarUsuario("admin1", "adn");

		// No accedemos al listado por tanto no podemos eal admin1!
		SeleniumUtils.textoNoPresentePagina(driver, "me@system.gtd");
	}

	// PR04: Probar que la base de datos contiene los datos insertados con
	// conexión correcta a la base de datos.
	@Test
	public void prueba04() {

		assertTrue(false); // NO ESTA ACABADO!!!!

		// Estamos en el login-validamos al admin
		validarUsuario("admin1", "admin1");

		// Esperamos a que se cargue la pagina del admin
		// concretamente al link de iniciar la BD
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:reiniciarbd", 10);

		// Pulsamos sobre el enlace
		By link = By.id("form-template:form-admin:reiniciarbd");
		driver.findElement(link).click();
	}

	// PR05: Visualizar correctamente la lista de usuarios normales.
	@Test
	public void prueba05() {
		// Estamos en el login-validamos como admin
		validarUsuario("admin1", "admin1");

		// Esperamos a que se cargue la pagina del admin
		// concretamente la tabla listado usuarios del admin
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:tablalistado", 10);

		// Comprobamos que existen los usuarios por el login e email
		SeleniumUtils.textoPresentePagina(driver, "user1");
		SeleniumUtils.textoPresentePagina(driver, "user1@gmail.com");
		SeleniumUtils.textoPresentePagina(driver, "user2");
		SeleniumUtils.textoPresentePagina(driver, "user2@gmail.com");
		SeleniumUtils.textoPresentePagina(driver, "user3");
		SeleniumUtils.textoPresentePagina(driver, "user3@gmail.com");
	}

	// PR06: Cambiar el estado de un usuario de ENABLED a DISABLED. Y tratar de
	// entrar con el usuario que se desactivado.
	@Test
	public void prueba06() {
		// Estamos en el login-validamos como admin
		validarUsuario("admin1", "admin1");

		// Esperamos a que se cargue la pagina del admin
		// concretamente la tabla listado usuarios del admin
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-admin:tablalistado", 10);

		// Comprobamos que existe el user a cambiar
		SeleniumUtils.textoPresentePagina(driver, "user2");
		SeleniumUtils.textoPresentePagina(driver, "user2@gmail.com");

		// Pulsamos sobre el hiperbinculo de estado del usuario que se encuantra
		// enabled
		By enlace = By
				.xpath("//td[contains(text(), 'user2@gmail.com')]/following-sibling::*/a[contains(@id, 'editarstatus')]");
		driver.findElement(enlace).click();// Ahora estaria disabled

		// Volvemos al login
		enlace = By.xpath("//a[contains(@id, 'form-template:cerrarsesion')]");
		driver.findElement(enlace).click();// Ahora estariamos en la ventana
											// login

		// Intentamos acceder como el usuario
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-login", 10);
		validarUsuario("user2", "user2");

		// Seguimos en login. No accedimos al listado tareas del usuario.
		// Buscamos el boton de validar
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-login:validation-button", 10);

		// Deshacemos los cambios
		validarUsuario("admin1", "admin1");
		enlace = By
				.xpath("//td[contains(text(), 'user2@gmail.com')]/following-sibling::*/a[contains(@id, 'editarstatus')]");
		driver.findElement(enlace).click();// Ahora volveria a estar enabled
	}

	// PR07: Cambiar el estado de un usuario a DISABLED a ENABLED. Y Y tratar de
	// entrar con el usuario que se ha activado.
	@Test
	public void prueba07() {
		// Estamos en el login-validamos como admin
		validarUsuario("admin1", "admin1");

		// Esperamos a que se cargue la pagina del admin
		// concretamente la tabla listado usuarios del admin
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-admin:tablalistado", 10);

		// Comprobamos que existe el user a cambiar
		SeleniumUtils.textoPresentePagina(driver, "user3");
		SeleniumUtils.textoPresentePagina(driver, "user3@gmail.com");

		// Editamos el estado del usuario que se encuantra enabled a disabled
		By enlace = By
				.xpath("//td[contains(text(), 'user3@gmail.com')]/following-sibling::*/a[contains(@id, 'editarstatus')]");
		driver.findElement(enlace).click();// Ahora estaria disabled
		enlace = By
				.xpath("//td[contains(text(), 'user3@gmail.com')]/following-sibling::*/a[contains(@id, 'editarstatus')]");
		driver.findElement(enlace).click();// Ahora volveria a estar enabled

		enlace = By.xpath("//a[contains(@id, 'form-template:cerrarsesion')]");
		driver.findElement(enlace).click();// Ahora estariamos en la ventana
											// login

		// Intentamos acceder como el usuario
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-login", 10);
		validarUsuario("user3", "user3");

		// Esperamos a que se cargue la pagina del usuario
		// concretamente la tabla listado tareas del user
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);
	}

	// PR08: Ordenar por Login
	@Test
	public void prueba08() {
		// Estamos en el login-validamos como admin
		validarUsuario("admin1", "admin1");

		// Obtenemos la lista de usuarios ordenados inicialmente
		List<WebElement> logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		assertTrue(logins.get(0).getText().equals("admin1"));
		assertTrue(logins.get(1).getText().equals("user1"));
		assertTrue(logins.get(2).getText().equals("user2"));
		assertTrue(logins.get(3).getText().equals("user3"));

		By button = By.xpath("//th[contains(@id, 'ordenar_login')]");
		driver.findElement(button).click();// El primero no ordena
		SeleniumUtils.EsperaCargaPaginaxpath(driver,
				"//th[contains(@id, 'ordenar_login')]", 10);
		button = By.xpath("//th[contains(@id, 'ordenar_login')]");
		driver.findElement(button).click();// Este cambio el orden por logins
		SeleniumUtils.EsperaCargaPaginaxpath(driver,
				"//th[contains(@id, 'ordenar_login')]", 10);

		// Obtenemos la nueva ordenacion
		logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		System.out.println(logins.get(0).getText());
		assertTrue(logins.get(0).getText().equals("user3"));
		assertTrue(logins.get(1).getText().equals("user2"));
		assertTrue(logins.get(2).getText().equals("user1"));
		assertTrue(logins.get(3).getText().equals("admin1"));

		// Volvemos al original
		button = By.xpath("//th[contains(@id, 'ordenar_login')]");
		driver.findElement(button).click();// Este cambio el orden por logins
		SeleniumUtils.EsperaCargaPaginaxpath(driver,
				"//th[contains(@id, 'ordenar_login')]", 10);

		logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		assertTrue(logins.get(0).getText().equals("admin1"));
		assertTrue(logins.get(1).getText().equals("user1"));
		assertTrue(logins.get(2).getText().equals("user2"));
		assertTrue(logins.get(3).getText().equals("user3"));

	}

	// PR09: Ordenar por Email
	@Test
	public void prueba09() {
		// Estamos en el login-validamos como admin
		validarUsuario("admin1", "admin1");

		// Obtenemos la lista de usuarios ordenados inicialmente
		List<WebElement> logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		assertTrue(logins.get(0).getText().equals("admin1"));
		assertTrue(logins.get(1).getText().equals("user1"));
		assertTrue(logins.get(2).getText().equals("user2"));
		assertTrue(logins.get(3).getText().equals("user3"));

		By button = By.xpath("//th[contains(@id, 'ordenar_email')]");
		driver.findElement(button).click();// El primero no ordena
		SeleniumUtils.EsperaCargaPaginaxpath(driver,
				"//th[contains(@id, 'ordenar_email')]", 10);
		button = By.xpath("//th[contains(@id, 'ordenar_email')]");
		driver.findElement(button).click();// Este cambio el orden por emails
		SeleniumUtils.EsperaCargaPaginaxpath(driver,
				"//th[contains(@id, 'ordenar_email')]", 10);

		// Obtenemos la nueva ordenacion
		logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		System.out.println(logins.get(0).getText());
		assertTrue(logins.get(0).getText().equals("user3"));
		assertTrue(logins.get(1).getText().equals("user2"));
		assertTrue(logins.get(2).getText().equals("user1"));
		assertTrue(logins.get(3).getText().equals("admin1"));

		// Volvemos al original
		button = By.xpath("//th[contains(@id, 'ordenar_email')]");
		driver.findElement(button).click();// Este cambio el orden por emails
		SeleniumUtils.EsperaCargaPaginaxpath(driver,
				"//th[contains(@id, 'ordenar_email')]", 10);

		logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		assertTrue(logins.get(0).getText().equals("admin1"));
		assertTrue(logins.get(1).getText().equals("user1"));
		assertTrue(logins.get(2).getText().equals("user2"));
		assertTrue(logins.get(3).getText().equals("user3"));
	}

	// PR10: Ordenar por Status
	@Test
	public void prueba10() {
		// Estamos en el login-validamos como admin
		validarUsuario("admin1", "admin1");

		// Pasamos al user2 de enabled a disabled
		By enlace = By
				.xpath("//td[contains(text(), 'user2@gmail.com')]/following-sibling::*/a[contains(@id, 'editarstatus')]");
		driver.findElement(enlace).click();// Ahora estaria disabled
		SeleniumUtils
				.EsperaCargaPaginaxpath(
						driver,
						"//td[contains(text(), 'user2@gmail.com')]/following-sibling::*/a[contains(@id, 'editarstatus')]",
						10);

		// Obtenemos la lista de usuarios ordenados inicialmente
		List<WebElement> logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		assertTrue(logins.get(0).getText().equals("admin1"));
		assertTrue(logins.get(1).getText().equals("user1"));
		assertTrue(logins.get(2).getText().equals("user2"));
		assertTrue(logins.get(3).getText().equals("user3"));

		By button = By.xpath("//th[contains(@id, 'ordenar_status')]");
		driver.findElement(button).click();// El primero ordena
		SeleniumUtils.EsperaCargaPaginaxpath(driver,
				"//th[contains(@id, 'ordenar_status')]", 10);

		// Obtenemos la nueva ordenacion-Ultimos disabled
		logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		assertTrue(logins.get(0).getText().equals("admin1"));
		assertTrue(logins.get(1).getText().equals("user1"));
		assertTrue(logins.get(2).getText().equals("user3"));
		assertTrue(logins.get(3).getText().equals("user2"));

		// Volvemos a pinchar-Primeros disabled
		button = By.xpath("//th[contains(@id, 'ordenar_status')]");
		driver.findElement(button).click();// Este cambio el orden por status
		SeleniumUtils.EsperaCargaPaginaxpath(driver,
				"//th[contains(@id, 'ordenar_status')]", 10);

		logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		assertTrue(logins.get(0).getText().equals("user2"));
		assertTrue(logins.get(1).getText().equals("admin1"));
		assertTrue(logins.get(2).getText().equals("user1"));
		assertTrue(logins.get(3).getText().equals("user3"));

		// Pasamos al user2 a enabled
		enlace = By
				.xpath("//td[contains(text(), 'user2@gmail.com')]/following-sibling::*/a[contains(@id, 'editarstatus')]");
		driver.findElement(enlace).click();// Ahora estaria enabled
	}

	// PR11: Borrar una cuenta de usuario normal y datos relacionados.
	@Test
	public void prueba11() throws InterruptedException {
		// Estamos en el login-validamos como admin
		validarUsuario("admin1", "admin1");

		// Esperamos a que se cargue la pagina del admin
		// concretamente la tablalistado del admin
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:tablalistado", 10);
		// Comprobamos que existe el user a eliminar
		SeleniumUtils.textoPresentePagina(driver, "user3");
		SeleniumUtils.textoPresentePagina(driver, "user3@gmail.com");

		By button = By
				.xpath("//td[contains(text(), 'user3@gmail.com')]/following-sibling::*/button[contains(@id, 'eliminar_button')]");
		driver.findElement(button).click();// Ahora viene confirmacion
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:tablalistado:3:comfirmation_button",
				10);
		button = By
				.xpath("//button[contains(@id, 'form-template:form-admin:tablalistado:3:comfirmation_button')]");
		driver.findElement(button).click();// Boton confirmacion

		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:tablalistado", 10);

		// Comprobamos que se elimino
		SeleniumUtils.textoNoPresentePagina(driver, "user3");
		SeleniumUtils.textoNoPresentePagina(driver, "user3@gmail.com");

		// Devolvemos al estado inicial
		Thread.sleep(1000);
		button = By.xpath("//a[contains(@id, 'form-template:cerrarsesion')]");
		driver.findElement(button).click();// Pulsamos sobre cerrar sesion
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-login", 10);
		validarUsuario("admin1", "admin1");
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:reiniciarbd", 10);
		button = By
				.xpath("//a[contains(@id, 'form-template:form-admin:reiniciarbd')]");
		driver.findElement(button).click();
	}
	// PR12: Crear una cuenta de usuario normal con datos válidos.
	@Test
	public void prueba12() throws InterruptedException {
		By button = By
				.xpath("//input[contains(@id, 'form-template:form-login:crear-button')]");
		driver.findElement(button).click();// Pulsamos boton de creacion

		//esperamos a que se cargue la pagina de registro
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-registro", 10);
		
		//Registramos y confirmamos
		registrarUsuario("uprueba1", "uprueba1@gmail.com", "uprueba1", "uprueba1");
	
		//volvemos al login
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-login", 10);
		
		//Comprobamos en admin y devolvemos a estado inicial
		validarUsuario("admin1", "admin1");
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:tablalistado", 10);
		// Comprobamos que existe el nuevo usuario
		SeleniumUtils.textoPresentePagina(driver, "uprueba1");
		SeleniumUtils.textoPresentePagina(driver, "uprueba1@gmail.com");
		
		button = By
				.xpath("//td[contains(text(), 'uprueba1@gmail.com')]/following-sibling::*/button[contains(@id, 'eliminar_button')]");
		driver.findElement(button).click();// Ahora viene confirmacion
		Thread.sleep(1000);
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:tablalistado:4:comfirmation_button",
				10);
		button = By
				.xpath("//button[contains(@id, 'form-template:form-admin:tablalistado:4:comfirmation_button')]");
		driver.findElement(button).click();// Boton confirmacion eliminacion
	}
	// PR13: Crear una cuenta de usuario normal con login repetido.
	@Test
	public void prueba13() {
		By button = By
				.xpath("//input[contains(@id, 'form-template:form-login:crear-button')]");
		driver.findElement(button).click();// Pulsamos boton de creacion

		//esperamos a que se cargue la pagina de registro
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-registro", 10);
		
		//Registramos con login repetido
		registrarUsuario("user1", "uprueba1@gmail.com", "uprueba1", "uprueba1");
		
		//Seguimos en el registro
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-registro", 10);
	}
	// PR14: Crear una cuenta de usuario normal con Email incorrecto.
	@Test
	public void prueba14() {
		By button = By
				.xpath("//input[contains(@id, 'form-template:form-login:crear-button')]");
		driver.findElement(button).click();// Pulsamos boton de creacion

		//esperamos a que se cargue la pagina de registro
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-registro", 10);
		
		//Registramos con mail incorrecto
		registrarUsuario("uprueba1", "uprueba1", "uprueba1", "uprueba1");
		
		//Seguimos en el registro
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-registro", 10);
	}

	// PR15: Crear una cuenta de usuario normal con Password incorrecta.
	@Test
	public void prueba15() {
		By button = By
				.xpath("//input[contains(@id, 'form-template:form-login:crear-button')]");
		driver.findElement(button).click();// Pulsamos boton de creacion

		//esperamos a que se cargue la pagina de registro
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-registro", 10);
		
		//Registramos con mail incorrecto
		registrarUsuario("uprueba1", "uprueba1", "1234", "1234");
		
		//Seguimos en el registro
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-registro", 10);
	}

	// USUARIO
	// PR16: Comprobar que en Inbox sólo aparecen listadas las tareas sin
	// categoría y que son las que tienen que. Usar paginación navegando por las
	// tres páginas.
	@Test
	public void prueba16() {
		assertTrue(false);
	}

	// PR17: Funcionamiento correcto de la ordenación por fecha planeada.
	@Test
	public void prueba17() {
		assertTrue(false);
	}

	// PR18: Funcionamiento correcto del filtrado.
	@Test
	public void prueba18() {
		assertTrue(false);
	}

	// PR19: Funcionamiento correcto de la ordenación por categoría.
	@Test
	public void prueba19() {
		assertTrue(false);
	}

	// PR20: Funcionamiento correcto de la ordenación por fecha planeada.
	@Test
	public void prueba20() {
		assertTrue(false);
	}

	// PR21: Comprobar que las tareas que no están en rojo son las de hoy y
	// además las que deben ser.
	@Test
	public void prueba21() {
		assertTrue(false);
	}

	// PR22: Comprobar que las tareas retrasadas están en rojo y son las que
	// deben ser.
	@Test
	public void prueba22() {
		assertTrue(false);
	}

	// PR23: Comprobar que las tareas de hoy y futuras no están en rojo y que
	// son las que deben ser.
	@Test
	public void prueba23() {
		assertTrue(false);
	}

	// PR24: Funcionamiento correcto de la ordenación por día.
	@Test
	public void prueba24() {
		assertTrue(false);
	}

	// PR25: Funcionamiento correcto de la ordenación por nombre.
	@Test
	public void prueba25() {
		assertTrue(false);
	}

	// PR26: Confirmar una tarea, inhabilitar el filtro de tareas terminadas, ir
	// a la pagina donde está la tarea terminada y comprobar que se muestra.
	@Test
	public void prueba26() {
		assertTrue(false);
	}

	// PR27: Crear una tarea sin categoría y comprobar que se muestra en la
	// lista Inbox.
	@Test
	public void prueba27() {
		assertTrue(false);
	}

	// PR28: Crear una tarea con categoría categoria1 y fecha planeada Hoy y
	// comprobar que se muestra en la lista Hoy.
	@Test
	public void prueba28() {
		assertTrue(false);
	}

	// PR29: Crear una tarea con categoría categoria1 y fecha planeada posterior
	// a Hoy y comprobar que se muestra en la lista Semana.
	@Test
	public void prueba29() {
		assertTrue(false);
	}

	// PR30: Editar el nombre, y categoría de una tarea (se le cambia a
	// categoría1) de la lista Inbox y comprobar que las tres pseudolista se
	// refresca correctamente.
	@Test
	public void prueba30() {
		assertTrue(false);
	}

	// PR31: Editar el nombre, y categoría (Se cambia a sin categoría) de una
	// tarea de la lista Hoy y comprobar que las tres pseudolistas se refrescan
	// correctamente.
	@Test
	public void prueba31() {
		assertTrue(false);
	}

	// PR32: Marcar una tarea como finalizada. Comprobar que desaparece de las
	// tres pseudolistas.
	@Test
	public void prueba32() {
		assertTrue(false);
	}

	// PR33: Salir de sesión desde cuenta de administrador.
	@Test
	public void prueba33() {
		// Estamos en el login-validamos al admin
		validarUsuario("admin1", "admin1");

		// Esperamos a que se cargue la pagina del admin
		// concretamente a la tabla de usuarios
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:tablalistado", 10);

		// En el listado podremos ver al admin
		SeleniumUtils.textoPresentePagina(driver, "admin1");
		SeleniumUtils.textoPresentePagina(driver, "me@system.gtd");

		// Cerramos sesion para volver al login
		By button = By
				.xpath("//a[contains(@id, 'form-template:cerrarsesion')]");
		driver.findElement(button).click();// Pulsamos sobre cerrar sesion

		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-login", 10);
		SeleniumUtils.textoNoPresentePagina(driver, "admin1");
		SeleniumUtils.textoNoPresentePagina(driver, "me@system.gtd");
	}

	// PR34: Salir de sesión desde cuenta de usuario normal.
	@Test
	public void prueba34() {
		// Estamos en el login-validamos al user1
		validarUsuario("user1", "user1");

		// Esperamos a que se cargue la pagina del user
		// concretamente a la tabla de tareas
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Al principio estamos en inbox
		SeleniumUtils.textoPresentePagina(driver, "Inbox");

		// Cerramos sesion para volver al login
		By button = By
				.xpath("//a[contains(@id, 'form-template:cerrarsesion')]");
		driver.findElement(button).click();// Pulsamos sobre cerrar sesion

		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-login", 10);

		SeleniumUtils.textoNoPresentePagina(driver, "Inbox");
	}
	// PR35: Cambio del idioma por defecto a un segundo idioma. (Probar algunas
	// vistas)
	@Test
	public void prueba35() {
		//Desde la ventana de login comprobamos textos español al inicio
		SeleniumUtils.textoPresentePagina(driver, "Usuario");
		SeleniumUtils.textoPresentePagina(driver, "Contraseña");
		SeleniumUtils.textoPresentePagina(driver, "ESPAÑOL");
		SeleniumUtils.textoPresentePagina(driver, "INGLES");
		
		SeleniumUtils.ClickSubopcionMenuHover(driver, 
				"form-template:idioma-menu", "form-template:en-menu");
		SeleniumUtils.EsperaCargaPagina(driver, "id", 
				"form-template:form-login", 10);
		
		//Ahora el login esta localizado
		SeleniumUtils.textoPresentePagina(driver, "Login");
		SeleniumUtils.textoPresentePagina(driver, "Password");
		SeleniumUtils.textoPresentePagina(driver, "SPANISH");
		SeleniumUtils.textoPresentePagina(driver, "ENGLISH");
		
		//Probamos la vista de admin este localizado a en
		validarUsuario("admin1", "admin1");
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:tablalistado", 10);
		
		SeleniumUtils.textoPresentePagina(driver, "Users List");
		SeleniumUtils.textoPresentePagina(driver, "Login");
		SeleniumUtils.textoPresentePagina(driver, "Email");
		SeleniumUtils.textoPresentePagina(driver, "Status");
		SeleniumUtils.textoPresentePagina(driver, "Delete");
	}
	// PR36: Cambio del idioma por defecto a un segundo idioma y vuelta al
	// idioma por defecto. (Probar algunas vistas)
	@Test
	public void prueba36() {
		//Principio en el login en español pasamos a en
		SeleniumUtils.ClickSubopcionMenuHover(driver, 
				"form-template:idioma-menu", "form-template:en-menu");
		SeleniumUtils.EsperaCargaPagina(driver, "id", 
				"form-template:form-login", 10);
		
		//Ahora el login esta localizado
		SeleniumUtils.textoPresentePagina(driver, "Login");
		SeleniumUtils.textoPresentePagina(driver, "Password");
		SeleniumUtils.textoPresentePagina(driver, "SPANISH");
		SeleniumUtils.textoPresentePagina(driver, "ENGLISH");
		
		//Volvemos a es
		SeleniumUtils.ClickSubopcionMenuHover(driver, 
				"form-template:idioma-menu", "form-template:es-menu");
		SeleniumUtils.EsperaCargaPagina(driver, "id", 
				"form-template:form-login", 10);
		
		//Ahora el login esta localizado
		SeleniumUtils.textoNoPresentePagina(driver, "Login");
		SeleniumUtils.textoPresentePagina(driver, "Usuario");
		SeleniumUtils.textoNoPresentePagina(driver, "Password");
		SeleniumUtils.textoPresentePagina(driver, "Contraseña");
		SeleniumUtils.textoNoPresentePagina(driver, "SPANISH");
		SeleniumUtils.textoPresentePagina(driver, "ESPAÑOL");
		SeleniumUtils.textoNoPresentePagina(driver, "ENGLISH");
		SeleniumUtils.textoPresentePagina(driver, "INGLES");
		
		//vplvemos localizar a en
		SeleniumUtils.ClickSubopcionMenuHover(driver, 
				"form-template:idioma-menu", "form-template:en-menu");
		SeleniumUtils.EsperaCargaPagina(driver, "id", 
				"form-template:form-login", 10);
		
		//Probamos en la tablaTareas de un user
		validarUsuario("user1","user1");
		SeleniumUtils.EsperaCargaPagina(driver, "id", 
				"form-template:form-task:tablalistado",10);
		
		SeleniumUtils.textoPresentePagina(driver, "Today:");
		SeleniumUtils.textoPresentePagina(driver, "Week:");
		
		//Localizamos a es
		SeleniumUtils.ClickSubopcionMenuHover(driver, 
				"form-template:idioma-menu", "form-template:es-menu");
		SeleniumUtils.EsperaCargaPagina(driver, "id", 
				"form-template:form-task", 10);
		
		SeleniumUtils.textoNoPresentePagina(driver, "Today:");
		SeleniumUtils.textoPresentePagina(driver, "Hoy:");
		SeleniumUtils.textoNoPresentePagina(driver, "Week:");
		SeleniumUtils.textoPresentePagina(driver, "Semana:");
	}
	// PR37: Intento de acceso a un URL privado de administrador con un usuario
	// autenticado como usuario normal.
	@Test
	public void prueba37() {
		//Autentificamos como usuario
		validarUsuario("user1", "user1");
		
		// Esperamos a que se cargue la pagina del user1
		// concretamente la tabla de task
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);
		
		//Tratamos de acceder a un url del admin
		driver.
			get("http://localhost:8280/sdi2-20/restricted/listaUsuarios.xhtml");
		//driver.
			//get("http://localhost:8180/sdi2-20/restricted/listaUsuarios.xhtml");
		
		//Se carga una página de acceso restringido
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-error", 10);
		driver.getCurrentUrl().
			equals("http://localhost:8280/sdi2-20/error.xhtml");
		//driver.getCurrentUrl().
			//equals("http://localhost:8180/sdi2-20/error.xhtml");
	}
	// PR38: Intento de acceso a un URL privado de usuario normal con un usuario
	// no autenticado.
	@Test
	public void prueba38() {
		//Tratamos de acceder a un url de usuario como anonimo
		driver.
		get("http://localhost:8280/sdi2-20/usuarios/listaTareas.xhtml");
		//driver.
		//get("http://localhost:8180/sdi2-20/usuarios/listaTareas.xhtml");

		//Se carga una página de acceso restringido
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-error", 10);
		driver.getCurrentUrl().
		equals("http://localhost:8280/sdi2-20/error.xhtml");
		//driver.getCurrentUrl().
		//equals("http://localhost:8180/sdi2-20/error.xhtml");
	}
}