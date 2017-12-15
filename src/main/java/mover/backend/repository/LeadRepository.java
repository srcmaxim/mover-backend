package mover.backend.repository;

import mover.backend.model.Lead;
import org.springframework.data.repository.CrudRepository;

public interface LeadRepository extends CrudRepository<Lead, Long> {
}
