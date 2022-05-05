package br.com.meli.PIFrescos.controller.forms;

import br.com.meli.PIFrescos.models.ShippingStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
public class ShippingFrom {
    @NotNull(message = "Can not be null")
    private ShippingStatus shippingStatus;
    @NotNull(message = "Can not be null")
    private String description;
}
