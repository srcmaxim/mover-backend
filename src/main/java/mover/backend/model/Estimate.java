package mover.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * A Estimate represents additional products used in single Lead.
 * For example: Big box, Packing tape.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(of = "name")
public class Estimate {
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @NotNull
    @Column(name = "price", nullable = false)
    private int price;
}
