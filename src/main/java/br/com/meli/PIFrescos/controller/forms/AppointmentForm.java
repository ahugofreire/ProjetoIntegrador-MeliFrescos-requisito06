package br.com.meli.PIFrescos.controller.forms;

import br.com.meli.PIFrescos.models.Address;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter @Setter
public class AppointmentForm {
    private Integer purchaseOrderId;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd' 'HH:mm:ss")
    private LocalDateTime appointmentDate;
    @NotNull(message = "Receiver can not be null")
    private String receiver;
    @NotNull(message = "Can not be null")
    private Boolean useRegistrationAddress;
    private Address address;
}
