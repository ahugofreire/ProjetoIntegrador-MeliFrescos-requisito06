package br.com.meli.PIFrescos.controller.forms;

import br.com.meli.PIFrescos.models.Batch;
import br.com.meli.PIFrescos.models.ProductsCart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCartForm {

    @NotNull(message = "O batchId não pode ser nulo.")
    private Integer batchId;
    @NotNull(message = "O batchId não pode ser nulo.")
    @Min(value = 0, message = "A quantidade não deve ser negativa.")
    private Integer quantity;

    /**
     * Converter a compra do usuario para o ProductsCart, o qual contem toda informaçao necessaria.
     * Muitas informaçoes são desconhecidas - buscar estes dados na camada de serviço.
     * @param productCartForm
     * @return productCart com valores vazios
     * @author Felipe Myose
     */
    public static ProductsCart convert(ProductCartForm productCartForm) {

        Batch batch = new Batch();
        batch.setBatchNumber(productCartForm.getBatchId());
        ProductsCart productsCart = new ProductsCart();

        productsCart.setBatch(batch);
        productsCart.setQuantity(productCartForm.getQuantity());

        return  productsCart;
    }
}
