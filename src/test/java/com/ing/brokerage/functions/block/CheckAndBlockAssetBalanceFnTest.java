package com.ing.brokerage.functions.block;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.functions.model.BlockAsset;
import com.ing.brokerage.model.OrderRequestDTO;
import com.ing.brokerage.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CheckAndBlockAssetBalanceFnTest {

  @Mock
  private AssetService assetService;

  @Mock
  private BlockAssetBalanceCns blockAssetBalanceCns;

  @InjectMocks
  private CheckAndBlockAssetBalanceFn checkAndBlockAssetBalanceFn;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldBlockAssetWhenBalanceIsSufficient() {
    // Given
    OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
    orderRequestDTO.setCustomerId(123l);
    orderRequestDTO.setAssetName("BTC");
    orderRequestDTO.setSize(new BigDecimal("50"));

    Asset asset = new Asset();
    asset.setUsableSize(new BigDecimal("100"));

    when(assetService.findByCustomerIdAndAssetName(123l, "BTC")).thenReturn(asset);

    // When
    Boolean result = checkAndBlockAssetBalanceFn.apply(orderRequestDTO);

    // Then
    assertTrue(result);
    verify(blockAssetBalanceCns).accept(any(BlockAsset.class));
  }

  @Test
  void shouldReturnFalseWhenAssetNotFound() {
    // Given
    OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
    orderRequestDTO.setCustomerId(123l);
    orderRequestDTO.setAssetName("DOGE");

    when(assetService.findByCustomerIdAndAssetName(123l, "DOGE")).thenReturn(null);

    // When
    Boolean result = checkAndBlockAssetBalanceFn.apply(orderRequestDTO);

    // Then
    assertFalse(result);
    verify(blockAssetBalanceCns, never()).accept(any(BlockAsset.class));
  }
}

