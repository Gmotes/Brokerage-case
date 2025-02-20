package com.ing.brokerage.functions.order;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ing.brokerage.entity.Order;
import com.ing.brokerage.functions.model.MatchedOrders;
import com.ing.brokerage.functions.transaction.deposit.DepositAssetCns;
import com.ing.brokerage.functions.transaction.deposit.DepositTRYCns;
import com.ing.brokerage.functions.transaction.withdraw.WithdrawAssetCns;
import com.ing.brokerage.functions.transaction.withdraw.WithdrawTRYCns;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MatchedOrdersFnTest {

  @Mock
  private DepositTRYCns depositTRYCns;

  @Mock
  private DepositAssetCns depositAssetCns;

  @Mock
  private WithdrawTRYCns withdrawTRYCns;

  @Mock
  private WithdrawAssetCns withdrawAssetCns;

  @InjectMocks
  private MatchedOrdersFn matchedOrdersFn;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldProcessMatchedOrdersCorrectly() {
    // Given
    MatchedOrders matchedOrders = new MatchedOrders();
    Order buyOrder = new Order();
    Order sellOrder = new Order();
    matchedOrders.setBuyOrder(buyOrder);
    matchedOrders.setSellOrder(sellOrder);

    // When
    matchedOrdersFn.accept(matchedOrders);

    // Then
    verify(depositAssetCns).accept(buyOrder);
    verify(withdrawAssetCns).accept(sellOrder);
    verify(depositTRYCns).accept(sellOrder);
    verify(withdrawTRYCns).accept(buyOrder);
  }

  @Test
  void shouldNotThrowExceptionIfNoOrders() {
    // Given
    MatchedOrders matchedOrders = new MatchedOrders();

    // When
    matchedOrdersFn.accept(matchedOrders);

    // Then
    verify(depositAssetCns, never()).accept(any(Order.class));
    verify(withdrawAssetCns, never()).accept(any(Order.class));
    verify(depositTRYCns, never()).accept(any(Order.class));
    verify(withdrawTRYCns, never()).accept(any(Order.class));
  }
}
