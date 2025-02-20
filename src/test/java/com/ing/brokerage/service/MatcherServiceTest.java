package com.ing.brokerage.service;

import static org.mockito.Mockito.*;

import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.functions.model.MatchedOrders;
import com.ing.brokerage.functions.order.MatchedOrdersFn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class MatcherServiceTest {
  @Mock
  private OrderService orderService;
  @Mock
  private AssetService assetService;
  @Mock
  private MatchedOrdersFn matchedOrdersFn;
  @InjectMocks
  private MatcherService matcherService;

  private Order buyOrder;
  private Order sellOrder;

  @BeforeEach
  void setUp() {
    buyOrder = new Order();
    buyOrder.setId(1L);
    buyOrder.setOrderSide(BrokerageConstants.BUY);
    buyOrder.setAssetName("BTC");
    buyOrder.setCustomerId(1001L);

    sellOrder = new Order();
    sellOrder.setId(2L);
    sellOrder.setOrderSide(BrokerageConstants.SELL);
    sellOrder.setAssetName("BTC");
    sellOrder.setCustomerId(1002L);
  }

  @Test
  void testMatchOrderWithPendingOrders_Success() {
    when(orderService.findPendingOrderForAsset(any(Order.class), anyString()))
        .thenReturn(List.of(sellOrder));

    matcherService.matchOrderWithPendingOrders(buyOrder, BrokerageConstants.INITIAL_STATUS);

    verify(orderService, times(1)).findPendingOrderForAsset(buyOrder, BrokerageConstants.INITIAL_STATUS);
    verify(matchedOrdersFn, times(1)).accept(any(MatchedOrders.class));
    verify(orderService, times(1)).updateStatusOrder(buyOrder, BrokerageConstants.MATCHED_STATUS);
    verify(orderService, times(1)).updateStatusOrder(sellOrder, BrokerageConstants.MATCHED_STATUS);
  }

  @Test
  void testMatchOrderWithPendingOrders_NoMatchFound() {
    when(orderService.findPendingOrderForAsset(any(Order.class), anyString()))
        .thenReturn(List.of());  // No matching orders

    matcherService.matchOrderWithPendingOrders(buyOrder, BrokerageConstants.INITIAL_STATUS);

    verify(orderService, times(1)).findPendingOrderForAsset(buyOrder, BrokerageConstants.INITIAL_STATUS);
    verifyNoInteractions(matchedOrdersFn);
    verify(orderService, never()).updateStatusOrder(any(), anyString());
  }

  @Test
  void testCancelledOrder_BuyOrder_UnblocksTRYBalance() {
    matcherService.cancelledOrder(buyOrder);

    verify(assetService, times(1)).unBlockTRYBalance(buyOrder);
    verify(assetService, never()).unBlockAssetBalance(any());
  }

  @Test
  void testCancelledOrder_SellOrder_UnblocksAssetBalance() {
    matcherService.cancelledOrder(sellOrder);

    verify(assetService, times(1)).unBlockAssetBalance(sellOrder);
    verify(assetService, never()).unBlockTRYBalance(any());
  }
}

