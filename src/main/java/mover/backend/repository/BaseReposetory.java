package mover.backend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Custom repository used for adding custom Data JPA methods for all repositories.
 */
@NoRepositoryBean
public interface BaseReposetory<T, ID> extends CrudRepository<T, ID> {

    void saveAndFlush(T entity);

    List<T> findAllByOrderByIdAsc();
}
