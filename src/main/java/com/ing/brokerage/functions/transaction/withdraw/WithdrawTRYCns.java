package com.ing.brokerage.functions.transaction.withdraw;

import com.ing.brokerage.constants.BrokerageConstants;
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
public class WithdrawTRYCns implements Consumer<Order> {
  private final AssetRepository assetRepository;
  private final AssetService assetService;

  @Autowired
  public WithdrawTRYCns(
      final AssetService assetService,
      final AssetRepository assetRepository) {
    this.assetRepository = assetRepository;
    this.assetService = assetService;
  }

  @Override
  public void accept(final Order order) {
    Asset foundAsset = assetService.findByCustomerIdAndAssetName(order.getCustomerId(), BrokerageConstants.TRY);
    if (foundAsset != null) {
      foundAsset.setSize(foundAsset.getSize().subtract(order.getPrice().multiply(order.getSize())));
      assetRepository.save(foundAsset);
    }
  }
}
