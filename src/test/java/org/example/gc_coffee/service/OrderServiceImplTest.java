package org.example.gc_coffee.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.gc_coffee.dto.OrderDto;
import org.example.gc_coffee.dto.OrderProductDto;
import org.example.gc_coffee.entity.Order;
import org.example.gc_coffee.entity.OrderProduct;
import org.example.gc_coffee.entity.Product;
import org.example.gc_coffee.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void testGetOrderByEmail_ShouldReturnOrderDto_WhenOrdersExist() {
        // Given
        String email = "test@example.com";
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .email(email)
                .address("Test Address")
                .postcode("12345")
                .orderStatus("PENDING")
                .orderProducts(new ArrayList<>())
                .build();

        when(orderRepository.findAllByEmailWithOrderProducts(email)).thenReturn(List.of(order));

        // When
        List<OrderDto> result = orderService.getOrderByEmail(email);

        // Then
        assertNotNull(result);
        for (OrderDto orderDto : result) {
            assertEquals(email, orderDto.getEmail());
        }
        verify(orderRepository, times(1)).findAllByEmailWithOrderProducts(email);
    }

    @Test
    void testRegisterOrder_ShouldThrowException_WhenProductNotFound() {
        // Given
        OrderDto orderDto = OrderDto.builder()
                .email("test@example.com")
                .address("Test Address")
                .postcode("12345")
                .orderStatus("PENDING")
                .orderProducts(Collections.singletonList(
                        OrderProductDto.builder()
                                .productId(UUID.randomUUID())
                                .category("Coffee")
                                .price(20000L)
                                .quantity(2)
                                .build()
                ))
                .build();

        when(productService.getProductByIds(any())).thenReturn(Collections.emptyMap());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> orderService.registerOrder(orderDto));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetAllOrders_ShouldReturnListOfOrderDtos_WhenOrdersExist() {
        // Given
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .address("Test Address")
                .postcode("12345")
                .orderStatus("PENDING")
                .orderProducts(new ArrayList<>())
                .build();

        when(orderRepository.findAllWithOrderProducts()).thenReturn(Collections.singletonList(order));

        // When
        List<OrderDto> result = orderService.getAllOrders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findAllWithOrderProducts();
    }
}