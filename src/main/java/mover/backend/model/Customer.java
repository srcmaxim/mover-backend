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
 * A Customer.
 */
@Entity
@Table(name = "customer")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Customer extends Person {
    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private Set<Lead> leads = new HashSet<>();
}
