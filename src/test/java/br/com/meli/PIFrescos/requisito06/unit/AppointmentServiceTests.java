package br.com.meli.PIFrescos.requisito06.unit;

import br.com.meli.PIFrescos.models.*;

import br.com.meli.PIFrescos.repository.AppointmentRepository;
import br.com.meli.PIFrescos.service.AppointmentServiceImpl;
import br.com.meli.PIFrescos.service.PurchaseOrderService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.meli.PIFrescos.models.OrderStatus.FINISHED;
import static br.com.meli.PIFrescos.models.OrderStatus.OPENED;
import static br.com.meli.PIFrescos.models.StorageType.FRESH;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTests {

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PurchaseOrderService purchaseOrderService;

    Address addressMock = new Address();
    User userMock = new User();

    Appointment appointmentMock = new Appointment();
    Shipping shippingMock = new Shipping();
    private Batch mockBatch1 = new Batch();
    private Batch mockBatch2 = new Batch();
    private List<ProductsCart> productsCartList = new ArrayList<>();
    private final Product product1 = new Product();
    private final Product product2 = new Product();

    private final PurchaseOrder purchaseOrder = new PurchaseOrder();

    @BeforeEach
    public void setUp() {

        addressMock.setId(1);
        addressMock.setStreet("Rua da neve");
        addressMock.setRegion("São Paulo");
        addressMock.setNumber("123");
        addressMock.setZipcode("01100-060");

        userMock.setFullname("John Test");
        userMock.setEmail("jonh@gmail.com");
        userMock.setId(1);
        userMock.setAddress(addressMock);

        product1.setProductId(1);
        product1.setProductName("Banana");
        product1.setProductType(FRESH);
        product1.setProductDescription("descriptionBanana");

        product1.setProductId(2);
        product2.setProductName("Maça");
        product2.setProductType(FRESH);
        product1.setProductDescription("description Maça");

        mockBatch1 = Batch.builder()
                .batchNumber(1)
                .product(product1)
                .currentQuantity(10)
                .unitPrice(BigDecimal.valueOf(10.0))
                .build();

        mockBatch2 = Batch.builder()
                .batchNumber(2)
                .product(product1)
                .currentQuantity(10)
                .unitPrice(BigDecimal.valueOf(25.0))
                .build();

        purchaseOrder.setId(1);
        purchaseOrder.setUser(userMock);
        purchaseOrder.setDate(LocalDate.of(2022, 1, 8));
        purchaseOrder.setOrderStatus(FINISHED);
        purchaseOrder.setCartList(productsCartList);

        appointmentMock.setId(1);
        appointmentMock.setReceiver("John Doe");
        appointmentMock.setUseRegistrationAddress(false);
        appointmentMock.setPurchaseOrder(purchaseOrder);
        appointmentMock.setShipping(shippingMock);
        appointmentMock.setAppointmentDate(LocalDateTime.parse("2022-05-30T13:00:00"));
        appointmentMock.setAddress(addressMock);
    }

    @Test
    @DisplayName("shouldBeAbleCreateAppointment")
    public void shouldBeAbleCreateAppointment() {
        Mockito.when(purchaseOrderService.getById(1)).thenReturn(purchaseOrder);
        Mockito.when(appointmentRepository.save(any())).thenReturn(appointmentMock);

        Appointment appointment =  appointmentService.create(appointmentMock);

        assertEquals(appointment, appointmentMock);

    }

    @Test
    @DisplayName("shouldBeAbleCreateAppointmentWithAddressUser")
    public void shouldBeAbleCreateAppointmentWithAddressUser() {
        Mockito.when(purchaseOrderService.getById(1)).thenReturn(purchaseOrder);
        appointmentMock.setUseRegistrationAddress(true);
        appointmentMock.setAddress(null);
        Mockito.when(appointmentRepository.save(any())).thenReturn(appointmentMock);

        Appointment appointment =  appointmentService.create(appointmentMock);

        assertEquals(appointment, appointmentMock);
        assertEquals(appointment.getAddress(), userMock.getAddress());
    }

    @Test
    @DisplayName("shouldBeAbleListAllAppointment")
    public void shouldBeAbleListAllAppointment() {

        List<Appointment> appointmentList = List.of(appointmentMock, appointmentMock);

        Mockito.when(appointmentRepository.findAllByPurchaseOrder_UserId(userMock.getId())).thenReturn(appointmentList);

        List<Appointment> appointments =  appointmentService.findAllUserId(userMock.getId());

        assertEquals(appointments, appointmentList);
        assertEquals(appointments.size(), 2);
    }

    @Test
    @DisplayName("shouldReturnRuntimeExceptionWhenOrderStatusIsDifferentFromFinished")
    public void shouldReturnRuntimeExceptionWhenOrderStatusIsDifferentFromFinished() {

        purchaseOrder.setOrderStatus(OPENED);
        Mockito.when(purchaseOrderService.getById(1)).thenReturn(purchaseOrder);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> appointmentService.create(appointmentMock));

        assertThat(exception.getMessage()).isEqualTo("Order status cannot be open");
    }

    @Test
    @DisplayName("shouldReturnRuntimeExceptionWhenAppointmentDateIsBeforeCurrentTime")
    public void shouldReturnRuntimeExceptionWhenAppointmentDateIsBeforeCurrentTime() {

        appointmentMock.setAppointmentDate(LocalDateTime.parse("2022-05-01T13:00:00"));
        Mockito.when(purchaseOrderService.getById(1)).thenReturn(purchaseOrder);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> appointmentService.create(appointmentMock));

        assertThat(exception.getMessage()).isEqualTo("Appointment date cannot be earlier than current date");
    }

    @Test
    @DisplayName("shouldBeAbleListAllAppointment")
    public void shouldBeAbleFindAppointmentById() {

        Mockito.when(appointmentRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(appointmentMock));

        Appointment appointment =  appointmentService.findById(1);

        assertEquals(appointment, appointmentMock);
        assertEquals(appointment.getId(), 1);
    }

    @Test
    @DisplayName("shouldReturnRuntimeExceptionWhenFindByIdDoesNotExistAppointment")
    public void shouldReturnRuntimeExceptionWhenFindByIdDoesNotExistAppointment() {

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> appointmentService.findById(1));

        assertThat(exception.getMessage()).isEqualTo("Appointment not found");
    }

    @Test
    @DisplayName("shouldReturnRuntimeExceptionWhenUpdateDoesNotExistAppointment")
    public void shouldReturnRuntimeExceptionWhenUpdateDoesNotExistAppointment() {

        Mockito.when(appointmentRepository.findById(any())).thenReturn(java.util.Optional.empty());
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> appointmentService.update(1,appointmentMock));

        assertThat(exception.getMessage()).isEqualTo("Does not exits Appointment");
    }

    @Test
    @DisplayName("shouldReturnRuntimeExceptionWhenUpdateAppointmentWithShippingStatusDifferentPreparing")
    public void shouldReturnRuntimeExceptionWhenUpdateAppointmentWithShippingStatusDifferentPreparing() {

        shippingMock.setShippingStatus(ShippingStatus.SENT);
        appointmentMock.setShipping(shippingMock);
        Mockito.when(appointmentRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(appointmentMock));

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> appointmentService.update(1, appointmentMock));

        assertThat(exception.getMessage()).isEqualTo("Order sent cannot be updated");
    }

    @Test
    @DisplayName("shouldBeAbleUpdateAppointmentById")
    public void shouldBeAbleUpdateAppointmentById() {

        Appointment appointmentOld = Appointment.builder().id(1)
                .appointmentDate(LocalDateTime.now())
                .address(addressMock)
                .shipping(shippingMock)
                .purchaseOrder(purchaseOrder)
                .receiver("user test")
                .build();


        Mockito.when(appointmentRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(appointmentMock));

        appointmentMock.setAppointmentDate(LocalDateTime.parse("2022-05-10T13:00:00"));
        appointmentMock.setAddress(addressMock);
        Mockito.when(appointmentRepository.save(any())).thenReturn(appointmentMock);

        Appointment appointmentResult = appointmentService.update(1, appointmentOld);

        assertEquals(appointmentResult, appointmentMock);
        assertEquals(appointmentResult.getId(), 1);
    }
}
