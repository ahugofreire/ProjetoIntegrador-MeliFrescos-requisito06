package br.com.meli.PIFrescos.requisito06.unit;

import br.com.meli.PIFrescos.models.Shipping;
import br.com.meli.PIFrescos.models.ShippingStatus;
import br.com.meli.PIFrescos.repository.ShippingRepository;
import br.com.meli.PIFrescos.service.ShippingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ShippingServiceTests {

    @InjectMocks
    private ShippingServiceImpl shippingService;

    @Mock
    private ShippingRepository shippingRepository;

    private final Shipping shippingMock = new Shipping();

    @BeforeEach
    public void setUp() {
        shippingMock.setId(1);
        shippingMock.setShippingStatus(ShippingStatus.PREPARING);
        shippingMock.setDescription("shipping description");
        shippingMock.setUpdatedAt(LocalDateTime.parse("2022-05-04T13:00:00"));

    }

    @Test
    @DisplayName("shouldBeAbleFindAllShipping")
    public void shouldBeAbleFindAllShipping() {

        List<Shipping> shippingList = List.of(shippingMock, shippingMock);

        Mockito.when(shippingRepository.findAll()).thenReturn(shippingList);

        List<Shipping> shippingResult = shippingService.findAll();

        assertEquals(shippingResult, shippingList);
        assertEquals(shippingResult.size(), 2);
    }

    @Test
    @DisplayName("shouldReturnExceptionWhenShippingListIsEmpty")
    public void shouldReturnExceptionWhenShippingListIsEmpty() {

        RuntimeException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> shippingService.findAll());

        assertThat(exception.getMessage()).isEqualTo("Shipping Not Found");
    }

    @Test
    @DisplayName("shouldBeAbleUpdateShippingById")
    public void shouldBeAbleUpdateShippingById() {
        Shipping shippingMock2 = new Shipping();
        shippingMock2.setId(1);
        shippingMock2.setShippingStatus(ShippingStatus.DELIVERED);
        shippingMock2.setDescription("shipping delivered");
        shippingMock2.setUpdatedAt(LocalDateTime.parse("2022-05-04T15:00:00"));

        Mockito.when(shippingRepository.findById(1)).thenReturn(java.util.Optional.of(shippingMock));


        Mockito.when(shippingRepository.save(any())).thenReturn(shippingMock2);

        Shipping shipping = shippingService.update(1, shippingMock2);

        assertEquals(shipping.getDescription(), shippingMock2.getDescription());
        assertEquals(shipping.getShippingStatus(), shippingMock2.getShippingStatus());
        assertEquals(shipping.getUpdatedAt(), shippingMock2.getUpdatedAt());
    }
}
