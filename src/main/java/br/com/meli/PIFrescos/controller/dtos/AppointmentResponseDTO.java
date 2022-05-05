package br.com.meli.PIFrescos.controller.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
@Getter @Setter
@ToString
public class AppointmentResponseDTO {
    private String name;
    private Integer purchaseOrderId;
    private LocalDateTime appointmentDate;
    private String receiver;
    private Integer shippingId;
}
