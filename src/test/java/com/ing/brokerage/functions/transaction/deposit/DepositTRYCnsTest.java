package com.ing.brokerage.functions.transaction.deposit;

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

public class DepositTRYCnsTest {

  @Mock
  private AssetRepository assetRepository;

  @Mock
  private AssetService assetService;

  @InjectMocks
  private DepositTRYCns depositTRYCns;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldUpdateExistingTRYBalance() {
    // Given
    Order order = new Order();
    order.setCustomerId(1L);
    order.setPrice(new BigDecimal("10.0"));
    order.setSize(new BigDecimal("2.0")); // Total TRY deposit = 10.0 * 2.0 = 20.0

    Asset existingTRY = new Asset();
    existingTRY.setCustomerId(1L);
    existingTRY.setAssetName(BrokerageConstants.TRY);
    existingTRY.setSize(new BigDecimal("50.0"));
    existingTRY.setUsableSize(new BigDecimal("50.0"));

    when(assetService.findByCustomerIdAndAssetName(order.getCustomerId(), BrokerageConstants.TRY))
        .thenReturn(existingTRY);

    // When
    depositTRYCns.accept(order);

    // Then
    assertEquals(new BigDecimal("70.00"), existingTRY.getSize());
    assertEquals(new BigDecimal("70.00"), existingTRY.getUsableSize());
    verify(assetRepository).save(existingTRY);
  }

  @Test
  void shouldCreateNewTRYAssetIfNotFound() {
    // Given
    Order order = new Order();
    order.setCustomerId(2L);
    order.setPrice(new BigDecimal("15.0"));
    order.setSize(new BigDecimal("3.0")); // Total TRY deposit = 15.0 * 3.0 = 45.0

    when(assetService.findByCustomerIdAndAssetName(order.getCustomerId(), BrokerageConstants.TRY))
        .thenReturn(null);

    // When
    depositTRYCns.accept(order);

    // Then
    verify(assetRepository).save(argThat(asset ->
        asset.getCustomerId().equals(order.getCustomerId()) &&
            asset.getAssetName().equals(BrokerageConstants.TRY) &&
            asset.getSize().equals(new BigDecimal("45.00")) &&
            asset.getUsableSize().equals(new BigDecimal("45.00"))
    ));
  }
}
