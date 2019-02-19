package org.ruanwei.demo.springframework.dataAccess.orm.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.ruanwei.demo.springframework.dataAccess.User;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class UserHibernateDao {
	private static Log log = LogFactory.getLog(UserHibernateDao.class);

	private static final String sql_11 = "select name from user where id = 1";
	private static final String sql_12 = "select name from user where id = ?";
	private static final String hql_12 = "from demo.User user where user.id=?";
	private static final String sql_13 = "select name from user where id = :id";

	private SessionFactory sessionFactory;
	private HibernateTemplate hibernateTemplate;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	public void queryForSingleRowWithSingleColumn(int id) {
		List<User> list = sessionFactory.getCurrentSession().createQuery(hql_12).setParameter(0, 1).list();
		list.forEach(e -> log.info("e========" + e));
	}
}
