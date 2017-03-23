package com.sdi.business.impl.admin.command;

import java.util.List;

import alb.util.date.DateUtil;

import com.sdi.business.exception.BusinessException;
import com.sdi.business.impl.command.Command;
import com.sdi.dto.Category;
import com.sdi.dto.Task;
import com.sdi.dto.User;
import com.sdi.dto.types.UserStatus;
import com.sdi.persistence.CategoryDao;
import com.sdi.persistence.Persistence;
import com.sdi.persistence.TaskDao;
import com.sdi.persistence.UserDao;

/**
 * 
 * @author raulr clase que borre todos los datos de la base de datos menos el
 *         usuario administrador (admin1/admin1) e inserte los siguientes datos:
 *         • 3 usuarios normales, con login/contraseña: user1/user1, user2/user2
 *         y user3/user3. • 3 categorias por usuario: categoría1, categoría2 y
 *         categoría3. • Cada usuario normal tendrá 30 tareas registradas: 10
 *         tareas previstas para los siguientes 6 días a hoy (sin categorizar),
 *         10 tareas para el día que se ejecuta la tarea - (sin categorizar) y
 *         otras 10 retrasadas respecto al día que ejecute la prueba y
 *         pertenecientes a las categorías 1 (3 tareas), 2 (3 tareas) y 3 (4
 *         tareas).
 */
public class InitiateDBCommand implements Command<Void> {

	private TaskDao tDao = null;
	private CategoryDao cDao = null;
	private UserDao uDao = null;

	@Override
	public Void execute() throws BusinessException {

		tDao = Persistence.getTaskDao();
		cDao = Persistence.getCategoryDao();
		uDao = Persistence.getUserDao();

		try {
			deleteAllUsers();
			createUsers();
			createCategories();
			createTasks();
		} catch (Exception e) {
			throw new BusinessException("Ha fallado la inicializacion de la BD");
		}

		return null;

	}

	private void createUsers() {
		User user = null;
		for (int i = 1; i <= 3; i++) {
			user = new User();
			user.setLogin("user" + i);
			user.setPassword("user" + i);
			user.setEmail("user" + i + "@gmail.com");
			user.setIsAdmin(false);
			user.setStatus(UserStatus.ENABLED);
			uDao.save(user);
		}
	}

	// Crea 3 categorias por usuario
	private void createCategories() {
		Category cat = new Category();
		List<User> users = uDao.findAll();
		for (User u : users) {
			if (!u.getIsAdmin()) {
				cat.setUserId(u.getId());
				cat.setName("categoria1");
				cDao.save(cat);
				cat.setName("categoria2");
				cDao.save(cat);
				cat.setName("categoria3");
				cDao.save(cat);
			}
		}
	}

	// Eliminamos todos los usuarios menos al admin
	private void deleteAllUsers() {
		List<User> users = uDao.findAll();
		for (User u : users) {
			if (!u.getIsAdmin()) {
				tDao.deleteAllFromUserId(u.getId());
				cDao.deleteAllFromUserId(u.getId());
				uDao.delete(u.getId());
			}
		}
	}

	private void createTasks() {
		List<User> users = uDao.findAll();
		for (User u : users) {
			if (!u.getIsAdmin()) {
				weekTasks(u);
				todayTasks(u);
				categoryTasks(u);
			}
		}
	}

	private void weekTasks(User u) {
		Task task = new Task();
		task.setUserId(u.getId());
		task.setCategoryId(null); // Sin categoria
		task.setCreated(DateUtil.today());
		task.setFinished(null);
		for (int i = 1; i <= 6; i++) {
			task.setTitle("Semana:" + i);
			task.setPlanned(DateUtil.addDays(DateUtil.today(), i));
			tDao.save(task);
		}
		for (int i = 1; i <= 4; i++) {
			task.setTitle("Semana:" + (6 + i));
			task.setPlanned(DateUtil.addDays(DateUtil.today(), i));
			tDao.save(task);
		}
	}

	private void todayTasks(User u) {
		Task task = new Task();
		task.setUserId(u.getId());
		task.setCategoryId(null);// Sin categoria
		task.setCreated(DateUtil.today());
		task.setPlanned(DateUtil.today());
		task.setFinished(null);
		for (int i = 1; i <= 10; i++) {
			task.setTitle("Hoy:" + i);
			tDao.save(task);
		}
	}

	private void categoryTasks(User u) {
		Task task = new Task();
		task.setUserId(u.getId());
		task.setCreated(DateUtil.today());
		task.setPlanned(DateUtil.yesterday()); // retrasadas
		task.setFinished(null);

		for (int i = 1; i <= 3; i++) { // 3 de categoria1
			task.setTitle("Con categoria1:" + i);
			task.setCategoryId(cDao
					.findByUserIdAndName(u.getId(), "categoria1").getId());

			tDao.save(task);
		}

		for (int i = 1; i <= 3; i++) { // 3 de categoria2
			task.setTitle("Con categoria2:" + i);
			task.setCategoryId(cDao
					.findByUserIdAndName(u.getId(), "categoria2").getId());

			tDao.save(task);
		}

		for (int i = 1; i <= 4; i++) { // 4 de categoria3
			task.setTitle("Con categoria3:" + i);
			task.setCategoryId(cDao
					.findByUserIdAndName(u.getId(), "categoria3").getId());

			tDao.save(task);
		}

	}

}
