package br.com.meli.PIFrescos.config.modelMapper;

import br.com.meli.PIFrescos.controller.dtos.AppointmentDTO;
import br.com.meli.PIFrescos.controller.dtos.AppointmentResponseDTO;
import br.com.meli.PIFrescos.models.Appointment;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper getModelMapper() {
        var modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Appointment.class, AppointmentResponseDTO.class)
                    .addMapping(src -> src.getPurchaseOrder().getUser().getFullname(), AppointmentResponseDTO::setName);
        modelMapper.createTypeMap(Appointment.class, AppointmentDTO.class)
                .addMapping(src -> src.getPurchaseOrder().getUser().getFullname(), AppointmentDTO::setName);
        return modelMapper;
    }
}
