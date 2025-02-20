package com.ing.brokerage.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.functions.block.BlockAssetBalanceCns;
import com.ing.brokerage.functions.block.BlockTRYBalanceCns;
import com.ing.brokerage.functions.model.BlockAsset;
import com.ing.brokerage.functions.model.BlockTRY;
import com.ing.brokerage.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

  @Mock
  private AssetRepository assetRepository;

  @Mock
  private BlockAssetBalanceCns blockAssetBalanceCns;

  @Mock
  private BlockTRYBalanceCns blockTRYBalanceCns;

  @InjectMocks
  private AssetService assetService;

  private Asset asset;
  private Order order;

  @BeforeEach
  void setUp() {
    asset = new Asset();
    asset.setId(1L);
    asset.setCustomerId(123L);
    asset.setAssetName("BTC");
    asset.setSize(BigDecimal.valueOf(10));

    order = new Order();
    order.setCustomerId(123L);
    order.setAssetName("BTC");
    order.setSize(BigDecimal.valueOf(2));
    order.setPrice(BigDecimal.valueOf(50000));
  }

  @Test
  void testGetAssetsByCustomerId() {
    when(assetRepository.findByCustomerId(123L)).thenReturn(List.of(asset));

    List<Asset> result = assetService.getAssetsByCustomerId(123L);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("BTC", result.get(0).getAssetName());

    verify(assetRepository, times(1)).findByCustomerId(123L);
  }

  @DisplayName("Test Asset Balance exists")
  @Test
  void testFindByCustomerIdAndAssetName_WhenAssetExists() {
    when(assetRepository.findByCustomerIdAndAssetName(123L, "BTC")).thenReturn(List.of(asset));

    Asset result = assetService.findByCustomerIdAndAssetName(123L, "BTC");

    assertNotNull(result);
    assertEquals("BTC", result.getAssetName());

    verify(assetRepository, times(2)).findByCustomerIdAndAssetName(123L, "BTC");
  }


  @DisplayName("Test Asset Balance does not exist")
  @Test
  void testFindByCustomerIdAndAssetName_WhenAssetDoesNotExist() {
    when(assetRepository.findByCustomerIdAndAssetName(123L, "ETH")).thenReturn(List.of());

    Asset result = assetService.findByCustomerIdAndAssetName(123L, "ETH");

    assertNull(result);
    verify(assetRepository, times(1)).findByCustomerIdAndAssetName(123L, "ETH");
  }


  @DisplayName("Test Unblock Asset Balance")
  @Test
  void testUnBlockAssetBalance() {
    when(assetRepository.findByCustomerIdAndAssetName(123L, "BTC")).thenReturn(List.of(asset));

    assetService.unBlockAssetBalance(order);

    verify(blockAssetBalanceCns, times(1)).accept(any(BlockAsset.class));
  }

  @DisplayName("Test Unblock TRY Balance")
  @Test
  void testUnBlockTRYBalance() {
    Asset tryAsset = new Asset();
    tryAsset.setCustomerId(123L);
    tryAsset.setAssetName(BrokerageConstants.TRY);

    when(assetRepository.findByCustomerIdAndAssetName(123L, BrokerageConstants.TRY)).thenReturn(List.of(tryAsset));

    assetService.unBlockTRYBalance(order);

    verify(blockTRYBalanceCns, times(1)).accept(any(BlockTRY.class));
  }
}
