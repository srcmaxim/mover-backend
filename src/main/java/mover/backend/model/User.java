package mover.backend.model;


import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * AjaxLoginProcessingFilter User class exist for authentication purposes.
 */
@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true, exclude = "password")
@ToString(callSuper = true, exclude = "password")
public class User extends Person {

    @NotNull
    @Column(name = "username", unique = true)
    private String username;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @ElementCollection(targetClass=Role.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name="role")
    @Column(name="name")
    private List<Role> roles;
}