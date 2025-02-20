package com.ing.brokerage.functions.block;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.functions.model.BlockTRY;
import com.ing.brokerage.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BlockTRYBalanceCnsTest {

  @Mock
  private AssetRepository assetRepository;

  @InjectMocks
  private BlockTRYBalanceCns blockTRYBalanceCns;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldBlockTRYBalance() {
    // Given
    Asset asset = new Asset();
    asset.setUsableSize(new BigDecimal("500"));

    BlockTRY blockTRY = mock(BlockTRY.class);
    when(blockTRY.getAsset()).thenReturn(asset);
    when(blockTRY.isWillBlock()).thenReturn(true);
    when(blockTRY.getSize()).thenReturn(new BigDecimal("10"));
    when(blockTRY.getPrice()).thenReturn(new BigDecimal("2"));

    // When
    blockTRYBalanceCns.accept(blockTRY);

    // Then
    assertEquals(new BigDecimal("480"), asset.getUsableSize());
    verify(assetRepository).save(asset);
  }

  @Test
  void shouldUnblockTRYBalance() {
    // Given
    Asset asset = new Asset();
    asset.setUsableSize(new BigDecimal("500"));

    BlockTRY blockTRY = mock(BlockTRY.class);
    when(blockTRY.getAsset()).thenReturn(asset);
    when(blockTRY.isWillUnBlock()).thenReturn(true);
    when(blockTRY.getSize()).thenReturn(new BigDecimal("20"));
    when(blockTRY.getPrice()).thenReturn(new BigDecimal("1"));

    // When
    blockTRYBalanceCns.accept(blockTRY);

    // Then
    assertEquals(new BigDecimal("520"), asset.getUsableSize());
    verify(assetRepository).save(asset);
  }

  @Test
  void shouldNotModifyTRYBalanceIfUnknownState() {
    // Given
    Asset asset = new Asset();
    asset.setUsableSize(new BigDecimal("500"));

    BlockTRY blockTRY = mock(BlockTRY.class);
    when(blockTRY.getAsset()).thenReturn(asset);
    when(blockTRY.isWillBlock()).thenReturn(false);
    when(blockTRY.isWillUnBlock()).thenReturn(false);

    // When
    blockTRYBalanceCns.accept(blockTRY);

    // Then
    assertEquals(new BigDecimal("500"), asset.getUsableSize()); // No change
    verify(assetRepository).save(asset);
  }

}
