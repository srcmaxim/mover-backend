package mover.backend.repository;

import mover.backend.model.Lead;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data JPA repository for the Lead entity.
 */
public interface LeadRepository extends CrudRepository<Lead, Long> {
}
