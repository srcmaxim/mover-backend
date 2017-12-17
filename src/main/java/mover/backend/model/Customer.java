package mover.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * A Customer represents a person who uses company's service.
 */
@Entity
@Table(name = "customer")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "leads")
@ToString(callSuper = true, exclude = "leads")
public class Customer extends Person {
    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private Set<Lead> leads = new HashSet<>();
}
