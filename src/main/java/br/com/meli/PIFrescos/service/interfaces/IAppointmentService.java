package br.com.meli.PIFrescos.service.interfaces;

import br.com.meli.PIFrescos.models.Appointment;

import java.util.List;

public interface IAppointmentService {
    List<Appointment> findAllUserId(Integer userId);
    Appointment create(Appointment appointment);
    Appointment update(Integer id, Appointment appointment);
    Appointment findById(Integer id);
    void isValid(Appointment appointment);
}
