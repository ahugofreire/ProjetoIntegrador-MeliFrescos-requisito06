package br.com.meli.PIFrescos.repository;

import br.com.meli.PIFrescos.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findAllByPurchaseOrder_UserId(Integer purchaseOrder_user_id);
}
