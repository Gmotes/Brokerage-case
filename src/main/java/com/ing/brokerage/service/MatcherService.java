package com.ing.brokerage.service;

import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.functions.model.MatchedOrders;
import com.ing.brokerage.functions.order.MatchedOrdersFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MatcherService {
  private final OrderService orderService;
  private final AssetService assetService;
  private final MatchedOrdersFn matchedOrdersFn;

  private static final Logger LOGGER = LoggerFactory.getLogger(MatcherService.class);

  @Autowired
  public MatcherService(final OrderService orderService,
                        final AssetService assetService,
                        @Lazy final MatchedOrdersFn matchedOrdersFn) {
    this.orderService    = orderService;
    this.assetService    = assetService;
    this.matchedOrdersFn = matchedOrdersFn;
  }

  //Matching should be synchronized(thread-safe) to block finding same pending order for different orders.
  public synchronized void matchOrderWithPendingOrders(final Order order,final String status) {
    Optional<Order> matchedOrder = orderService.findPendingOrderForAsset(order,status).stream().findFirst();

    matchedOrder.ifPresent(pendingOrder -> {
      LOGGER.info("Matched pending order id {} with order id {}", pendingOrder.getId(), order.getId());
      MatchedOrders matchedOrders = new MatchedOrders();
      if (order.getOrderSide().equalsIgnoreCase(BrokerageConstants.BUY)) {
        matchedOrders.setBuyOrder(order);
        matchedOrders.setSellOrder(pendingOrder);
      }
      if (order.getOrderSide().equalsIgnoreCase(BrokerageConstants.SELL)) {
        matchedOrders.setBuyOrder(pendingOrder);
        matchedOrders.setSellOrder(order);
      }
      matchedOrdersFn.accept(matchedOrders);
      orderService.updateStatusOrder(order, BrokerageConstants.MATCHED_STATUS);
      orderService.updateStatusOrder(pendingOrder, BrokerageConstants.MATCHED_STATUS);
    });
  }


  public void cancelledOrder(final Order order) {
    if (order.getOrderSide().equalsIgnoreCase(BrokerageConstants.BUY)) {
      assetService.unBlockTRYBalance(order);
    } else {
      assetService.unBlockAssetBalance(order);
    }
  }

}
