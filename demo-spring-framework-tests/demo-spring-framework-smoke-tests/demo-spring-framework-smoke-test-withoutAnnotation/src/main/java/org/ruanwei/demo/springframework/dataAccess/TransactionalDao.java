package org.ruanwei.demo.springframework.dataAccess;

public interface TransactionalDao<T> {

	// Transaction
	// 1.事务是默认在抛出运行时异常进行回滚的，因此不能在事务方法中进行try-catch捕获
	// 2.事务是通过代理目标对象实现的，因此只有调用代理的事务方法才生效，调用目标对象(例如同一类中的其他方法)没有事务
	// 3.由于事务传播类型不同，transactionalMethod1会回滚，transactionalMethod2不会回滚
	void transactionalMethod1(T user);

	void transactionalMethod2(T user);

}
