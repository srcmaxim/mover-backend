package mover.backend.repository;

import mover.backend.model.Lead;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Lead entity.
 */
@Repository
public interface LeadRepository extends BaseReposetory<Lead,Long> {
}
