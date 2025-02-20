package com.ing.brokerage.messaging.publisher;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.ing.brokerage.entity.Order;
import com.ing.brokerage.messaging.model.OrderEvent;

@ExtendWith(MockitoExtension.class)
class OrderPublisherTest {

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private OrderPublisher orderPublisher;

  private Order order;

  @BeforeEach
  void setUp() {
    order = new Order();
    order.setId(1L);
  }

  @Test
  void testPublishOrder() {
    orderPublisher.publishOrder(order);

    verify(eventPublisher, times(1)).publishEvent(any(OrderEvent.class));
  }
}

