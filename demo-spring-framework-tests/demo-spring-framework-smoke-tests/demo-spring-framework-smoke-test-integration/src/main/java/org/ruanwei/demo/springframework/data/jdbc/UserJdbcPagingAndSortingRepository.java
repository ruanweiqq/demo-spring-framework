package org.ruanwei.demo.springframework.data.jdbc;

import org.ruanwei.demo.springframework.dataAccess.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * see also SimpleKeyValueRepository
 * 
 * @author ruanwei
 *
 */
@Transactional
@RepositoryDefinition(domainClass = User.class, idClass = Integer.class)
public interface UserJdbcPagingAndSortingRepository extends PagingAndSortingRepository<User, Integer> {

}
