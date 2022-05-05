package br.com.meli.PIFrescos.requisito06.integration;

import br.com.meli.PIFrescos.controller.dtos.AppointmentResponseDTO;
import br.com.meli.PIFrescos.controller.dtos.TokenDto;
import br.com.meli.PIFrescos.controller.forms.AppointmentForm;
import br.com.meli.PIFrescos.controller.forms.LoginForm;
import br.com.meli.PIFrescos.models.*;
import br.com.meli.PIFrescos.repository.AppointmentRepository;
import br.com.meli.PIFrescos.repository.PurchaseOrderRepository;
import br.com.meli.PIFrescos.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.meli.PIFrescos.models.OrderStatus.FINISHED;
import static br.com.meli.PIFrescos.models.StorageType.FRESH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Requisito 06 teste de intregração;
 * @author Antonio Hugo
 *
 */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public class AppointmentControllerTests {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AppointmentRepository appointmentRepository;

    @MockBean
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User userMock = new User();
    private Address addressMock = new Address();
    private Profile profileMock = new Profile();
    private String accessToken;

    Appointment appointmentMock = new Appointment();
    Shipping shippingMock = new Shipping();
    private Batch mockBatch1 = new Batch();
    private Batch mockBatch2 = new Batch();
    private List<ProductsCart> productsCartList = new ArrayList<>();
    private final Product product1 = new Product();
    private final Product product2 = new Product();
    private final PurchaseOrder purchaseOrder = new PurchaseOrder();

    @BeforeEach
    public void setUp() throws Exception {
        profileMock.setId(1L);
        profileMock.setName("ADMIN");

        addressMock.setId(1);
        addressMock.setStreet("Rua da neve");
        addressMock.setRegion("São Paulo");
        addressMock.setNumber("123");
        addressMock.setZipcode("01100-060");

        userMock.setId(1);
        userMock.setEmail("meli@gmail.com");
        userMock.setFullname("John Doe");
        userMock.setPassword("$2a$10$GtzVniP9dVMmVW2YxytuvOG9kHu9nrwAxe8/UXSFkaECmIJ4UJcHy");
        userMock.setAddress(addressMock);
        userMock.setProfiles(List.of(profileMock));

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
        appointmentMock.setReceiver("John Tre");
        appointmentMock.setUseRegistrationAddress(false);
        appointmentMock.setPurchaseOrder(purchaseOrder);
        appointmentMock.setShipping(shippingMock);
        appointmentMock.setAppointmentDate(LocalDateTime.parse("2022-05-30T13:00:00"));
        appointmentMock.setAddress(addressMock);

        this.accessToken = this.userLogin();
    }

    /**
     * This method returned the mock user token.
     * @return String
     * @author Antonio Hugo
     *
     */
    private String userLogin() throws Exception {
        String userLogin = "{"
                + "\"email\": \"meli@gmail.com\", "
                + "\"password\": \"123456\""
                + "}";

        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(userMock));
        MvcResult result = mockMvc.perform(post("/auth")
                .contentType("application/json").content(userLogin))
                .andExpect(status().isOk())
                .andReturn();

        TypeReference<TokenDto> typeReference = new TypeReference<TokenDto>() {};
        TokenDto token = objectMapper.readValue(result.getResponse().getContentAsString(), typeReference);

        return "Bearer " + token.getToken();
    }

    @Test
    @DisplayName("shouldBeAbleCreateAppointment")
    public void shouldBeAbleCreateAppointment() throws Exception {

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userMock));

        AppointmentForm appointment = new AppointmentForm();
                appointment.setPurchaseOrderId(1);
                appointment.setAppointmentDate(LocalDateTime.parse("2022-05-30T13:00:00"));
                appointment.setReceiver("John Tre");
                appointment.setUseRegistrationAddress(false);
                appointment.setAddress(addressMock);

        objectMapper.registerModule(new JavaTimeModule());
        String payload = objectMapper.writeValueAsString(appointment);

        Mockito.when(purchaseOrderRepository.findById(1)).thenReturn(Optional.of(purchaseOrder));
        Mockito.when(appointmentRepository.save(any())).thenReturn(appointmentMock);

        mockMvc.perform(post("/fresh-products/orders/appointments")
                .header("Authorization",  accessToken)
                .contentType("application/json").content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.appointmentDate").value("2022-05-30T13:00:00"))
                .andExpect(jsonPath("$.receiver").value("John Tre"))
                .andReturn();
    }

    @Test
    @DisplayName("shouldNotBeAbleCreateAppointmentWhenPurchaseOrderIdIsNull")
    public void shouldNotBeAbleCreateAppointmentWhenPurchaseOrderIdIsNull() throws Exception {

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userMock));

        AppointmentForm appointment = new AppointmentForm();
        appointment.setPurchaseOrderId(null);
        appointment.setAppointmentDate(LocalDateTime.parse("2022-05-30T13:00:00"));
        appointment.setReceiver("John Tre");
        appointment.setUseRegistrationAddress(false);
        appointment.setAddress(addressMock);

        objectMapper.registerModule(new JavaTimeModule());
        String payload = objectMapper.writeValueAsString(appointment);

        Mockito.when(purchaseOrderRepository.findById(1)).thenReturn(Optional.of(purchaseOrder));
        Mockito.when(appointmentRepository.save(any())).thenReturn(appointmentMock);

        mockMvc.perform(post("/fresh-products/orders/appointments")
                .header("Authorization",  accessToken)
                .contentType("application/json").content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].field").value("purchaseOrderId"))
                .andExpect(jsonPath("$[*].error").value("Can not be null"))
                .andReturn();
    }

    @Test
    @DisplayName("shouldNotBeAbleCreateAppointmentWhenReceiverIsNull")
    public void shouldNotBeAbleCreateAppointmentWhenReceiverIsNull() throws Exception {

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userMock));

        AppointmentForm appointment = new AppointmentForm();
        appointment.setPurchaseOrderId(1);
        appointment.setAppointmentDate(LocalDateTime.parse("2022-05-30T13:00:00"));
        appointment.setReceiver(null);
        appointment.setUseRegistrationAddress(false);
        appointment.setAddress(addressMock);

        objectMapper.registerModule(new JavaTimeModule());
        String payload = objectMapper.writeValueAsString(appointment);

        Mockito.when(purchaseOrderRepository.findById(1)).thenReturn(Optional.of(purchaseOrder));
        Mockito.when(appointmentRepository.save(any())).thenReturn(appointmentMock);

        mockMvc.perform(post("/fresh-products/orders/appointments")
                .header("Authorization",  accessToken)
                .contentType("application/json").content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].field").value("receiver"))
                .andExpect(jsonPath("$[*].error").value("Receiver can not be null"))
                .andReturn();
    }

    @Test
    @DisplayName("shouldNotBeAbleCreateAppointmentWhenAppointmentDateNull")
    public void shouldNotBeAbleCreateAppointmentWhenAppointmentDateIsNull() throws Exception {

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userMock));

        AppointmentForm appointment = new AppointmentForm();
        appointment.setPurchaseOrderId(1);
        appointment.setAppointmentDate(null);
        appointment.setReceiver("John Tre");
        appointment.setUseRegistrationAddress(false);
        appointment.setAddress(addressMock);

        objectMapper.registerModule(new JavaTimeModule());
        String payload = objectMapper.writeValueAsString(appointment);

        Mockito.when(purchaseOrderRepository.findById(1)).thenReturn(Optional.of(purchaseOrder));
        Mockito.when(appointmentRepository.save(any())).thenReturn(appointmentMock);

        mockMvc.perform(post("/fresh-products/orders/appointments")
                .header("Authorization",  accessToken)
                .contentType("application/json").content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].field").value("appointmentDate"))
                .andExpect(jsonPath("$[*].error").value("Can not be null"))
                .andReturn();
    }

    @Test
    @DisplayName("shouldNotBeAbleCreateAppointmentWhenAppointmentDateEarlierCurrentDate")
    public void shouldNotBeAbleCreateAppointmentWhenAppointmentDateEarlierCurrentDate() throws Exception {

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userMock));

        AppointmentForm appointment = new AppointmentForm();
        appointment.setPurchaseOrderId(1);
        appointment.setAppointmentDate(LocalDateTime.parse("2022-05-01T13:00:00"));
        appointment.setReceiver("John Tre");
        appointment.setUseRegistrationAddress(false);
        appointment.setAddress(addressMock);

        objectMapper.registerModule(new JavaTimeModule());
        String payload = objectMapper.writeValueAsString(appointment);

        Mockito.when(purchaseOrderRepository.findById(1)).thenReturn(Optional.of(purchaseOrder));
        Mockito.when(appointmentRepository.save(any())).thenReturn(appointmentMock);

        mockMvc.perform(post("/fresh-products/orders/appointments")
                .header("Authorization",  accessToken)
                .contentType("application/json").content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Appointment date cannot be earlier than current date"))
                .andReturn();
    }

    @Test
    @DisplayName("shouldNotBeAbleCreateAppointmentWhenUseRegistrationAddressIsNull")
    public void shouldNotBeAbleCreateAppointmentWhenUseRegistrationAddressIsNull() throws Exception {

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userMock));

        AppointmentForm appointment = new AppointmentForm();
        appointment.setPurchaseOrderId(1);
        appointment.setAppointmentDate(LocalDateTime.parse("2022-07-05T13:00:00"));
        appointment.setReceiver("John Tre");
        appointment.setUseRegistrationAddress(null);
        appointment.setAddress(addressMock);

        objectMapper.registerModule(new JavaTimeModule());
        String payload = objectMapper.writeValueAsString(appointment);

        Mockito.when(purchaseOrderRepository.findById(1)).thenReturn(Optional.of(purchaseOrder));
        Mockito.when(appointmentRepository.save(any())).thenReturn(appointmentMock);

        mockMvc.perform(post("/fresh-products/orders/appointments")
                .header("Authorization",  accessToken)
                .contentType("application/json").content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].field").value("useRegistrationAddress"))
                .andExpect(jsonPath("$[*].error").value("Can not be null"))
                .andReturn();
    }

    @Test
    @DisplayName("shouldBeAbleListAllAppointmentByUser")
    public void shouldBeAbleListAllAppointmentByUser() throws Exception {

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userMock));
        Mockito.when(purchaseOrderRepository.findById(1)).thenReturn(Optional.of(purchaseOrder));

        List<Appointment> expected = List.of(appointmentMock, appointmentMock);

        Mockito.when(appointmentRepository.findAllByPurchaseOrder_UserId(any())).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/fresh-products/orders/appointments")
                .header("Authorization",  accessToken))
                .andExpect(status().isOk())
                .andReturn();
        objectMapper.registerModule(new JavaTimeModule());
        TypeReference<List<AppointmentResponseDTO>> typeReference = new TypeReference<List<AppointmentResponseDTO>>() {};
        List<AppointmentResponseDTO> appointments = objectMapper.readValue(result.getResponse().getContentAsString(), typeReference);

        assertThat(appointments).isNotNull();
        assertThat(appointments).hasSize(2);

    }

    @Test
    @DisplayName("shouldBeAbleFindByAppointmentId")
    public void shouldBeAbleFindByAppointmentId() throws Exception {

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userMock));
        Mockito.when(purchaseOrderRepository.findById(1)).thenReturn(Optional.of(purchaseOrder));


        Mockito.when(appointmentRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(appointmentMock));

        mockMvc.perform(get("/fresh-products/orders/appointments/1")
                .header("Authorization",  accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.purchaseOrderId").value(1))
                .andExpect(jsonPath("$.appointmentDate").value("2022-05-30T13:00:00"))
                .andExpect(jsonPath("$.receiver").value("John Tre"))
                .andReturn();
    }

    @Test
    @DisplayName("shouldBeAbleUpdateAppointmentById")
    public void shouldBeAbleUpdateAppointmentById() throws Exception {

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userMock));

        AppointmentForm appointment = new AppointmentForm();
        appointment.setPurchaseOrderId(1);
        appointment.setAppointmentDate(LocalDateTime.parse("2022-07-05T18:00:00"));
        appointment.setReceiver("John Test");
        appointment.setAddress(addressMock);

        objectMapper.registerModule(new JavaTimeModule());
        String payload = objectMapper.writeValueAsString(appointment);

        Mockito.when(purchaseOrderRepository.findById(1)).thenReturn(Optional.of(purchaseOrder));
        Mockito.when(appointmentRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(appointmentMock));
        appointmentMock.setAppointmentDate(LocalDateTime.parse("2022-07-05T18:00:00"));
        appointmentMock.setReceiver("John Test");

        Mockito.when(appointmentRepository.save(any())).thenReturn(appointmentMock);

        mockMvc.perform(put("/fresh-products/orders/appointments/1")
                .header("Authorization",  accessToken)
                .contentType("application/json").content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receiver").value("John Test"))
                .andExpect(jsonPath("$.purchaseOrderId").value(1))
                .andExpect(jsonPath("$.appointmentDate").value("2022-07-05T18:00:00"))
                .andReturn();
    }
}
