package com.ing.brokerage.functions.transaction.deposit;

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

public class DepositAssetCnsTest {
  @Mock
  private AssetRepository assetRepository;
  @Mock
  private AssetService assetService;
  @InjectMocks
  private DepositAssetCns depositAssetCns;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldUpdateExistingAsset() {
    // Given
    Order order = new Order();
    order.setCustomerId(1L);
    order.setAssetName("BTC");
    order.setSize(new BigDecimal("2.0"));

    Asset existingAsset = new Asset();
    existingAsset.setCustomerId(1L);
    existingAsset.setAssetName("BTC");
    existingAsset.setSize(new BigDecimal("5.0"));
    existingAsset.setUsableSize(new BigDecimal("5.0"));

    when(assetService.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName()))
        .thenReturn(existingAsset);

    // When
    depositAssetCns.accept(order);

    // Then
    assertEquals(new BigDecimal("7.0"), existingAsset.getSize());
    assertEquals(new BigDecimal("7.0"), existingAsset.getUsableSize());
    verify(assetRepository).save(existingAsset);
  }

  @Test
  void shouldCreateNewAssetIfNotFound() {
    // Given
    Order order = new Order();
    order.setCustomerId(2L);
    order.setAssetName("ETH");
    order.setSize(new BigDecimal("3.5"));

    when(assetService.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName()))
        .thenReturn(null);

    // When
    depositAssetCns.accept(order);

    // Then
    verify(assetRepository).save(argThat(asset ->
        asset.getCustomerId().equals(order.getCustomerId()) &&
            asset.getAssetName().equals(order.getAssetName()) &&
            asset.getSize().equals(order.getSize()) &&
            asset.getUsableSize().equals(order.getSize())
    ));
  }
}
