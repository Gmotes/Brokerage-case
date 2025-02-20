package com.ing.brokerage.functions.transaction.withdraw;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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

public class WithdrawAssetCnsTest {

  @Mock
  private AssetRepository assetRepository;

  @Mock
  private AssetService assetService;

  @InjectMocks
  private WithdrawAssetCns withdrawAssetCns;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldWithdrawAssetIfExists() {
    // Given
    Order order = new Order();
    order.setCustomerId(1L);
    order.setAssetName("BTC");
    order.setSize(new BigDecimal("0.5")); // Withdraw 0.5 BTC

    Asset existingAsset = new Asset();
    existingAsset.setCustomerId(1L);
    existingAsset.setAssetName("BTC");
    existingAsset.setSize(new BigDecimal("2.0"));

    when(assetService.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName()))
        .thenReturn(existingAsset);

    // When
    withdrawAssetCns.accept(order);

    // Then
    assertEquals(new BigDecimal("1.5"), existingAsset.getSize()); // 2.0 - 0.5 = 1.5
    verify(assetRepository).save(existingAsset);
  }

  @Test
  void shouldDoNothingIfAssetNotFound() {
    // Given
    Order order = new Order();
    order.setCustomerId(2L);
    order.setAssetName("ETH");
    order.setSize(new BigDecimal("1.0")); // Withdraw 1 ETH

    when(assetService.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName()))
        .thenReturn(null);

    // When
    assertThrows(NullPointerException.class, () -> withdrawAssetCns.accept(order));

    // Then
    verify(assetRepository, never()).save(any());
  }
}

