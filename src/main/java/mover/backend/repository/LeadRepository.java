package mover.backend.repository;

import mover.backend.model.Estimate;
import mover.backend.model.Lead;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Lead entity.
 */
@Repository
public interface LeadRepository extends CrudRepository<Lead,Long> {
    Iterable<Estimate> findEstimatesById(Long id);
}
