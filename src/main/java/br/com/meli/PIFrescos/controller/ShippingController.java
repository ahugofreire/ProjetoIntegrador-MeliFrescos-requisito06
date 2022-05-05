package br.com.meli.PIFrescos.controller;

import br.com.meli.PIFrescos.controller.forms.ShippingFrom;
import br.com.meli.PIFrescos.models.Shipping;
import br.com.meli.PIFrescos.service.interfaces.IShippingService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fresh-products/orders/shipping")
@AllArgsConstructor
public class ShippingController {

    private final IShippingService shippingService;
    private final ModelMapper modelMapper;

    @GetMapping("")
    public ResponseEntity<List<Shipping>> findAll() {
        return ResponseEntity.ok().body(this.shippingService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shipping> updateShipping(@PathVariable Integer id, @RequestBody ShippingFrom shippingFrom) {

        Shipping shipping = modelMapper.map(shippingFrom, Shipping.class);

        return ResponseEntity.ok().body(this.shippingService.update(id, shipping));
    }

}
