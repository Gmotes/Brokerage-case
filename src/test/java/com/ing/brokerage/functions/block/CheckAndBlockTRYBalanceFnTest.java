package com.ing.brokerage.functions.block;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.functions.model.BlockTRY;
import com.ing.brokerage.model.OrderRequestDTO;
import com.ing.brokerage.service.AssetService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CheckAndBlockTRYBalanceFnTest {

  @Mock
  private AssetService assetService;

  @Mock
  private BlockTRYBalanceCns blockTRYBalanceCns;

  @InjectMocks
  private CheckAndBlockTRYBalanceFn checkAndBlockTRYBalanceFn;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldBlockTRYBalanceWhenSufficientFunds() {
    // Given
    OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
    orderRequestDTO.setCustomerId(123l);
    orderRequestDTO.setSize(new BigDecimal("50"));
    orderRequestDTO.setPrice(new BigDecimal("2"));

    Asset asset = new Asset();
    asset.setUsableSize(new BigDecimal("200"));

    when(assetService.findByCustomerIdAndAssetName(123l, BrokerageConstants.TRY)).thenReturn(asset);

    // When
    Boolean result = checkAndBlockTRYBalanceFn.apply(orderRequestDTO);

    // Then
    assertTrue(result);
    verify(blockTRYBalanceCns).accept(any(BlockTRY.class));
  }

  @Test
  void shouldNotBlockTRYBalanceWhenInsufficientFunds() {
    // Given
    OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
    orderRequestDTO.setCustomerId(123l);
    orderRequestDTO.setSize(new BigDecimal("100"));
    orderRequestDTO.setPrice(new BigDecimal("3"));

    Asset asset = new Asset();
    asset.setUsableSize(new BigDecimal("200"));

    when(assetService.findByCustomerIdAndAssetName(123l, BrokerageConstants.TRY)).thenReturn(asset);

    // When
    Boolean result = checkAndBlockTRYBalanceFn.apply(orderRequestDTO);

    // Then
    assertFalse(result);
    verify(blockTRYBalanceCns, never()).accept(any(BlockTRY.class));
  }

  @Test
  void shouldReturnFalseWhenAssetNotFound() {
    // Given
    OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
    orderRequestDTO.setCustomerId(123l);
    orderRequestDTO.setSize(new BigDecimal("50"));
    orderRequestDTO.setPrice(new BigDecimal("2"));

    when(assetService.findByCustomerIdAndAssetName(123l, BrokerageConstants.TRY)).thenReturn(null);

    // When
    Boolean result = checkAndBlockTRYBalanceFn.apply(orderRequestDTO);

    // Then
    assertFalse(result);
    verify(blockTRYBalanceCns, never()).accept(any(BlockTRY.class));
  }
}
