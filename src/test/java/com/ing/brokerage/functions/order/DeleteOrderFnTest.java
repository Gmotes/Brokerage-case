package com.ing.brokerage.functions.order;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

public class DeleteOrderFnTest {

  @Mock
  private OrderRepository orderRepository;

  @InjectMocks
  private DeleteOrderFn deleteOrderFn;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldCancelOrderWhenStatusIsNotCancelled() {
    // Given
    Long orderId = 1L;
    Order order = new Order();
    order.setId(orderId);
    order.setStatus("OPEN");

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(orderRepository.save(order)).thenReturn(order);

    // When
    Order result = deleteOrderFn.apply(orderId);

    // Then
    assertNotNull(result);
    assertEquals(BrokerageConstants.CANCELLED_STATUS, result.getStatus());
    verify(orderRepository).save(order);
  }

  @Test
  void shouldReturnNullWhenOrderDoesNotExist() {
    // Given
    Long orderId = 1L;
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    // When
    Order result = deleteOrderFn.apply(orderId);

    // Then
    assertNull(result);
    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  void shouldReturnNullWhenOrderStatusIsCancelled() {
    // Given
    Long orderId = 1L;
    Order order = new Order();
    order.setId(orderId);
    order.setStatus(BrokerageConstants.CANCELLED_STATUS);

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    // When
    Order result = deleteOrderFn.apply(orderId);

    // Then
    assertNull(result);
    verify(orderRepository, never()).save(any(Order.class));
  }
}
