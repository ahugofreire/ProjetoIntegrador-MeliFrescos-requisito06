package br.com.meli.PIFrescos.repository;

import br.com.meli.PIFrescos.models.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingRepository extends JpaRepository<Shipping, Integer> {
}
