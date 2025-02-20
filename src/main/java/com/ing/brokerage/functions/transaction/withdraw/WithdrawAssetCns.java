package com.ing.brokerage.functions.transaction.withdraw;

import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.repository.AssetRepository;
import com.ing.brokerage.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Transactional
@Component
public class WithdrawAssetCns implements Consumer<Order> {
  private final AssetRepository assetRepository;
  private final AssetService assetService;

  @Autowired
  public WithdrawAssetCns(
      final AssetService assetService,
      final AssetRepository assetRepository) {
    this.assetRepository = assetRepository;
    this.assetService = assetService;
  }

  @Override
  public void accept(final Order order) {
    Asset foundAsset = assetService.findByCustomerIdAndAssetName(order.getCustomerId(),order.getAssetName());
    foundAsset.setSize(foundAsset.getSize().subtract(order.getSize()));
    assetRepository.save(foundAsset);
  }
}