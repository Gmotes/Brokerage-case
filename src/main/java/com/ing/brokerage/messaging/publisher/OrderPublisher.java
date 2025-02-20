package com.ing.brokerage.messaging.publisher;

import com.ing.brokerage.entity.Order;
import com.ing.brokerage.messaging.model.OrderEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class OrderPublisher {
  private final ApplicationEventPublisher eventPublisher;

  public OrderPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }
  public void publishOrder(final Order order) {
    OrderEvent orderEvent = new OrderEvent(this, order);
    eventPublisher.publishEvent(orderEvent);
  }
}

