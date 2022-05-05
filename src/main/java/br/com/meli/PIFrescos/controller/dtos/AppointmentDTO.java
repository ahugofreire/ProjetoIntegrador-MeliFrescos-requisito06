package br.com.meli.PIFrescos.controller.dtos;

import br.com.meli.PIFrescos.models.Address;
import br.com.meli.PIFrescos.models.ShippingStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter @Setter
public class AppointmentDTO {
    private String name;
    private Integer purchaseOrderId;
    private LocalDateTime appointmentDate;
    private String receiver;
    private Address address;
    private ShippingStatus shippingStatus;
    private String shippingDescription;
    private LocalDateTime shippingUpdatedAt;
}
