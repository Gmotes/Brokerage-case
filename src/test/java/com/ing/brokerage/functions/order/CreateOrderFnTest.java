package com.ing.brokerage.functions.order;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.model.OrderRequestDTO;
import com.ing.brokerage.repository.OrderRepository;
import com.ing.brokerage.service.ControlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CreateOrderFnTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private ControlService controlService;

  @InjectMocks
  private CreateOrderFn createOrderFn;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldCreateOrderWhenControlCheckPasses() {
    // Given
    OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
    orderRequestDTO.setCustomerId(123l);
    orderRequestDTO.setAssetName("BTC");
    orderRequestDTO.setPrice(new BigDecimal("50000"));
    orderRequestDTO.setSide("BUY");
    orderRequestDTO.setSize(new BigDecimal("1"));

    when(controlService.orderCheck(orderRequestDTO)).thenReturn(true);
    Order savedOrder = new Order();
    savedOrder.setCustomerId(123l);
    savedOrder.setAssetName("BTC");
    savedOrder.setPrice(new BigDecimal("50000"));
    savedOrder.setOrderSide("BUY");
    savedOrder.setSize(new BigDecimal("1"));
    savedOrder.setStatus(BrokerageConstants.INITIAL_STATUS);
    savedOrder.setCreateDate(LocalDateTime.now());

    when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

    // When
    Order result = createOrderFn.apply(orderRequestDTO);

    // Then
    assertNotNull(result);
    verify(orderRepository).save(any(Order.class));
    assertEquals(123l, result.getCustomerId());
    assertEquals("BTC", result.getAssetName());
    assertEquals(new BigDecimal("50000"), result.getPrice());
    assertEquals(BrokerageConstants.INITIAL_STATUS, result.getStatus());
    assertNotNull(result.getCreateDate());
    assertEquals("BUY", result.getOrderSide());
    assertEquals(new BigDecimal("1"), result.getSize());
  }

  @Test
  void shouldReturnNullWhenControlCheckFails() {
    // Given
    OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
    orderRequestDTO.setCustomerId(123l);
    orderRequestDTO.setAssetName("BTC");
    orderRequestDTO.setPrice(new BigDecimal("50000"));
    orderRequestDTO.setSide("BUY");
    orderRequestDTO.setSize(new BigDecimal("1"));

    when(controlService.orderCheck(orderRequestDTO)).thenReturn(false);

    // When
    Order result = createOrderFn.apply(orderRequestDTO);

    // Then
    assertNull(result);
    verify(orderRepository, never()).save(any(Order.class));
  }
}
