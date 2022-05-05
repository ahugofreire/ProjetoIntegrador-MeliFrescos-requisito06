package br.com.meli.PIFrescos.service.interfaces;

import br.com.meli.PIFrescos.models.Shipping;

import java.util.List;

public interface IShippingService {
    Shipping update(Integer id, Shipping shipping);
    List<Shipping> findAll();
}
