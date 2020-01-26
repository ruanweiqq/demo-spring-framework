package org.ruanwei.demo.springframework.dataAccess.jdbc;

public interface TransactionnalDao<T> {

	// Create
	int save(T entity);

	// Transaction
	default void transactionalMethod1(T user) {
	};

	default void transactionalMethod2(T user) {
	};
}
