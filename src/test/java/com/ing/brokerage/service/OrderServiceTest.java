package com.ing.brokerage.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ing.brokerage.entity.Order;
import com.ing.brokerage.functions.order.CreateOrderFn;
import com.ing.brokerage.functions.order.DeleteOrderFn;
import com.ing.brokerage.messaging.publisher.OrderPublisher;
import com.ing.brokerage.model.OrderRequestDTO;
import com.ing.brokerage.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
  @Mock
  private OrderRepository orderRepository;
  @Mock
  private OrderPublisher orderPublisher;
  @Mock
  private CreateOrderFn createOrderFn;
  @Mock
  private DeleteOrderFn deleteOrderFn;
  @InjectMocks
  private OrderService orderService;
  private Order order;
  private OrderRequestDTO orderRequestDTO;

  @BeforeEach
  void setUp() {
    orderRequestDTO = new OrderRequestDTO();
    orderRequestDTO.setCustomerId(123L);
    orderRequestDTO.setAssetName("BTC");
    orderRequestDTO.setSize(BigDecimal.valueOf(2));
    orderRequestDTO.setPrice(BigDecimal.valueOf(50000));

    order = new Order();
    order.setId(1L);
    order.setCustomerId(123L);
    order.setAssetName("BTC");
    order.setSize(BigDecimal.valueOf(2));
    order.setPrice(BigDecimal.valueOf(50000));
    order.setStatus("PENDING");
  }

  @Test
  void testCreateOrder_Success() {
    when(createOrderFn.apply(orderRequestDTO)).thenReturn(order);

    ResponseEntity<?> response = orderService.createOrder(orderRequestDTO);

    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    assertEquals("Order received", response.getBody());
    verify(orderPublisher, times(1)).publishOrder(order);
  }

  @Test
  void testCreateOrder_Failure_InsufficientBalance() {
    when(createOrderFn.apply(orderRequestDTO)).thenReturn(null);

    ResponseEntity<?> response = orderService.createOrder(orderRequestDTO);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Insufficient balance", response.getBody());
    verify(orderPublisher, never()).publishOrder(any());
  }

  @Test
  void testDeleteOrder_Success() {
    when(deleteOrderFn.apply(1L)).thenReturn(order);

    boolean result = orderService.deleteOrder(1L);

    assertTrue(result);
    verify(orderPublisher, times(1)).publishOrder(order);
  }

  @Test
  void testDeleteOrder_Failure() {
    when(deleteOrderFn.apply(1L)).thenReturn(null);

    boolean result = orderService.deleteOrder(1L);

    assertFalse(result);
    verify(orderPublisher, never()).publishOrder(any());
  }

  @Test
  void testGetOrdersByCustomerAndDateRange() {
    LocalDateTime startDate = LocalDateTime.now().minusDays(7);
    LocalDateTime endDate = LocalDateTime.now();

    when(orderRepository.findByCustomerIdAndDateRange(123L, startDate, endDate)).thenReturn(List.of(order));

    List<Order> result = orderService.getOrdersByCustomerAndDateRange(123L, startDate, endDate);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("BTC", result.get(0).getAssetName());

    verify(orderRepository, times(1)).findByCustomerIdAndDateRange(123L, startDate, endDate);
  }

  @Test
  void testUpdateStatusOrder() {
    orderService.updateStatusOrder(order, "COMPLETED");

    assertEquals("COMPLETED", order.getStatus());
    verify(orderRepository, times(1)).save(order);
  }

  @Test
  void testFindPendingOrderForAsset() {
    when(orderRepository.findPendingOrderForAsset(123L, "BTC", "BUY", order.getPrice(), order.getSize()))
        .thenReturn(List.of(order));

    List<Order> result = orderService.findPendingOrderForAsset(order, "BUY");

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("BTC", result.get(0).getAssetName());

    verify(orderRepository, times(1)).findPendingOrderForAsset(123L, "BTC", "BUY", order.getPrice(), order.getSize());
  }
}

