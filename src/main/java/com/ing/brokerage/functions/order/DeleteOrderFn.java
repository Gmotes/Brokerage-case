package com.ing.brokerage.functions.order;

import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

@Component
@Transactional
public class DeleteOrderFn implements Function<Long, Order> {

  private final OrderRepository orderRepository;

  @Autowired
  public DeleteOrderFn(
      final OrderRepository orderRepository) {
    this.orderRepository  = orderRepository;
  }

  @Override
  public Order apply(final Long orderId) {
    Optional<Order> order = orderRepository.findById(orderId);
    if (!order.isPresent() )
      return null;
    Order foundOrder = order.get();
    if (foundOrder.getStatus().equalsIgnoreCase(BrokerageConstants.CANCELLED_STATUS))
      return null;
    foundOrder.setStatus(BrokerageConstants.CANCELLED_STATUS);
    orderRepository.save(foundOrder);

    return foundOrder;
  }
}


