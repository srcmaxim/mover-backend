package mover.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * A Person represents basic information about Customer or Employee.
 */
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(exclude = "id")
public class Person {

    private static final String EMAIL_PATTERN =
            "[A-z0-9]+(\\-[A-z0-9]+|\\.[A-z0-9]+|\\_[A-z0-9]+)*@[A-z0-9]{2,}(\\.[A-z]{2,})+";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "firstname", nullable = false)
    private String firstName;

    @NotNull
    @Column(name = "lastname", nullable = false)
    private String lastName;

    @NotNull
    @Pattern(regexp = EMAIL_PATTERN)
    @Column(name = "email")
    private String email;

    @NotNull
    @Column(name = "phone", nullable = false)
    private String phone;
}
