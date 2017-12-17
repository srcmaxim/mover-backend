package mover.backend.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A Address represents address in string format and also longitude and latitude of it.
 */
@Embeddable
@Data
public class Address implements Serializable {
    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @NotNull
    @Column(name = "longitude", nullable = false)
    private String longitude;

    @NotNull
    @Column(name = "latitude", nullable = false)
    private String latitude;
}
