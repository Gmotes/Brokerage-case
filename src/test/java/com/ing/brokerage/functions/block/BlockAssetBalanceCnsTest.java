package com.ing.brokerage.functions.block;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.functions.model.BlockAsset;
import com.ing.brokerage.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BlockAssetBalanceCnsTest {

  @Mock
  private AssetRepository assetRepository;

  @InjectMocks
  private BlockAssetBalanceCns blockAssetBalanceCns;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldBlockAsset() {
    // Given
    Asset asset = new Asset();
    asset.setUsableSize(new BigDecimal("100"));

    BlockAsset blockAsset = mock(BlockAsset.class);
    when(blockAsset.getAsset()).thenReturn(asset);
    when(blockAsset.isWillBlock()).thenReturn(true);
    when(blockAsset.getSize()).thenReturn(new BigDecimal("20"));

    // When
    blockAssetBalanceCns.accept(blockAsset);

    // Then
    assertEquals(new BigDecimal("80"), asset.getUsableSize());
    verify(assetRepository).save(asset);
  }

  @Test
  void shouldUnblockAsset() {
    // Given
    Asset asset = new Asset();
    asset.setUsableSize(new BigDecimal("100"));

    BlockAsset blockAsset = mock(BlockAsset.class);
    when(blockAsset.getAsset()).thenReturn(asset);
    when(blockAsset.isWillUnBlock()).thenReturn(true);
    when(blockAsset.getSize()).thenReturn(new BigDecimal("30"));

    // When
    blockAssetBalanceCns.accept(blockAsset);

    // Then
    assertEquals(new BigDecimal("130"), asset.getUsableSize());
    verify(assetRepository).save(asset);
  }

  @Test
  void shouldNotModifyAssetIfUnknownState() {
    // Given
    Asset asset = new Asset();
    asset.setUsableSize(new BigDecimal("100"));

    BlockAsset blockAsset = mock(BlockAsset.class);
    when(blockAsset.getAsset()).thenReturn(asset);
    when(blockAsset.isWillBlock()).thenReturn(false);
    when(blockAsset.isWillUnBlock()).thenReturn(false);

    // When
    blockAssetBalanceCns.accept(blockAsset);

    // Then
    assertEquals(new BigDecimal("100"), asset.getUsableSize()); // No change
    verify(assetRepository).save(asset);
  }

}

