package mover.backend.model;

import lombok.Data;
import mover.backend.model.enumeration.Category;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

/**
 * A Inventory.
 */
@Embeddable
@Data
public class Inventory {
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "weight", nullable = false)
    private Integer weight;

    @NotNull
    @Column(name = "volume", nullable = false)
    private Integer volume;
}
