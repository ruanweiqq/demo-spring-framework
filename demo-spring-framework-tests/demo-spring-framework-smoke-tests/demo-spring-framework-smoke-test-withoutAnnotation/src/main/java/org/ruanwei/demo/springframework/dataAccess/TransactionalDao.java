package org.ruanwei.demo.springframework.dataAccess;

public interface TransactionalDao<T> {

	// Transaction
	void transactionalMethod1(T user);

	void transactionalMethod2(T user);

}
