package com.ing.brokerage.messaging.consumer;

import com.ing.brokerage.entity.Order;
import com.ing.brokerage.functions.transaction.deposit.DepositTRYCns;
import com.ing.brokerage.functions.transaction.withdraw.WithdrawTRYCns;
import com.ing.brokerage.messaging.model.OrderEvent;
import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.service.MatcherService;
import com.ing.brokerage.service.OrderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderConsumer {
  private final OrderService orderService;
  private final MatcherService matcherService;
  private final DepositTRYCns depositTRYCns;
  private final WithdrawTRYCns withdrawTRYCns;
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

  @Autowired
  public OrderConsumer(final OrderService orderService,
                       final MatcherService matcherService,
                       final DepositTRYCns depositTRYCns,
                       final WithdrawTRYCns withdrawTRYCns) {
    this.orderService   = orderService;
    this.matcherService = matcherService;
    this.depositTRYCns  = depositTRYCns;
    this.withdrawTRYCns = withdrawTRYCns;

  }
  @Async("eventTaskExecutor")
  @EventListener
  @Transactional
  public synchronized void handleOrderEvent(final OrderEvent orderEvent) {
    // PROCESS ORDER

    final Order order = orderEvent.getOrder();

    LOGGER.info("Order received for matching with order id {}", order.getId());

    // Deposit TRY
    if (order.getAssetName().equalsIgnoreCase(BrokerageConstants.TRY)
        && order.getOrderSide().equalsIgnoreCase(BrokerageConstants.BUY))
    {
      LOGGER.info("Deposit TRY for order id {}", order.getId());
      depositTRYCns.accept(order);
      orderService.updateStatusOrder(order, BrokerageConstants.MATCHED_STATUS);
      return;
    }

    // WITHDRAW TRY
    if (order.getAssetName().equalsIgnoreCase(BrokerageConstants.TRY)
        && order.getOrderSide().equalsIgnoreCase(BrokerageConstants.SELL))
    {
      LOGGER.info("Withdraw TRY for order id {}", order.getId());
      withdrawTRYCns.accept(order);
      orderService.updateStatusOrder(order, BrokerageConstants.MATCHED_STATUS);
      return;
    }


    // Cancel order
    if (!order.getAssetName().equalsIgnoreCase(BrokerageConstants.TRY)
        && order.getStatus().equalsIgnoreCase(BrokerageConstants.CANCELLED_STATUS))
    {
      LOGGER.info("Unblock Asset for order id {}", order.getId());
      matcherService.cancelledOrder(order);
      return;
    }

    // Check seller
    if (!order.getAssetName().equalsIgnoreCase(BrokerageConstants.TRY)
        && order.getOrderSide().equalsIgnoreCase(BrokerageConstants.BUY))
    {
        LOGGER.info("Finding seller for order id {}", order.getId());
        matcherService.matchOrderWithPendingOrders(order, BrokerageConstants.SELL);
        return;
    }

    // Check buyer
    if (!order.getAssetName().equalsIgnoreCase(BrokerageConstants.TRY)
        && order.getOrderSide().equalsIgnoreCase(BrokerageConstants.SELL))
    {
      LOGGER.info("Finding buyer for order id {}", order.getId());
      matcherService.matchOrderWithPendingOrders(order, BrokerageConstants.BUY);
    }

  }
}
