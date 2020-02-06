package org.ruanwei.demo.springframework.dataAccess;

import java.lang.reflect.ParameterizedType;

public interface Dao<T, ID> {

	default Class<T> getTClass() {
		@SuppressWarnings("unchecked")
		Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		return tClass;
	}
}
