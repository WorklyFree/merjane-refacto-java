package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;


    @Test
    void processOrder_normalProduct_available() throws Exception {
        Long orderId = 1L;
        Product product = new Product();
        product.setId(101L);
        product.setName("Normal Product");
        product.setType("NORMAL");
        product.setAvailable(2);

        Set<Product> items = new HashSet<>();
        items.add(product);

        Order order = new Order();
        order.setId(orderId);
        order.setItems(items);

        orderRepository.save(order);
        productRepository.save(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{orderId}/processOrder", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(orderId));
    }

    @Test
    void processOrder_normalProduct_notAvailable_withLeadTime() throws Exception {
        Long orderId = 2L;
        Product product = new Product();
        product.setId(102L);
        product.setName("Normal Product Out Of Stock");
        product.setType("NORMAL");
        product.setAvailable(0);
        product.setLeadTime(5);

        Set<Product> items = new HashSet<>();
        items.add(product);

        Order order = new Order();
        order.setId(orderId);
        order.setItems(items);

        orderRepository.save(order);
        productRepository.save(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{orderId}/processOrder", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(orderId));
    }

    @Test
    void processOrder_seasonalProduct_inSeason_available() throws Exception {
        Long orderId = 3L;
        Product product = new Product();
        product.setId(103L);
        product.setName("Seasonal Product In Season");
        product.setType("SEASONAL");
        product.setAvailable(1);
        product.setSeasonStartDate(LocalDate.now().minusDays(1));
        product.setSeasonEndDate(LocalDate.now().plusDays(1));

        Set<Product> items = new HashSet<>();
        items.add(product);

        Order order = new Order();
        order.setId(orderId);
        order.setItems(items);

        orderRepository.save(order);
        productRepository.save(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{orderId}/processOrder", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(orderId));
    }

    @Test
    void processOrder_seasonalProduct_notInSeason_leadTimeExceedsEnd() throws Exception {
        Long orderId = 4L;
        Product product = new Product();
        product.setId(104L);
        product.setName("Seasonal Product Out Of Season - Lead Exceeds");
        product.setType("SEASONAL");
        product.setAvailable(1);
        product.setSeasonStartDate(LocalDate.now().minusDays(10));
        product.setSeasonEndDate(LocalDate.now().minusDays(5));
        product.setLeadTime(10);

        Set<Product> items = new HashSet<>();
        items.add(product);

        Order order = new Order();
        order.setId(orderId);
        order.setItems(items);

        orderRepository.save(order);
        productRepository.save(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{orderId}/processOrder", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(orderId));
    }

    @Test
    void processOrder_seasonalProduct_notInSeason_beforeStart() throws Exception {
        Long orderId = 5L;
        Product product = new Product();
        product.setId(105L);
        product.setName("Seasonal Product Before Season");
        product.setType("SEASONAL");
        product.setAvailable(1);
        product.setSeasonStartDate(LocalDate.now().plusDays(5));
        product.setSeasonEndDate(LocalDate.now().plusDays(10));
        product.setLeadTime(2);

        Set<Product> items = new HashSet<>();
        items.add(product);

        Order order = new Order();
        order.setId(orderId);
        order.setItems(items);

        orderRepository.save(order);
        productRepository.save(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{orderId}/processOrder", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(orderId));
    }

    @Test
    void processOrder_seasonalProduct_notInSeason_delay() throws Exception {
        Long orderId = 6L;
        Product product = new Product();
        product.setId(106L);
        product.setName("Seasonal Product Out Of Season - Delay");
        product.setType("SEASONAL");
        product.setAvailable(1);
        product.setSeasonStartDate(LocalDate.now().plusDays(2));
        product.setSeasonEndDate(LocalDate.now().plusDays(7));
        product.setLeadTime(1);

        Set<Product> items = new HashSet<>();
        items.add(product);

        Order order = new Order();
        order.setId(orderId);
        order.setItems(items);

        orderRepository.save(order);
        productRepository.save(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{orderId}/processOrder", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(orderId));
    }

    @Test
    void processOrder_expirableProduct_available_notExpired() throws Exception {
        Long orderId = 7L;
        Product product = new Product();
        product.setId(107L);
        product.setName("Expirable Product Not Expired");
        product.setType("EXPIRABLE");
        product.setAvailable(1);
        product.setExpiryDate(LocalDate.now().plusDays(5));

        Set<Product> items = new HashSet<>();
        items.add(product);

        Order order = new Order();
        order.setId(orderId);
        order.setItems(items);

        orderRepository.save(order);
        productRepository.save(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{orderId}/processOrder", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(orderId));
    }

    @Test
    void processOrder_expirableProduct_expired() throws Exception {
        Long orderId = 8L;
        Product product = new Product();
        product.setId(108L);
        product.setName("Expirable Product Expired");
        product.setType("EXPIRABLE");
        product.setAvailable(1);
        product.setExpiryDate(LocalDate.now().minusDays(1));

        Set<Product> items = new HashSet<>();
        items.add(product);

        Order order = new Order();
        order.setId(orderId);
        order.setItems(items);

        orderRepository.save(order);
        productRepository.save(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{orderId}/processOrder", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(orderId));
}

    @Test
    void processOrder_expirableProduct_notAvailable_notExpired() throws Exception {
        Long orderId = 9L;
        Product product = new Product();
        product.setId(109L);
        product.setName("Expirable Product Not Available");
        product.setType("EXPIRABLE");
        product.setAvailable(0);
        product.setExpiryDate(LocalDate.now().plusDays(2));

        Set<Product> items = new HashSet<>();
        items.add(product);

        Order order = new Order();
        order.setId(orderId);
        order.setItems(items);

        orderRepository.save(order);
        productRepository.save(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/orders/{orderId}/processOrder", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value(orderId));
    }

}