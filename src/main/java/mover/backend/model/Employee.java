package mover.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A Employee represents a person who works in the company.
 */
@Entity
@Table(name = "employee")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "leads")
@ToString(callSuper = true, exclude = "leads")
public class Employee extends Person {
    @ManyToMany(mappedBy = "assignedTos")
    @JsonIgnore
    private Set<Lead> leads = new HashSet<>();
}
