package com.ing.brokerage.functions.transaction.withdraw;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.repository.AssetRepository;
import com.ing.brokerage.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

public class WithdrawTRYCnsTest {
  @Mock
  private AssetRepository assetRepository;
  @Mock
  private AssetService assetService;

  @InjectMocks
  private WithdrawTRYCns withdrawTRYCns;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldWithdrawTRYIfBalanceExists() {
    // Given
    Order order = new Order();
    order.setCustomerId(1L);
    order.setPrice(new BigDecimal("10"));
    order.setSize(new BigDecimal("2.2")); // Total TRY withdrawal = 10.0 * 2.0 = 20.0

    Asset existingTRY = new Asset();
    existingTRY.setCustomerId(1L);
    existingTRY.setAssetName(BrokerageConstants.TRY);
    existingTRY.setSize(new BigDecimal("50"));

    when(assetService.findByCustomerIdAndAssetName(order.getCustomerId(), BrokerageConstants.TRY))
        .thenReturn(existingTRY);

    // When
    withdrawTRYCns.accept(order);

    // Then
    assertEquals(new BigDecimal("28.0"), existingTRY.getSize()); // 50 - 20 = 30
    verify(assetRepository).save(existingTRY);
  }

  @Test
  void shouldDoNothingIfTRYBalanceNotFound() {
    // Given
    Order order = new Order();
    order.setCustomerId(2L);
    order.setPrice(new BigDecimal("15.0"));
    order.setSize(new BigDecimal("3.0")); // Total TRY withdrawal = 15.0 * 3.0 = 45.0

    when(assetService.findByCustomerIdAndAssetName(order.getCustomerId(), BrokerageConstants.TRY))
        .thenReturn(null);

    // When
    withdrawTRYCns.accept(order);

    // Then
    verify(assetRepository, never()).save(any());
  }
}

