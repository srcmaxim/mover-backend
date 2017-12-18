package mover.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;
import mover.backend.annotation.DateRange;
import mover.backend.model.enumeration.Status;
import mover.backend.model.enumeration.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A lead represents a route from origin to destination in a specific time.
 */
@Entity
@Table(name = "lead")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(exclude = {"estimates","inventories", "customer", "assignedTos"})
@EqualsAndHashCode(exclude = {"id", "estimates","inventories", "customer", "assignedTos"})
@DateRange(before = "start", after = "end")
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "start", nullable = false)
    private ZonedDateTime start;

    @NotNull
    @Column(name = "end", nullable = false)
    private ZonedDateTime end;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Embedded
    @NotNull
    @AttributeOverrides({
            @AttributeOverride(name = "address", column = @Column(name = "origin_address")),
            @AttributeOverride(name = "longitude", column = @Column(name = "origin_longitude")),
            @AttributeOverride(name = "latitude", column = @Column(name = "origin_latitude"))
    })
    private Address origin;

    @Embedded
    @NotNull
    @AttributeOverrides({
            @AttributeOverride(name = "address", column = @Column(name = "destination_address")),
            @AttributeOverride(name = "longitude", column = @Column(name = "destination_longitude")),
            @AttributeOverride(name = "latitude", column = @Column(name = "destination_latitude"))
    })
    private Address destination;

    @ElementCollection
    private Set<Estimate> estimates = new HashSet<>();

    @ElementCollection
    private Set<Inventory> inventories = new HashSet<>();

    @ManyToOne
    @JsonIgnore
    private Customer customer;

    @ManyToMany
    @JoinTable(
            name = "lead_employee",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "lead_id")
    )
    @JsonIgnore
    private Set<Employee> assignedTos = new HashSet<>();
}
