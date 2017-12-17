package mover.backend.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A Person represents basic information about Customer or Employee.
 */
@MappedSuperclass
@Data
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "firstname", nullable = false)
    private String firstname;

    @NotNull
    @Column(name = "lastname", nullable = false)
    private String lastname;

    @Column(name = "email")
    private String email;

    @NotNull
    @Column(name = "phone", nullable = false)
    private String phone;
}
