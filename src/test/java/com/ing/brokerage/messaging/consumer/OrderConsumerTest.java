package com.ing.brokerage.messaging.consumer;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ing.brokerage.entity.Order;
import com.ing.brokerage.functions.transaction.deposit.DepositTRYCns;
import com.ing.brokerage.functions.transaction.withdraw.WithdrawTRYCns;
import com.ing.brokerage.messaging.model.OrderEvent;
import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.service.MatcherService;
import com.ing.brokerage.service.OrderService;

@ExtendWith(MockitoExtension.class)
class OrderConsumerTest {

  @Mock
  private OrderService orderService;

  @Mock
  private MatcherService matcherService;

  @Mock
  private DepositTRYCns depositTRYCns;

  @Mock
  private WithdrawTRYCns withdrawTRYCns;

  @InjectMocks
  private OrderConsumer orderConsumer;

  private Order buyTryOrder;
  private Order sellTryOrder;
  private Order buyAssetOrder;
  private Order sellAssetOrder;
  private Order cancelledOrder;

  @BeforeEach
  void setUp() {
    buyTryOrder = new Order();
    buyTryOrder.setId(1L);
    buyTryOrder.setAssetName(BrokerageConstants.TRY);
    buyTryOrder.setOrderSide(BrokerageConstants.BUY);
    buyTryOrder.setStatus(BrokerageConstants.INITIAL_STATUS);

    sellTryOrder = new Order();
    sellTryOrder.setId(2L);
    sellTryOrder.setAssetName(BrokerageConstants.TRY);
    sellTryOrder.setOrderSide(BrokerageConstants.SELL);
    sellTryOrder.setStatus(BrokerageConstants.INITIAL_STATUS);

    buyAssetOrder = new Order();
    buyAssetOrder.setId(3L);
    buyAssetOrder.setAssetName("BTC");
    buyAssetOrder.setOrderSide(BrokerageConstants.BUY);
    buyAssetOrder.setStatus(BrokerageConstants.INITIAL_STATUS);

    sellAssetOrder = new Order();
    sellAssetOrder.setId(4L);
    sellAssetOrder.setAssetName("BTC");
    sellAssetOrder.setOrderSide(BrokerageConstants.SELL);
    sellAssetOrder.setStatus(BrokerageConstants.INITIAL_STATUS);

    cancelledOrder = new Order();
    cancelledOrder.setId(5L);
    cancelledOrder.setAssetName("BTC");
    cancelledOrder.setStatus(BrokerageConstants.CANCELLED_STATUS);
  }

  @Test
  void testHandleOrderEvent_DepositTRY() {
    OrderEvent event = new OrderEvent(this, buyTryOrder);

    orderConsumer.handleOrderEvent(event);

    verify(depositTRYCns, times(1)).accept(buyTryOrder);
    verify(orderService, times(1)).updateStatusOrder(buyTryOrder, BrokerageConstants.MATCHED_STATUS);
    verifyNoInteractions(matcherService);
  }

  @Test
  void testHandleOrderEvent_WithdrawTRY() {
    OrderEvent event = new OrderEvent(this, sellTryOrder);

    orderConsumer.handleOrderEvent(event);

    verify(withdrawTRYCns, times(1)).accept(sellTryOrder);
    verify(orderService, times(1)).updateStatusOrder(sellTryOrder, BrokerageConstants.MATCHED_STATUS);
    verifyNoInteractions(matcherService);
  }

  @Test
  void testHandleOrderEvent_CancelledOrder() {
    OrderEvent event = new OrderEvent(this, cancelledOrder);

    orderConsumer.handleOrderEvent(event);

    verify(matcherService, times(1)).cancelledOrder(cancelledOrder);
    verifyNoInteractions(depositTRYCns);
    verifyNoInteractions(withdrawTRYCns);
  }

  @Test
  void testHandleOrderEvent_FindSeller() {
    OrderEvent event = new OrderEvent(this, buyAssetOrder);

    orderConsumer.handleOrderEvent(event);

    verify(matcherService, times(1)).matchOrderWithPendingOrders(buyAssetOrder, BrokerageConstants.SELL);
  }

  @Test
  void testHandleOrderEvent_FindBuyer() {
    OrderEvent event = new OrderEvent(this, sellAssetOrder);

    orderConsumer.handleOrderEvent(event);

    verify(matcherService, times(1)).matchOrderWithPendingOrders(sellAssetOrder, BrokerageConstants.BUY);
  }
}
