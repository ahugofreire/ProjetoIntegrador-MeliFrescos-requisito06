package br.com.meli.PIFrescos.config.modelMapper;

import br.com.meli.PIFrescos.controller.dtos.AppointmentDTO;
import br.com.meli.PIFrescos.models.AppointmentDelivery;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper getModelMapper() {
        var modelMapper = new ModelMapper();
            modelMapper.createTypeMap(AppointmentDelivery.class, AppointmentDTO.class)
                    .addMapping(src -> src.getPurchaseOrder().getUser().getFullname(), AppointmentDTO::setName);
        return modelMapper;
    }
}
