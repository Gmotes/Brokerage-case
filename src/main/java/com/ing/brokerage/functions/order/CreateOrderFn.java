package com.ing.brokerage.functions.order;

import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.model.OrderRequestDTO;
import com.ing.brokerage.repository.OrderRepository;
import com.ing.brokerage.service.ControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.Function;

@Component
@Transactional
public class CreateOrderFn implements Function<OrderRequestDTO, Order> {

  private final OrderRepository orderRepository;
  private final ControlService controlService;

  @Autowired
  public CreateOrderFn(
      final ControlService controlService,
      final OrderRepository orderRepository) {
    this.controlService = controlService;
    this.orderRepository  = orderRepository;
  }

  @Override
  public Order apply(final OrderRequestDTO orderRequestDTO) {

    if (controlService.orderCheck(orderRequestDTO)) {
      Order order = new Order();
      order.setCustomerId(orderRequestDTO.getCustomerId());
      order.setAssetName(orderRequestDTO.getAssetName());
      order.setPrice(orderRequestDTO.getPrice());
      order.setStatus(BrokerageConstants.INITIAL_STATUS);
      order.setCreateDate(LocalDateTime.now());
      order.setOrderSide(orderRequestDTO.getSide());
      order.setSize(orderRequestDTO.getSize());
      return orderRepository.save(order);
    }
    return null;
  }
}

