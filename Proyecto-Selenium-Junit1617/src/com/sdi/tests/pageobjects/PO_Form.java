package com.sdi.tests.pageobjects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class PO_Form {

	
	
   public void rellenaFormulario(WebDriver driver, String pnombre, String papellidos, String piduser, String pemail)
   {
		WebElement nombre = driver.findElement(By.id("form-principal:nombre"));
		nombre.click();
		nombre.clear();
		nombre.sendKeys(pnombre);
		WebElement apellidos = driver.findElement(By.id("form-principal:apellidos"));
		apellidos.click();
		apellidos.clear();
		apellidos.sendKeys(papellidos);
		WebElement iduser = driver.findElement(By.id("form-principal:iduser"));
		iduser.click();
		iduser.clear();
		iduser.sendKeys(piduser);
		WebElement idcorreo = driver.findElement(By.id("form-principal:correo"));
		idcorreo.click();
		idcorreo.clear();
		idcorreo.sendKeys(pemail);
		//Pulsar el boton de Alta.
		By boton = By.id("form-principal:boton");
		driver.findElement(boton).click();	   
   }
   
   public void rellenaLogin(WebDriver driver, String ulogin, String upassword)
   {
	   WebElement login = driver.findElement(By.id("form-login:login-text"));
	   login.click();
	   login.clear();
	   login.sendKeys(ulogin);
	   WebElement password = driver.findElement(By.id("form-login:password-text"));
	   password.click();
	   password.clear();
	   password.sendKeys(ulogin);
	   //Pulsar el boton de validacion
	   By boton = By.id("form-login:validation-button");
	   driver.findElement(boton).click();
   }
	
	
	
}
