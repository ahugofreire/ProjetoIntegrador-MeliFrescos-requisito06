package br.com.meli.PIFrescos.controller.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter @Setter
public class AppointmentDTO {
    private String name;
    private Integer purchaseOrderId;
    private LocalDateTime appointmentDate;
    private String receiver;
}
