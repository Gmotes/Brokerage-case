package com.ing.brokerage.messaging.model;

import com.ing.brokerage.entity.Order;
import org.springframework.context.ApplicationEvent;

public class OrderEvent extends ApplicationEvent {
  private final Order order;

  public OrderEvent(final Object source,final Order order) {
    super(source);
    this.order = order;
  }

  public Order getOrder() {
    return order;
  }
}

