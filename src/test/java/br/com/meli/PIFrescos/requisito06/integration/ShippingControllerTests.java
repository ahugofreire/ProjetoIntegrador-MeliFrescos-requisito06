package br.com.meli.PIFrescos.requisito06.integration;

import br.com.meli.PIFrescos.controller.dtos.TokenDto;
import br.com.meli.PIFrescos.controller.forms.ShippingFrom;
import br.com.meli.PIFrescos.models.*;
import br.com.meli.PIFrescos.repository.ShippingRepository;
import br.com.meli.PIFrescos.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
public class ShippingControllerTests {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ShippingRepository shippingRepository;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User userMock = new User();
    private Address addressMock = new Address();
    private Profile profileMock = new Profile();
    private String accessToken;

    Appointment appointmentMock = new Appointment();
    Shipping shippingMock = new Shipping();
    Shipping shippingMock2 = new Shipping();

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

        shippingMock2.setId(1);
        shippingMock2.setShippingStatus(ShippingStatus.SENT);
        shippingMock2.setDescription("Status description");
        shippingMock2.setUpdatedAt(LocalDateTime.parse("2022-05-04T13:00:00"));

        this.accessToken = this.userLogin();
    }

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
    @DisplayName("shouldBeAbleFindAllShipping")
    public void shouldBeAbleFindAllShipping() throws Exception {

        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(userMock));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userMock));
        List<Shipping> shippingList = List.of(shippingMock, shippingMock2);

        Mockito.when(shippingRepository.findAll()).thenReturn(shippingList);

        MvcResult result = mockMvc.perform(get("/fresh-products/orders/shipping")
                .header("Authorization",  accessToken))
                .andExpect(status().isOk())
                .andReturn();

        objectMapper.registerModule(new JavaTimeModule());
        TypeReference<List<Shipping>> typeReference = new TypeReference<List<Shipping>>() {};
        List<Shipping> shippingListResult = objectMapper.readValue(result.getResponse().getContentAsString(), typeReference);

        assertThat(shippingListResult).isNotNull();
        assertThat(shippingListResult).hasSize(2);
    }

    @Test
    @DisplayName("shouldBeAbleUpdateShipping")
    public void shouldBeAbleUpdateShipping() throws Exception {

        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(userMock));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(userMock));

        ShippingFrom shippingFrom = new ShippingFrom();
        shippingFrom.setShippingStatus(ShippingStatus.SENT);
        shippingFrom.setDescription("Status description");

        String payload = objectMapper.writeValueAsString(shippingFrom);

        Mockito.when(shippingRepository.findById(any())).thenReturn(Optional.ofNullable(shippingMock2));
        Mockito.when(shippingRepository.save(any())).thenReturn(shippingMock2);

       mockMvc.perform(put("/fresh-products/orders/shipping/1")
                .header("Authorization",  accessToken)
                .contentType("application/json").content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.shippingStatus").value("SENT"))
                .andExpect(jsonPath("$.description").value("Status description"))
                .andReturn();
    }
}
