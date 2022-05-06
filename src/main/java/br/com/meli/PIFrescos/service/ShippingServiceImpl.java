package br.com.meli.PIFrescos.service;

import br.com.meli.PIFrescos.models.Shipping;
import br.com.meli.PIFrescos.repository.ShippingRepository;
import br.com.meli.PIFrescos.service.interfaces.IShippingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@AllArgsConstructor
public class ShippingServiceImpl implements IShippingService {
    private final ShippingRepository shippingRepository;

    @Override
    public Shipping update(Integer id, Shipping shipping) {
        Shipping shippingOp = this.shippingRepository.findById(id).orElseThrow(() -> new RuntimeException("Shipping Not Found"));

        shippingOp.setShippingStatus(shipping.getShippingStatus());
        shippingOp.setDescription(shipping.getDescription());

        return this.shippingRepository.save(shippingOp);
    }

    @Override
    public List<Shipping> findAll() {
        List<Shipping> shippingList =  this.shippingRepository.findAll();

        if(shippingList.isEmpty()) {
            throw new EntityNotFoundException("Shipping Not Found");
        }

        return  shippingList;
    }
}
