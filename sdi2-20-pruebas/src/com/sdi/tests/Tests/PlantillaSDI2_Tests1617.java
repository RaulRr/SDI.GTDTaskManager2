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

import alb.util.date.DateUtil;

import com.sdi.business.Services;
import com.sdi.business.exception.BusinessException;
import com.sdi.tests.pageobjects.PO_Form;
import com.sdi.tests.utils.SeleniumUtils;

//Ordenamos las pruebas por el nombre del método
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlantillaSDI2_Tests1617 {

	WebDriver driver;
	List<WebElement> elementos = null;
	boolean prepararBD = true;

	public PlantillaSDI2_Tests1617() {
	}

	@Before
	public void run() {
		
		compobarBD();
		
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

	private void compobarBD() {
		if(prepararBD){
			try {
				Services.getAdminService().initiateDB();
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			prepararBD=false;
		}
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

		// No accedemos al listado por tanto seguimos en el index
		driver.getCurrentUrl().equals(
				"http://localhost:8280/sdi2-20/index.xhtml");
	}

	// PR03: Fallo en la autenticación del administrador por introducir mal la
	// password.
	@Test
	public void prueba03() {
		// Estamos en el login-introducimos mal la password
		validarUsuario("admin1", "adn");

		// No accedemos al listado por tanto seguimos en el index
		driver.getCurrentUrl().equals(
				"http://localhost:8280/sdi2-20/index.xhtml");
	}

	// PR04: Probar que la base de datos contiene los datos insertados con
	// conexión correcta a la base de datos.
	@Test
	public void prueba04() throws InterruptedException {
		// Estamos en el login-validamos al admin
		validarUsuario("admin1", "admin1");

		// Esperamos a que se cargue la pagina del admin
		// concretamente al link de iniciar la BD
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:reiniciarbd", 10);

		List<WebElement> ids = driver.findElements(By
				.xpath("//span[contains(@id, 'td_id')]"));
		int oldUser = Integer.parseInt(ids.get(3).getText());

		// Pulsamos sobre el enlace
		By link = By.id("form-template:form-admin:reiniciarbd");
		driver.findElement(link).click();

		// Esperamos que actualice la lsita de usuarios
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:reiniciarbd", 10);
		Thread.sleep(1000);
		ids = driver.findElements(By.xpath("//span[contains(@id, 'td_id')]"));
		int newUser = Integer.parseInt(ids.get(1).getText());

		// El ID del primer usuario es 1 mayor que el antiguo ID del ultimo
		// usuario
		assertTrue(oldUser + 1 == newUser);
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
				.xpath("//td[contains(text(), 'user2@gmail.com')]/following-"
						+ "sibling::*/a[contains(@id, 'editarstatus')]");
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

		// Deshacemos los cambios desde selenium
		validarUsuario("admin1", "admin1");
		enlace = By
				.xpath("//td[contains(text(), 'user2@gmail.com')]/following-"
						+ "sibling::*/a[contains(@id, 'editarstatus')]");
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
				.xpath("//td[contains(text(), 'user3@gmail.com')]/following-"
						+ "sibling::*/a[contains(@id, 'editarstatus')]");
		driver.findElement(enlace).click();// Ahora estaria disabled
		enlace = By
				.xpath("//td[contains(text(), 'user3@gmail.com')]/following-"
						+ "sibling::*/a[contains(@id, 'editarstatus')]");
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
	public void prueba08() throws InterruptedException {
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
		Thread.sleep(500);
		
		button = By.xpath("//th[contains(@id, 'ordenar_login')]");
		driver.findElement(button).click();// Este cambio el orden por logins
		Thread.sleep(500);

		// Obtenemos la nueva ordenacion
		logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		assertTrue(logins.get(0).getText().equals("user3"));
		assertTrue(logins.get(1).getText().equals("user2"));
		assertTrue(logins.get(2).getText().equals("user1"));
		assertTrue(logins.get(3).getText().equals("admin1"));

		// Volvemos al original
		button = By.xpath("//th[contains(@id, 'ordenar_login')]");
		driver.findElement(button).click();// Este cambio el orden por logins
		Thread.sleep(500);

		logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		assertTrue(logins.get(0).getText().equals("admin1"));
		assertTrue(logins.get(1).getText().equals("user1"));
		assertTrue(logins.get(2).getText().equals("user2"));
		assertTrue(logins.get(3).getText().equals("user3"));

	}

	// PR09: Ordenar por Email
	@Test
	public void prueba09() throws InterruptedException {
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
		Thread.sleep(500);
		
		button = By.xpath("//th[contains(@id, 'ordenar_email')]");
		driver.findElement(button).click();// Este cambio el orden por emails
		Thread.sleep(500);

		// Obtenemos la nueva ordenacion
		logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		assertTrue(logins.get(0).getText().equals("user3"));
		assertTrue(logins.get(1).getText().equals("user2"));
		assertTrue(logins.get(2).getText().equals("user1"));
		assertTrue(logins.get(3).getText().equals("admin1"));

		// Volvemos al original
		button = By.xpath("//th[contains(@id, 'ordenar_email')]");
		driver.findElement(button).click();// Este cambio el orden por emails
		Thread.sleep(500);

		logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		assertTrue(logins.get(0).getText().equals("admin1"));
		assertTrue(logins.get(1).getText().equals("user1"));
		assertTrue(logins.get(2).getText().equals("user2"));
		assertTrue(logins.get(3).getText().equals("user3"));
	}

	// PR10: Ordenar por Status
	@Test
	public void prueba10() throws InterruptedException {
		// Estamos en el login-validamos como admin
		validarUsuario("admin1", "admin1");

		// Pasamos al user2 de enabled a disabled
		By enlace = By
				.xpath("//td[contains(text(), 'user2@gmail.com')]/following-"
						+ "sibling::*/a[contains(@id, 'editarstatus')]");
		driver.findElement(enlace).click();// Ahora estaria disabled
		Thread.sleep(500);

		// Obtenemos la lista de usuarios ordenados inicialmente
		List<WebElement> logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		assertTrue(logins.get(0).getText().equals("admin1"));
		assertTrue(logins.get(1).getText().equals("user1"));
		assertTrue(logins.get(2).getText().equals("user2"));
		assertTrue(logins.get(3).getText().equals("user3"));

		By button = By.xpath("//th[contains(@id, 'ordenar_status')]");
		driver.findElement(button).click();// El primero ordena
		Thread.sleep(500);

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
		Thread.sleep(500);

		logins = driver.findElements(By
				.xpath("//span[contains(@id, 'td_login')]"));
		assertTrue(logins.get(0).getText().equals("user2"));
		assertTrue(logins.get(1).getText().equals("admin1"));
		assertTrue(logins.get(2).getText().equals("user1"));
		assertTrue(logins.get(3).getText().equals("user3"));

		// Pasamos al user2 a enabled
		enlace = By
				.xpath("//td[contains(text(), 'user2@gmail.com')]/following-"
						+ "sibling::*/a[contains(@id, 'editarstatus')]");
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
				.xpath("//td[contains(text(), 'user3@gmail.com')]/following-"
						+ "sibling::*/button[contains(@id,'eliminar_button')]");
		driver.findElement(button).click();// Ahora viene confirmacion
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:tablalistado:3:comfirmation_button",
				10);
		button = By
				.xpath("//button[contains(@id, 'form-template:form-"
						+ "admin:tablalistado:3:comfirmation_button')]");
		driver.findElement(button).click();// Boton confirmacion

		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:tablalistado", 10);

		// Comprobamos que se elimino
		SeleniumUtils.textoNoPresentePagina(driver, "user3");
		SeleniumUtils.textoNoPresentePagina(driver, "user3@gmail.com");

		// Deshacemos los cambios
		prepararBD = true;
	}

	// PR12: Crear una cuenta de usuario normal con datos válidos.
	@Test
	public void prueba12() throws InterruptedException {
		By button = By
				.xpath("//input[contains(@id, "
						+ "'form-template:form-login:crear-button')]");
		driver.findElement(button).click();// Pulsamos boton de creacion

		// esperamos a que se cargue la pagina de registro
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-registro", 10);

		// Registramos y confirmamos
		registrarUsuario("uprueba1", "uprueba1@gmail.com", "uprueba1",
				"uprueba1");

		// volvemos al login
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-login", 10);

		// Comprobamos en admin y devolvemos a estado inicial
		validarUsuario("admin1", "admin1");
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:tablalistado", 10);
		// Comprobamos que existe el nuevo usuario
		SeleniumUtils.textoPresentePagina(driver, "uprueba1");
		SeleniumUtils.textoPresentePagina(driver, "uprueba1@gmail.com");

		button = By
				.xpath("//td[contains(text(), 'uprueba1@gmail.com')]/following-"
						+ "sibling::*/button[contains(@id,'eliminar_button')]");
		driver.findElement(button).click();// Ahora viene confirmacion
		Thread.sleep(1000);
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:tablalistado:4:comfirmation_button",
				10);
		button = By
				.xpath("//button[contains(@id, 'form-template:form-admin:"
						+ "tablalistado:4:comfirmation_button')]");
		driver.findElement(button).click();// Boton confirmacion eliminacion
	}

	// PR13: Crear una cuenta de usuario normal con login repetido.
	@Test
	public void prueba13() {
		By button = By
				.xpath("//input[contains(@id, 'form-template:form-login:crear-"
						+ "button')]");
		driver.findElement(button).click();// Pulsamos boton de creacion

		// esperamos a que se cargue la pagina de registro. 
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-registro", 10);

		// Registramos con login repetido. 
		registrarUsuario("user1", "uprueba1@gmail.com", "uprueba1", "uprueba1");

		// Seguimos en el registro. no se ha registrado
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-registro", 10);
	}

	// PR14: Crear una cuenta de usuario normal con Email incorrecto.
	@Test
	public void prueba14() {
		By button = By
				.xpath("//input[contains(@id, 'form-template:"
						+ "form-login:crear-button')]");
		driver.findElement(button).click();// Pulsamos boton de creacion

		// esperamos a que se cargue la pagina de registro
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-registro", 10);

		// Registramos con mail incorrecto
		registrarUsuario("uprueba1", "uprueba1", "uprueba1", "uprueba1");

		// Seguimos en el registro. no se ha registrado
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-registro", 10);
	}

	// PR15: Crear una cuenta de usuario normal con Password incorrecta.
	@Test
	public void prueba15() {
		By button = By
				.xpath("//input[contains(@id, 'form-template:form-login:"
						+ "crear-button')]");
		driver.findElement(button).click();// Pulsamos boton de creacion

		// esperamos a que se cargue la pagina de registro
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-registro", 10);

		// Registramos con password incorrecto
		registrarUsuario("uprueba1", "uprueba1", "1234", "1234");

		// Seguimos en el registro
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-registro", 10);
	}

	// USUARIO
	// PR16: Comprobar que en Inbox sólo aparecen listadas las tareas sin
	// categoría y que son las que tienen que. Usar paginación navegando por las
	// tres páginas.
	@Test
	public void prueba16() throws InterruptedException {
		// Validamos al usuario
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Inicio es en inbox
		// Buscamos categorias
		List<WebElement> cat = driver.findElements(By
				.xpath("//span[contains(@id, 'categoria-noname')]"));
		assertNotNull(cat);
		assertEquals(8, cat.size()); // 8 tateas
		for (WebElement e : cat) {// Comprobamos los 8 elementos
			e.getText().equals("");
		}
		// Estamos en pag1 buscamos en la pag2
		By pag = By
				.xpath("//span[contains(@class, 'ui-icon ui-icon-seek-next')]");

		driver.findElement(pag).click();
		Thread.sleep(1000);

		// Buscamos categorias
		cat = driver.findElements(By
				.xpath("//span[contains(@id, 'categoria-noname')]"));
		assertNotNull(cat);
		assertEquals(8, cat.size()); // 8 tateas
		for (WebElement e : cat) {
			e.getText().equals("");
		}

		// Estamos en pag2 buscamos en la pag3
		pag = By.xpath("//span[contains(@class, 'ui-icon ui-icon-seek-next')]");
		driver.findElement(pag).click();
		Thread.sleep(1000);

		cat = driver.findElements(By
				.xpath("//span[contains(@id, 'categoria-noname')]"));
		assertNotNull(cat);
		assertEquals(4, cat.size()); // En esta solo hay 4 tareas
		for (WebElement e : cat) {
			e.getText().equals("");
		}
	}

	// PR17: Funcionamiento correcto de la ordenación por fecha planeada.
	@Test
	public void prueba17() throws InterruptedException {
		// Validamos al usuario
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Obtenemos la lista de tareas ordenados inicialmente
		List<WebElement> titles = driver.findElements(By
				.xpath("//span[contains(@id, 'title-inbox')]"));
		assertEquals(8, titles.size());
		assertTrue(titles.get(0).getText().equals("Semana:1"));
		assertTrue(titles.get(1).getText().equals("Semana:2"));
		assertTrue(titles.get(2).getText().equals("Semana:3"));

		// Ordenamos por fecha planeada
		By button = By.xpath("//th[contains(@id, 'ordenar-planed')]");
		driver.findElement(button).click();//
		Thread.sleep(1000);
		button = By.xpath("//th[contains(@id, 'ordenar_email')]");

		// Obtenemos la nueva lista
		titles = driver.findElements(By
				.xpath("//span[contains(@id, 'title-inbox')]"));
		assertEquals(8, titles.size());
		assertTrue(titles.get(0).getText().equals("Hoy:1"));
		assertTrue(titles.get(1).getText().equals("Hoy:2"));
		assertTrue(titles.get(2).getText().equals("Hoy:3"));

		// Ordenamos 2 vez por fecha planeada
		button = By.xpath("//th[contains(@id, 'ordenar-planed')]");
		driver.findElement(button).click();//
		Thread.sleep(1000);
		button = By.xpath("//th[contains(@id, 'ordenar_email')]");

		// Obtenemos la nueva lista
		titles = driver.findElements(By
				.xpath("//span[contains(@id, 'title-inbox')]"));
		assertEquals(8, titles.size());
		assertTrue(titles.get(0).getText().equals("Semana:6"));
		assertTrue(titles.get(1).getText().equals("Semana:5"));
		assertTrue(titles.get(2).getText().equals("Semana:4"));

	}

	// PR18: Funcionamiento correcto del filtrado.
	@Test
	public void prueba18() throws InterruptedException {
		// Filtrado del nombre de tareas
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Escribimos el titulo de una tarea
		WebElement login = driver.findElement(By
				.id("form-template:form-task:tablalistado:"
						+ "ordenar-title-inbox:filter"));
		
		login.click();
		login.clear();
		login.sendKeys("Semana:7");
		login.click();
		Thread.sleep(1000);

		List<WebElement> title = driver.findElements(By
				.xpath("//span[contains(@id, 'title-inbox')]"));
		assertEquals(1, title.size());// Solo una cumple el requisito
		assertEquals("Semana:7", title.get(0).getText());

		// Mostrar tareas que no estan en la pag inicial
		login.click();
		login.clear();
		login.sendKeys("Hoy:1");
		login.click();
		Thread.sleep(1000);

		// Apareceran Hoy:1 y Hoy:10
		title = driver.findElements(By
				.xpath("//span[contains(@id, 'title-inbox')]"));
		assertEquals(2, title.size());// Ahora 2
		assertEquals("Hoy:1", title.get(0).getText());
		assertEquals("Hoy:10", title.get(1).getText());
	}

	// PR19: Funcionamiento correcto de la ordenación por categoría.
	@Test
	public void prueba19() throws InterruptedException {
		// Validamos al usuario
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Accedemos a la lista de Week, que tiene categorías
		driver.findElement(By.id("form-template:form-task:boton-today"))
				.click();//
		Thread.sleep(1000);

		// Obtenemos la lista de tareas ordenados inicialmente
		List<WebElement> titles = driver.findElements(By
				.xpath("//span[contains(@id, 'categoria-noname')]"));
		assertEquals(8, titles.size());
		assertTrue(titles.get(0).getText().equals(""));
		assertTrue(titles.get(1).getText().equals(""));
		assertTrue(titles.get(2).getText().equals(""));

		// Ordenamos por categoría dos veces para mostrar las que tienen nombre
		By button = By.xpath("//th[contains(@id, 'ordenar-cat')]");
		driver.findElement(button).click();//
		Thread.sleep(1000);
		button = By.xpath("//th[contains(@id, 'ordenar-cat')]");
		driver.findElement(button).click();//
		Thread.sleep(1000);

		// Obtenemos la nueva lista
		titles = driver.findElements(By
				.xpath("//span[contains(@id, 'categoria-noname')]"));
		assertEquals(8, titles.size());
		assertTrue(titles.get(0).getText().equals("categoria3"));
		assertTrue(titles.get(1).getText().equals("categoria3"));
		assertTrue(titles.get(4).getText().equals("categoria2"));

		// Ordenamos de nuevo para mostrar las que no tienen categoría
		button = By.xpath("//th[contains(@id, 'ordenar-cat')]");
		driver.findElement(button).click();//
		Thread.sleep(1000);

		// Obtenemos la nueva lista
		titles = driver.findElements(By
				.xpath("//span[contains(@id, 'categoria-noname')]"));
		assertEquals(8, titles.size());
		assertTrue(titles.get(0).getText().equals(""));
		assertTrue(titles.get(1).getText().equals(""));
		assertTrue(titles.get(2).getText().equals(""));
	}

	// PR20: Funcionamiento correcto de la ordenación por fecha planeada.
	@Test
	public void prueba20() throws InterruptedException {
		// Validamos al usuario
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Obtenemos la lista de tareas ordenados inicialmente
		List<WebElement> titles = driver.findElements(By
				.xpath("//span[contains(@id, 'title-inbox')]"));
		assertEquals(8, titles.size());
		assertTrue(titles.get(0).getText().equals("Semana:1"));
		assertTrue(titles.get(1).getText().equals("Semana:2"));
		assertTrue(titles.get(2).getText().equals("Semana:3"));

		// Ordenamos por fecha planeada
		By button = By.xpath("//th[contains(@id, 'ordenar-planed')]");
		driver.findElement(button).click();//
		Thread.sleep(1000);
		button = By.xpath("//th[contains(@id, 'ordenar_email')]");

		// Obtenemos la nueva lista
		titles = driver.findElements(By
				.xpath("//span[contains(@id, 'title-inbox')]"));
		assertEquals(8, titles.size());
		assertTrue(titles.get(0).getText().equals("Hoy:1"));
		assertTrue(titles.get(1).getText().equals("Hoy:2"));
		assertTrue(titles.get(2).getText().equals("Hoy:3"));

		// Ordenamos 2 vez por fecha planeada
		button = By.xpath("//th[contains(@id, 'ordenar-planed')]");
		driver.findElement(button).click();//
		Thread.sleep(1000);
		button = By.xpath("//th[contains(@id, 'ordenar_email')]");

		// Obtenemos la nueva lista
		titles = driver.findElements(By
				.xpath("//span[contains(@id, 'title-inbox')]"));
		assertEquals(8, titles.size());
		assertTrue(titles.get(0).getText().equals("Semana:6"));
		assertTrue(titles.get(1).getText().equals("Semana:5"));
		assertTrue(titles.get(2).getText().equals("Semana:4"));
	}

	// PR21: Comprobar que las tareas que no están en rojo son las de hoy y
	// además las que deben ser.
	@Test
	public void prueba21() throws InterruptedException {
		// Validamos al usuario
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Accedemos a la lista de Today
		driver.findElement(By.id("form-template:form-task:boton-today"))
				.click();//
		Thread.sleep(1000);

		// Obtenemos la lista de tareas y comprobamos fechas
		List<WebElement> titles = driver.findElements(By
				.xpath("//span[contains(@id, 'planned-nored')]"));
		assertEquals(8, titles.size());

		assertTrue(titles.get(0).getText().equals(DateUtil.today().toString()));
		assertTrue(titles.get(1).getText().equals(DateUtil.today().toString()));
		assertTrue(titles.get(2).getText().equals(DateUtil.today().toString()));
	}

	// PR22: Comprobar que las tareas retrasadas están en rojo y son las que
	// deben ser.
	@Test
	public void prueba22() throws InterruptedException {
		// Validamos al usuario
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Accedemos a la lista de Today
		driver.findElement(By.id("form-template:form-task:boton-today"))
				.click();//
		Thread.sleep(1000);

		// Estamos en pag1 buscamos en la pag2 (Que tiene retrasadas)
		By pag = By
				.xpath("//span[contains(@class, 'ui-icon ui-icon-seek-next')]");

		driver.findElement(pag).click();
		Thread.sleep(1000);

		// Obtenemos la lista de tareas y comprobamos fechas
		List<WebElement> titles = driver.findElements(By
				.xpath("//span[contains(@id, 'planned-red')]"));
		assertEquals(6, titles.size());
		assertTrue(titles.get(2).getText()
				.equals(DateUtil.yesterday().toString()));
		assertTrue(titles.get(3).getText()
				.equals(DateUtil.yesterday().toString()));
		assertTrue(titles.get(4).getText()
				.equals(DateUtil.yesterday().toString()));
	}

	// PR23: Comprobar que las tareas de hoy y futuras no están en rojo y que
	// son las que deben ser.
	@Test
	public void prueba23() throws InterruptedException {
		// Validamos al usuario
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Accedemos a la lista de Today
		driver.findElement(By.id("form-template:form-task:boton-week")).click();
		Thread.sleep(1000);

		// Obtenemos la lista de categorias NO en rojo (no retrasadas) y de
		// fechas
		List<WebElement> cat = driver.findElements(By
				.xpath("//span[contains(@id, 'categoria-nored')]"));
		List<WebElement> date = driver.findElements(By
				.xpath("//span[contains(@id, 'planned-week')]"));
		assertEquals(8, cat.size());
		assertEquals(8, date.size());

		// Comprobamos que ambos elementos son de la misma fila
		assertEquals(
				date.get(0).findElement(By.xpath(".."))
						.findElement(By.xpath("..")),
				cat.get(0).findElement(By.xpath(".."))
						.findElement(By.xpath("..")));
		assertEquals(
				date.get(1).findElement(By.xpath(".."))
						.findElement(By.xpath("..")),
				cat.get(1).findElement(By.xpath(".."))
						.findElement(By.xpath("..")));
		assertEquals(
				date.get(2).findElement(By.xpath(".."))
						.findElement(By.xpath("..")),
				cat.get(2).findElement(By.xpath(".."))
						.findElement(By.xpath("..")));
		// Comprobamos que efectivamente sus fechas son posteriores
		assertTrue(date.get(0).getText().
				equals(DateUtil.tomorrow().toString()));
		assertTrue(date.get(1).getText()
				.equals(DateUtil.addDays(DateUtil.today(), 2).toString()));
		assertTrue(date.get(2).getText()
				.equals(DateUtil.addDays(DateUtil.today(), 3).toString()));
	}

	// PR24: Funcionamiento correcto de la ordenación por día.
	@Test
	public void prueba24() throws InterruptedException {
		// Validamos al usuario
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Accedemos a la lista de Week
		driver.findElement(By.id("form-template:form-task:boton-week")).click();
		Thread.sleep(1000);

		// Obtenemos la lista de tareas ordenados inicialmente
		List<WebElement> date = driver.findElements(By
				.xpath("//span[contains(@id, 'planned-week')]"));
		assertEquals(8, date.size());
		assertTrue(date.get(0).getText().
				equals(DateUtil.tomorrow().toString()));
		assertTrue(date.get(1).getText()
				.equals(DateUtil.addDays(DateUtil.today(), 2).toString()));
		assertTrue(date.get(2).getText()
				.equals(DateUtil.addDays(DateUtil.today(), 3).toString()));

		// Ordenamos por fecha planeada
		By button = By.xpath("//th[contains(@id, 'ordenar-planed')]");
		driver.findElement(button).click();//
		Thread.sleep(1000);

		// Obtenemos la nueva lista
		date = driver.findElements(By
				.xpath("//span[contains(@id, 'planned-week')]"));
		assertEquals(8, date.size());
		assertTrue(date.get(0).getText()
				.equals(DateUtil.yesterday().toString()));
		assertTrue(date.get(1).getText()
				.equals(DateUtil.yesterday().toString()));
		assertTrue(date.get(2).getText()
				.equals(DateUtil.yesterday().toString()));

		// Ordenamos 2 vez por fecha planeada
		button = By.xpath("//th[contains(@id, 'ordenar-planed')]");
		driver.findElement(button).click();//
		Thread.sleep(1000);

		// Obtenemos la nueva lista
		date = driver.findElements(By
				.xpath("//span[contains(@id, 'planned-week')]"));
		assertEquals(8, date.size());
		assertTrue(date.get(0).getText()
				.equals(DateUtil.addDays(DateUtil.today(), 6).toString()));// 27
		assertTrue(date.get(1).getText()
				.equals(DateUtil.addDays(DateUtil.today(), 5).toString()));// 26
		assertTrue(date.get(2).getText()
				.equals(DateUtil.addDays(DateUtil.today(), 4).toString()));// 25
	}

	// PR25: Funcionamiento correcto de la ordenación por nombre.
	@Test
	public void prueba25() throws InterruptedException {
		// Validamos al usuario
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Accedemos a la lista de Week
		driver.findElement(By.id("form-template:form-task:boton-week")).click();
		Thread.sleep(1000);

		// Obtenemos la lista de tareas ordenados inicialmente
		List<WebElement> titles = driver.findElements(By
				.xpath("//span[contains(@id, 'title-noinbox')]"));
		assertEquals(8, titles.size());
		assertTrue(titles.get(0).getText().equals("Semana:1"));
		assertTrue(titles.get(1).getText().equals("Semana:2"));
		assertTrue(titles.get(2).getText().equals("Semana:3"));

		// Ordenamos por fecha planeada
		By button = By.xpath("//th[contains(@id, 'ordenar-title')]");
		driver.findElement(button).click();//
		Thread.sleep(1000);

		// Obtenemos la nueva lista
		titles = driver.findElements(By
				.xpath("//span[contains(@id, 'title-noinbox')]"));
		assertEquals(8, titles.size());
		assertTrue(titles.get(0).getText().equals("Con categoria1:1"));
		assertTrue(titles.get(1).getText().equals("Con categoria1:2"));
		assertTrue(titles.get(2).getText().equals("Con categoria1:3"));

		// Ordenamos 2 vez por fecha planeada
		button = By.xpath("//th[contains(@id, 'ordenar-title')]");
		driver.findElement(button).click();//
		Thread.sleep(1000);

		// Obtenemos la nueva lista
		titles = driver.findElements(By
				.xpath("//span[contains(@id, 'title-noinbox')]"));
		assertEquals(8, titles.size());
		assertTrue(titles.get(0).getText().equals("Semana:9"));
		assertTrue(titles.get(1).getText().equals("Semana:8"));
		assertTrue(titles.get(2).getText().equals("Semana:7"));
	}

	// PR26: Confirmar una tarea, inhabilitar el filtro de tareas terminadas, ir
	// a la pagina donde está la tarea terminada y comprobar que se muestra.
	@Test
	public void prueba26() throws InterruptedException {
		// Validamos al usuario
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Cerramos la priemra tarea
		driver.findElement(
				By.id("form-template:form-task:tablalistado:0:finalizar"))
				.click();
		Thread.sleep(1000);

		// Pulsamos en ver finalizadas
		driver.findElement(By.id("form-template:form-task:filtro-terminadas"))
				.click();
		Thread.sleep(1000);

		// Nos movemos a la tercera página
		By pag = By
				.xpath("//span[contains(@class, 'ui-icon ui-icon-seek-next')]");

		driver.findElement(pag).click();
		Thread.sleep(1000);
		driver.findElement(pag).click();
		Thread.sleep(1000);

		// Obtenemos la tarea finalizada
		List<WebElement> titles = driver.findElements(By
				.xpath("//span[contains(@id, 'title-inboxFinished')]"));
		assertEquals(1, titles.size());
		assertTrue(titles.get(0).getText().equals("Semana:1"));

		// Devolvemos todo a como estaba
		prepararBD=true;
	}

	// PR27: Crear una tarea sin categoría y comprobar que se muestra en la
	// lista Inbox.
	@Test
	public void prueba27() throws InterruptedException {
		// Filtrado del nombre de tareas
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Añadimos el nombre de la nueva tarea
		WebElement nombre = driver.findElement(By
				.id("form-template:form-task:taskNombre"));
		nombre.click();
		nombre.clear();
		nombre.sendKeys("TaskInbox");

		driver.findElement(By.id("form-template:form-task:taskButton")).click();
		Thread.sleep(1000);

		// Seguiriamos en inbox y buscamos por el listado
		// Probamos no hay categorias:
		// Buscamos categorias
		List<WebElement> cat = driver.findElements(By
				.xpath("//span[contains(@id, 'categoria-noname')]"));
		assertNotNull(cat);
		assertEquals(8, cat.size()); // 8 tateas
		for (WebElement e : cat) {
			e.getText().equals("");
		}

		// La añadida esta en la pag 3 del listado inbox
		By pag = By
				.xpath("//span[contains(@class, 'ui-icon ui-icon-seek-next')]");
		driver.findElement(pag).click();
		Thread.sleep(1000);
		driver.findElement(pag).click();
		Thread.sleep(1000);
		List<WebElement> title = driver.findElements(By
				.xpath("//span[contains(@id, 'title-inbox')]"));
		assertTrue(title.get(4).getText().equals("TaskInbox"));// 5 elemento

		// Reseteamos la bbdd
		By button = By
				.xpath("//a[contains(@id, 'form-template:cerrarsesion')]");
		driver.findElement(button).click();// Pulsamos sobre cerrar sesion

		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-login", 10);

		// Validamos con el admin
		validarUsuario("admin1", "admin1");

		// Esperamos a que se cargue la pagina del admin
		// concretamente al link de iniciar la BD
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-admin:reiniciarbd", 10);
		// Pulsamos sobre el enlace
		By link = By.id("form-template:form-admin:reiniciarbd");
		driver.findElement(link).click();
	}

	// PR28: Crear una tarea con categoría categoria1 y fecha planeada Hoy y
	// comprobar que se muestra en la lista Hoy.
	@Test
	public void prueba28() throws InterruptedException {
		// Filtrado del nombre de tareas
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Añadimos el nombre de la nueva tarea
		WebElement nombre = driver.findElement(By
				.id("form-template:form-task:taskNombre"));
		nombre.click();
		nombre.clear();
		nombre.sendKeys("TaskToday");

		Thread.sleep(1000);
		
		//Añadimos la fecha de hoy
		WebElement fecha = driver.findElement(By
				.id("form-template:form-task:taskPlanned_input"));
		fecha.click();
		fecha.clear();
		fecha.sendKeys(DateUtil.toString(DateUtil.today()));
		
		// Cambiamos la categoría
		WebElement login = driver.findElement(By
				.id("form-template:form-task:taskCategory_label"));
		login.click();
		Thread.sleep(500);
		login = driver.findElement(By
				.id("form-template:form-task:taskCategory_1"));
		login.click();
		Thread.sleep(500);

		//Creamos la nueva tarea
		driver.findElement(By.id("form-template:form-task:taskButton")).click();
		Thread.sleep(1000);

		// Estaremos en today y buscamos por el listado
		// Probamos hay categorias:
		// La añadida esta en la pag 3 del listado junto tareas con categoria
		By pag = By
				.xpath("//span[contains(@class, 'ui-icon ui-icon-seek-next')]");
		driver.findElement(pag).click();
		Thread.sleep(1000);
		driver.findElement(pag).click();
		Thread.sleep(1000);//Llegamos a la ultima pagina
		
		//Mostramos que hay categorias en hoy
		List<WebElement> cat = driver.findElements(By
				.xpath("//span[contains(@id, 'categoria-noname')]"));
		assertEquals(5, cat.size()); // 5 tareas
		for (WebElement e : cat) {
			e.getText().contains("categoria");
		}
		
		//Obtenemos sus titulo
		List<WebElement> title = driver.findElements(By
				.xpath("//span[contains(@id, 'title-noinbox')]"));
		
		assertTrue(title.get(4).getText().equals("TaskToday"));// 5 elemento
		assertTrue(cat.get(4).getText().equals("categoria1"));

		
		// Devolvemos todo a como estaba
		prepararBD = true;
	}

	// PR29: Crear una tarea con categoría categoria1 y fecha planeada posterior
	// a Hoy y comprobar que se muestra en la lista Semana.
	@Test
	public void prueba29() throws InterruptedException {
		// Filtrado del nombre de tareas
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Añadimos el nombre de la nueva tarea
		WebElement nombre = driver.findElement(By
				.id("form-template:form-task:taskNombre"));
		nombre.click();
		nombre.clear();
		nombre.sendKeys("TaskWeek");

		Thread.sleep(1000);

		// Añadimos la fecha dentro de dos dias
		WebElement fecha = driver.findElement(By
				.id("form-template:form-task:taskPlanned_input"));
		fecha.click();
		fecha.clear();
		fecha.sendKeys(DateUtil.toString(DateUtil.
				addDays(DateUtil.today(), 2)));

		// Cambiamos la categoría
		WebElement login = driver.findElement(By
				.id("form-template:form-task:taskCategory_label"));
		login.click();
		Thread.sleep(500);
		login = driver.findElement(By
				.id("form-template:form-task:taskCategory_1"));
		login.click();
		Thread.sleep(500);

		// Creamos la nueva tarea
		driver.findElement(By.id("form-template:form-task:taskButton")).click();
		Thread.sleep(1000);

		// Estaremos en week y buscamos por el listado. Este tiene 4 pags.
		// Probamos hay categorias:
		// La añadida esta en la pag 4 del listado junto tareas con categoria
		By pag = By
				.xpath("//span[contains(@class, 'ui-icon ui-icon-seek-next')]");
		driver.findElement(pag).click();
		Thread.sleep(500);
		driver.findElement(pag).click();
		Thread.sleep(500);
		driver.findElement(pag).click();
		Thread.sleep(1000);// Llegamos a la ultima pagina

		// Mostramos que hay tareas con categoria en week que ademas 
		//estan retrasadas
		List<WebElement> cat = driver.findElements(By
				.xpath("//span[contains(@id, 'categoria-red')]"));
		assertEquals(6, cat.size()); // 6 tareas retrasadas
		for (WebElement e : cat) {
			e.getText().contains("categoria");
		}

		// Obtenemos nuestra tarea que es la 7 y no retrasada en la pag4
		List<WebElement> title = driver.findElements(By
				.xpath("//span[contains(@id, 'title-noinbox')]"));
		assertTrue(title.get(6).getText().equals("TaskWeek"));// 7 elemento
		
		//la tarea no esta atrasada como la 6 de arriba. No muetra en rojo
		WebElement catT = driver.findElement(By
				.xpath("//span[contains(@id, 'categoria-nored')]"));
		assertTrue(catT.getText().equals("categoria1"));

		// Devolvemos todo a como estaba
		prepararBD=true;
	}

	// PR30: Editar el nombre, y categoría de una tarea (se le cambia a
	// categoría1) de la lista Inbox y comprobar que las tres pseudolista se
	// refresca correctamente.
	@Test
	public void prueba30() throws InterruptedException {
		// Validamos al usuario
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Sacanmos los botones editar y pulsando el primero
		driver.findElement(
				By.id("form-template:form-task:tablalistado:0:editar")).click();

		SeleniumUtils.EsperaCargaPagina(driver, "id", 
				"form-template:form-task:tablaform", 10);

		// Escribimos el titulo nuevo de una tarea
		WebElement login = driver.findElement(By
				.id("form-template:form-task:title_input"));
		login.click();
		login.clear();
		login.sendKeys("TAREA MODIFICADA");
		Thread.sleep(500);

		// Cambiamos la categoría
		login = driver.findElement(By
				.id("form-template:form-task:categorias_menu"));
		login.click();
		Thread.sleep(500);

		login = driver.findElement(By
				.id("form-template:form-task:categorias_menu_1"));
		login.click();
		Thread.sleep(500);

		// Finalizamos la edición
		login = driver.findElement(By
				.id("form-template:form-task:editar_button"));
		login.click();
		
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// La añadida esta en la pag 4 de Week, nada más
		// Desaparece de Inbox al tener categoría, y de Today por la fecha
		// Accedemos a la lista de Week
		driver.findElement(By.id("form-template:form-task:boton-week")).click();
		Thread.sleep(500);

		By pag = By
				.xpath("//span[contains(@class, 'ui-icon ui-icon-seek-next')]");
		driver.findElement(pag).click();
		Thread.sleep(500);
		driver.findElement(pag).click();
		Thread.sleep(500);
		driver.findElement(pag).click();
		Thread.sleep(500);

		// Comprobamos que aparece
		List<WebElement> title = driver.findElements(By
				.xpath("//span[contains(@id, 'title-noinbox')]"));
		assertTrue(title.get(5).getText().equals("TAREA MODIFICADA"));// 5

		// Reseteamos la bbdd
		prepararBD=true;

	}

	// PR31: Editar el nombre, y categoría (Se cambia a sin categoría) de una
	// tarea de la lista Hoy y comprobar que las tres pseudolistas se refrescan
	// correctamente.
	@Test
	public void prueba31() throws InterruptedException {
		// Validamos al usuario
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		driver.findElement(By.id("form-template:form-task:boton-today"))
				.click();//
		Thread.sleep(500);

		// Ordenamos para coger uan con categoría
		By button = By.xpath("//th[contains(@id, 'ordenar-cat')]");
		driver.findElement(button).click();//
		driver.findElement(button).click();//
		Thread.sleep(500);

		// Sacanmos los botones editar y pulsando el primero
		driver.findElement(
				By.id("form-template:form-task:tablalistado:0:editar")).click();
		
		SeleniumUtils.EsperaCargaPagina(driver, "id", 
				"form-template:form-task:tablaform", 10);

		// Escribimos el titulo nuevo de una tarea
		WebElement login = driver.findElement(By
				.id("form-template:form-task:title_input"));
		login.click();
		login.clear();
		login.sendKeys("TAREA MODIFICADA");

		// Cambiamos la categoría
		login = driver.findElement(By
				.id("form-template:form-task:categorias_menu"));
		login.click();
		Thread.sleep(500);

		login = driver.findElement(By
				.id("form-template:form-task:categorias_menu_0"));
		login.click();
		Thread.sleep(500);

		// Finalizamos la edición
		login = driver.findElement(By
				.id("form-template:form-task:editar_button"));
		login.click();
		
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// La añadida esta Inbox, y en las otras 2 donde ya estaba
		driver.findElement(By.id("form-template:form-task:boton-today"))
				.click();//
		Thread.sleep(500);

		// Ordenamos por título apra que salga siempre primera
		button = By.id("form-template:form-task:tablalistado:ordenar-title");
		driver.findElement(button).click();//
		Thread.sleep(500);
		driver.findElement(button).click();//
		Thread.sleep(500);
		
		// Comprobamos que aparece en TODAY
		List<WebElement> title = driver.findElements(By
				.xpath("//span[contains(@id, 'title-noinbox')]"));
		assertTrue(title.get(0).getText().equals("TAREA MODIFICADA"));

		// Comprobamos que aparece en INBOX
		driver.findElement(By.id("form-template:form-task:boton-inbox"))
				.click();//
		Thread.sleep(500);
		title = driver.findElements(By
				.xpath("//span[contains(@id, 'title-inbox')]"));

		assertTrue(title.get(0).getText().equals("TAREA MODIFICADA"));

		// Comprobamos que aparece en WEEK
		driver.findElement(By.id("form-template:form-task:boton-week")).click();
		Thread.sleep(500);
		title = driver.findElements(By
				.xpath("//span[contains(@id, 'title-noinbox')]"));

		assertTrue(title.get(0).getText().equals("TAREA MODIFICADA"));

		// Reseteamos la bbdd
		prepararBD=true;
	}

	// PR32: Marcar una tarea como finalizada. Comprobar que desaparece de las
	// tres pseudolistas.
	@Test
	public void prueba32() throws InterruptedException {
		// Validamos al usuario
		validarUsuario("user1", "user1");

		// Cargamos la pag. de tareas
		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Obtenemos la lista de tareas y comprobamos la primera
		List<WebElement> titles = driver.findElements(By
				.xpath("//span[contains(@id, 'taskId')]"));
		assertEquals(8, titles.size());
		String id = titles.get(0).getText();

		// Finalizamos la tarea
		driver.findElement(
				By.id("form-template:form-task:tablalistado:0:finalizar"))
				.click();

		// Esperamos a la tabla
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);
		Thread.sleep(1000);
		// Obtenemos la lista de tareas y comprobamos que el primero ya no es el
		// mismo
		titles = driver.findElements(By
				.xpath("//span[contains(@id, 'taskId')]"));
		assertEquals(8, titles.size());
		assertFalse(titles.get(0).getText().equals(id));

		// Cambiamos al lista week
		driver.findElement(By.id("form-template:form-task:boton-week")).click();
		Thread.sleep(1000);

		// Obtenemos la lista de tareas y comprobamos que ya no esta
		titles = driver.findElements(By
				.xpath("//span[contains(@id, 'taskId')]"));
		assertEquals(8, titles.size());
		SeleniumUtils.textoNoPresentePagina(driver, id);

		// Cambiamos al lista today
		driver.findElement(By.id("form-template:form-task:boton-today"))
				.click();//
		Thread.sleep(1000);

		// Obtenemos la lista de tareas y comprobamos que ya no esta
		titles = driver.findElements(By
				.xpath("//span[contains(@id, 'taskId')]"));
		assertEquals(8, titles.size());
		SeleniumUtils.textoNoPresentePagina(driver, id);

		// Reseteamos la bbdd
		prepararBD=true;
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
	public void prueba35() throws InterruptedException {
		// Desde la ventana de login comprobamos textos español al inicio
		SeleniumUtils.textoPresentePagina(driver, "Usuario");
		SeleniumUtils.textoPresentePagina(driver, "Contraseña");
		SeleniumUtils.textoPresentePagina(driver, "ESPAÑOL");
		SeleniumUtils.textoPresentePagina(driver, "INGLES");

		SeleniumUtils.ClickSubopcionMenuHover(driver,
				"form-template:idioma-menu", "form-template:en-menu");
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-login", 10);
		Thread.sleep(1000);

		// Ahora el login esta localizado
		SeleniumUtils.textoPresentePagina(driver, "Login");
		SeleniumUtils.textoPresentePagina(driver, "Password");
		SeleniumUtils.textoPresentePagina(driver, "SPANISH");
		SeleniumUtils.textoPresentePagina(driver, "ENGLISH");

		// Probamos la vista de admin este localizado a en
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
	public void prueba36() throws InterruptedException {
		// Principio en el login en español pasamos a en
		SeleniumUtils.ClickSubopcionMenuHover(driver,
				"form-template:idioma-menu", "form-template:en-menu");
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-login", 10);
		Thread.sleep(1000);

		// Ahora el login esta localizado
		SeleniumUtils.textoPresentePagina(driver, "Login");
		SeleniumUtils.textoPresentePagina(driver, "Password");
		SeleniumUtils.textoPresentePagina(driver, "SPANISH");
		SeleniumUtils.textoPresentePagina(driver, "ENGLISH");

		// Volvemos a es
		SeleniumUtils.ClickSubopcionMenuHover(driver,
				"form-template:idioma-menu", "form-template:es-menu");
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-login", 10);
		Thread.sleep(1000);

		// Ahora el login esta localizado
		SeleniumUtils.textoNoPresentePagina(driver, "Login");
		SeleniumUtils.textoPresentePagina(driver, "Usuario");
		SeleniumUtils.textoNoPresentePagina(driver, "Password");
		SeleniumUtils.textoPresentePagina(driver, "Contraseña");
		SeleniumUtils.textoNoPresentePagina(driver, "SPANISH");
		SeleniumUtils.textoPresentePagina(driver, "ESPAÑOL");
		SeleniumUtils.textoNoPresentePagina(driver, "ENGLISH");
		SeleniumUtils.textoPresentePagina(driver, "INGLES");

		// vplvemos localizar a en
		SeleniumUtils.ClickSubopcionMenuHover(driver,
				"form-template:idioma-menu", "form-template:en-menu");
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-login", 10);

		// Probamos en la tablaTareas de un user
		validarUsuario("user1", "user1");
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		SeleniumUtils.textoPresentePagina(driver, "Today:");
		SeleniumUtils.textoPresentePagina(driver, "Week:");

		// Localizamos a es
		SeleniumUtils.ClickSubopcionMenuHover(driver,
				"form-template:idioma-menu", "form-template:es-menu");
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task", 10);

		Thread.sleep(1000);

		SeleniumUtils.textoNoPresentePagina(driver, "Today:");
		SeleniumUtils.textoPresentePagina(driver, "Hoy:");
		SeleniumUtils.textoNoPresentePagina(driver, "Week:");
		SeleniumUtils.textoPresentePagina(driver, "Semana:");
	}

	// PR37: Intento de acceso a un URL privado de administrador con un usuario
	// autenticado como usuario normal.
	@Test
	public void prueba37() {
		// Autentificamos como usuario
		validarUsuario("user1", "user1");

		// Esperamos a que se cargue la pagina del user1
		// concretamente la tabla de task
		SeleniumUtils.EsperaCargaPagina(driver, "id",
				"form-template:form-task:tablalistado", 10);

		// Tratamos de acceder a un url del admin
		driver.
		get("http://localhost:8280/sdi2-20/restricted/listaUsuarios.xhtml");
		// driver.
		// get("http://localhost:8180/sdi2-20/restricted/listaUsuarios.xhtml");

		// Se carga una página de acceso restringido
		SeleniumUtils.EsperaCargaPagina(driver, "id", "form-error", 10);
		driver.getCurrentUrl().equals(
				"http://localhost:8280/sdi2-20/error.xhtml");
		// driver.getCurrentUrl().
		// equals("http://localhost:8180/sdi2-20/error.xhtml");
	}

	// PR38: Intento de acceso a un URL privado de usuario normal con un usuario
	// no autenticado.
	@Test
	public void prueba38() {
		// Tratamos de acceder a un url de usuario como anonimo
		driver.get("http://localhost:8280/sdi2-20/usuarios/listaTareas.xhtml");
		// driver.
		// get("http://localhost:8180/sdi2-20/usuarios/listaTareas.xhtml");

		// Se carga una página de acceso restringido
		SeleniumUtils.EsperaCargaPagina(driver, "id", "form-error", 10);
		driver.getCurrentUrl().equals(
				"http://localhost:8280/sdi2-20/error.xhtml");
		// driver.getCurrentUrl().
		// equals("http://localhost:8180/sdi2-20/error.xhtml");
	}
}