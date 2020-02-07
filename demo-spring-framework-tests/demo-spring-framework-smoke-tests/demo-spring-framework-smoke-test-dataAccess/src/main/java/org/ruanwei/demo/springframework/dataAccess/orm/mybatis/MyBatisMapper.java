package org.ruanwei.demo.springframework.dataAccess.orm.mybatis;

import java.util.List;

import org.ruanwei.demo.springframework.dataAccess.dao.CrudDao;

/**
 * 
 * @author ruanwei
 *
 * @param <T>
 * @param <ID>
 */
public interface MyBatisMapper<T, ID> extends CrudDao<T, ID> {

	// ============将CrudDao中的Iterable具体化为List=============
	@Override
	List<T> findAll();

	@Override
	List<T> findAllById(Iterable<ID> ids);

	@Override
	List<T> findAllByGtId(ID id);

}
