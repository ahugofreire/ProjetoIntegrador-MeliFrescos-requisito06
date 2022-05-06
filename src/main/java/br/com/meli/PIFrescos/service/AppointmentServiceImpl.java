package br.com.meli.PIFrescos.service;

import br.com.meli.PIFrescos.models.*;
import br.com.meli.PIFrescos.repository.AppointmentRepository;
import br.com.meli.PIFrescos.service.interfaces.IAppointmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AppointmentServiceImpl implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final PurchaseOrderService purchaseOrderService;

    @Override
    public List<Appointment> findAllUserId(Integer userid) {

        List<Appointment> appointments = this.appointmentRepository.findAllByPurchaseOrder_UserId(userid);

        if(appointments.isEmpty()) {
            throw new EntityNotFoundException("Appointment Not Found");
        }

        return appointments;
    }

    @Override
    public Appointment create(Appointment appointment) {
        this.isValid(appointment);

        Shipping shipping = new Shipping();
        appointment.setShipping(shipping);

        if (appointment.getUseRegistrationAddress()) {
            PurchaseOrder purchaseOrder = this.purchaseOrderService.getById(appointment.getPurchaseOrder().getId());
            Address address = purchaseOrder.getUser().getAddress();
            appointment.setAddress(address);
        }

        return this.appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment update(Integer id, Appointment appointment) {
        Appointment appointmentOp = this.appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Does not exits Appointment"));

        if (appointmentOp.getShipping().getShippingStatus() != ShippingStatus.PREPARING) {
            throw new RuntimeException("Order sent cannot be updated");
        }

        Address address = appointmentOp.getAddress();
        address.setNumber(appointment.getAddress().getNumber());
        address.setRegion(appointment.getAddress().getRegion());
        address.setStreet(appointment.getAddress().getStreet());
        address.setZipcode(appointment.getAddress().getZipcode());

        appointmentOp.setReceiver(appointment.getReceiver());
        appointmentOp.setAppointmentDate(appointment.getAppointmentDate());
        appointmentOp.setAddress(address);

        return  this.appointmentRepository.save(appointmentOp);
    }

    @Override
    public Appointment findById(Integer id) {
       return this.appointmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
    }

    @Override
    public void isValid(Appointment appointment) {
        PurchaseOrder purchaseOrder = this.purchaseOrderService.getById(appointment.getPurchaseOrder().getId());

        if (purchaseOrder.getOrderStatus() != OrderStatus.FINISHED) {
            throw  new RuntimeException("Order status cannot be open");
        }

        if(appointment.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw  new RuntimeException("Appointment date cannot be earlier than current date");
        }

    }
}
