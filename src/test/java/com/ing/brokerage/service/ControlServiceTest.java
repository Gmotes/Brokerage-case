package com.ing.brokerage.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.functions.block.CheckAndBlockAssetBalanceFn;
import com.ing.brokerage.functions.block.CheckAndBlockTRYBalanceFn;
import com.ing.brokerage.model.OrderRequestDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ControlServiceTest {

  @Mock
  private CheckAndBlockTRYBalanceFn checkAndBlockTRYBalanceFn;

  @Mock
  private CheckAndBlockAssetBalanceFn checkAndBlockAssetBalanceFn;

  @InjectMocks
  private ControlService controlService;

  private OrderRequestDTO buyOrder;
  private OrderRequestDTO sellOrder;
  private OrderRequestDTO withdrawTRY;
  private OrderRequestDTO depositTRY;

  @BeforeEach
  void setUp() {
    buyOrder = new OrderRequestDTO();
    buyOrder.setSide(BrokerageConstants.BUY);
    buyOrder.setAssetName("BTC");

    sellOrder = new OrderRequestDTO();
    sellOrder.setSide(BrokerageConstants.SELL);
    sellOrder.setAssetName("BTC");

    withdrawTRY = new OrderRequestDTO();
    withdrawTRY.setSide(BrokerageConstants.SELL);
    withdrawTRY.setAssetName(BrokerageConstants.TRY);

    depositTRY = new OrderRequestDTO();
    depositTRY.setSide(BrokerageConstants.BUY);
    depositTRY.setAssetName(BrokerageConstants.TRY);
  }

  @Test
  void testOrderCheck_BuyAsset_BlockTRY_Success() {
    when(checkAndBlockTRYBalanceFn.apply(buyOrder)).thenReturn(true);

    boolean result = controlService.orderCheck(buyOrder);

    assertTrue(result);
    verify(checkAndBlockTRYBalanceFn, times(1)).apply(buyOrder);
    verifyNoInteractions(checkAndBlockAssetBalanceFn);
  }

  @Test
  void testOrderCheck_SellAsset_BlockAsset_Success() {
    when(checkAndBlockAssetBalanceFn.apply(sellOrder)).thenReturn(true);

    boolean result = controlService.orderCheck(sellOrder);

    assertTrue(result);
    verify(checkAndBlockAssetBalanceFn, times(1)).apply(sellOrder);
    verifyNoInteractions(checkAndBlockTRYBalanceFn);
  }

  @Test
  void testOrderCheck_WithdrawTRY_Success() {
    when(checkAndBlockTRYBalanceFn.apply(withdrawTRY)).thenReturn(true);

    boolean result = controlService.orderCheck(withdrawTRY);

    assertTrue(result);
    verify(checkAndBlockTRYBalanceFn, times(1)).apply(withdrawTRY);
    verifyNoInteractions(checkAndBlockAssetBalanceFn);
  }

  @Test
  void testOrderCheck_DepositTRY_AlwaysTrue() {
    boolean result = controlService.orderCheck(depositTRY);

    assertTrue(result);
    verifyNoInteractions(checkAndBlockTRYBalanceFn);
    verifyNoInteractions(checkAndBlockAssetBalanceFn);
  }

  @Test
  void testOrderCheck_InvalidCase_Failure() {
    OrderRequestDTO invalidOrder = new OrderRequestDTO();
    invalidOrder.setSide("INVALID");
    invalidOrder.setAssetName("UNKNOWN");

    boolean result = controlService.orderCheck(invalidOrder);

    assertFalse(result);
    verifyNoInteractions(checkAndBlockTRYBalanceFn);
    verifyNoInteractions(checkAndBlockAssetBalanceFn);
  }
}
