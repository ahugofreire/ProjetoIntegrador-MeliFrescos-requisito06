package br.com.meli.PIFrescos.controller;

import br.com.meli.PIFrescos.config.security.TokenService;
import br.com.meli.PIFrescos.controller.dtos.AppointmentDTO;
import br.com.meli.PIFrescos.controller.dtos.AppointmentResponseDTO;
import br.com.meli.PIFrescos.controller.forms.AppointmentForm;
import br.com.meli.PIFrescos.models.Appointment;
import br.com.meli.PIFrescos.service.interfaces.IAppointmentService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(AppointmentController.baseURI)
@AllArgsConstructor
public class AppointmentController {

    public static final String baseURI = "/fresh-products/orders/appointments";

    private final IAppointmentService appointmentService;
    private final ModelMapper modelMapper;
    private final TokenService tokenService;

    @PostMapping("")
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @Valid @RequestBody AppointmentForm appointmentForm, UriComponentsBuilder uriBuilder) {

        Appointment appointment = modelMapper.map(appointmentForm, Appointment.class);

        Appointment appointmentCreated = appointmentService.create(appointment);

        URI uri = uriBuilder.path(AppointmentController.baseURI.concat("/{id}"))
                .buildAndExpand(appointmentCreated.getId())
                .toUri();

        AppointmentResponseDTO appointmentResponseDTO = modelMapper.map(appointmentCreated, AppointmentResponseDTO.class);

        return ResponseEntity.created(uri).body(appointmentResponseDTO);
    }

    @GetMapping("")
    public ResponseEntity<List<AppointmentResponseDTO>> findAllAppointments() {

       List<Appointment> appointmentDeliveries = this.appointmentService.findAllUserId(tokenService.getUserLogged().getId());

        List<AppointmentResponseDTO> appointmentResponseDTOS = appointmentDeliveries.stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(appointmentResponseDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> findAppointmentById(@PathVariable Integer id) {

        Appointment appointment = this.appointmentService.findById(id);

        AppointmentDTO appointmentDTO = modelMapper.map(appointment, AppointmentDTO.class);

        return ResponseEntity.ok().body(appointmentDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> findAppointmentById(@PathVariable Integer id,
                                                              @RequestBody  AppointmentForm appointmentForm) {

        Appointment appointment = modelMapper.map(appointmentForm, Appointment.class);
        Appointment appointmentResult = this.appointmentService.update(id, appointment);

        AppointmentDTO appointmentDTO = modelMapper.map(appointmentResult, AppointmentDTO.class);

        return ResponseEntity.ok().body(appointmentDTO);
    }
}