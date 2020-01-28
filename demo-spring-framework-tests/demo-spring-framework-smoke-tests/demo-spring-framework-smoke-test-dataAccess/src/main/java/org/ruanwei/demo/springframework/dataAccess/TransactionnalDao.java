package org.ruanwei.demo.springframework.dataAccess;

public interface TransactionnalDao<T> {

	// Transaction
	default void transactionalMethod1(T user) {
		throw new UnsupportedOperationException();
	};

	default void transactionalMethod2(T user) {
		throw new UnsupportedOperationException();
	};

}
