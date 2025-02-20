package com.ing.brokerage.service;

import com.ing.brokerage.entity.Order;
import com.ing.brokerage.functions.order.CreateOrderFn;
import com.ing.brokerage.functions.order.DeleteOrderFn;
import com.ing.brokerage.messaging.publisher.OrderPublisher;
import com.ing.brokerage.model.OrderRequestDTO;
import com.ing.brokerage.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
  private final OrderRepository orderRepository;
  private final OrderPublisher orderPublisher;
  private final CreateOrderFn createOrderFn;
  private final DeleteOrderFn deleteOrderFn;

  @Autowired
  public OrderService(
      final OrderRepository orderRepository,
      final OrderPublisher orderPublisher,
      @Lazy final CreateOrderFn createOrderFn,
      @Lazy final DeleteOrderFn deleteOrderFn) {
    this.orderRepository  = orderRepository;
    this.orderPublisher   = orderPublisher;
    this.createOrderFn = createOrderFn;
    this.deleteOrderFn = deleteOrderFn;
  }
  public ResponseEntity createOrder(final OrderRequestDTO orderRequestDTO) {
    LOGGER.info("Checking balance started for asset {}",orderRequestDTO.getAssetName());
    Order savedOrder = createOrderFn.apply(orderRequestDTO);
    if (savedOrder != null) {
      LOGGER.info("Creating order is completed. Creating event for matching");
      orderPublisher.publishOrder(savedOrder);
      return ResponseEntity
          .status(HttpStatus.ACCEPTED)
          .body("Order received");
    }
    LOGGER.info("Creating order is not completed. Insufficient balance");
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("Insufficient balance");
  }

  public boolean deleteOrder(final Long orderId){
    Order deletedOrder= deleteOrderFn.apply(orderId);
    if (deletedOrder != null) {
      LOGGER.info("Deleting order is completed for order Id {}", orderId, "Creating event for unblock assets");
      orderPublisher.publishOrder(deletedOrder);
      return true;
    } else {
      LOGGER.info("Deleting order is not completed for order Id {}", orderId, "Either it doesn't exists or already deleted");
      return false;
    }
  }

  public List<Order> getOrdersByCustomerAndDateRange(final Long customerId, final LocalDateTime startDate, final LocalDateTime endDate) {
    return orderRepository.findByCustomerIdAndDateRange(customerId, startDate, endDate);
  }

  public void updateStatusOrder(final Order order,final String status) {
    order.setStatus(status);
    orderRepository.save(order);
  }

  public List<Order> findPendingOrderForAsset(final Order order, final String orderSide) {
    List<Order> pendingOrders =
        orderRepository.findPendingOrderForAsset(order.getCustomerId(),order.getAssetName(),orderSide,order.getPrice(),order.getSize());
    return pendingOrders;
  }
}
