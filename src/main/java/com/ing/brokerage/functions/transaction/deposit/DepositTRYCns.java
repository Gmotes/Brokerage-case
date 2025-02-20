package com.ing.brokerage.functions.transaction.deposit;

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
public class DepositTRYCns implements Consumer<Order> {
  private final AssetRepository assetRepository;
  private final AssetService assetService;

  @Autowired
  public DepositTRYCns(
      final AssetService assetService,
      final AssetRepository assetRepository) {
    this.assetRepository = assetRepository;
    this.assetService = assetService;
  }

  @Override
  public void accept(final Order order) {
    Asset foundAsset = assetService.findByCustomerIdAndAssetName(order.getCustomerId(), BrokerageConstants.TRY);
    if (foundAsset != null) {
      foundAsset.setSize(foundAsset.getSize().add(order.getPrice().multiply(order.getSize())));
      foundAsset.setUsableSize(foundAsset.getUsableSize().add(order.getPrice().multiply(order.getSize())));
      assetRepository.save(foundAsset);
      return;
    }
    Asset depositAsset = new Asset();
    depositAsset.setAssetName(BrokerageConstants.TRY);
    depositAsset.setCustomerId(order.getCustomerId());
    depositAsset.setUsableSize(order.getPrice().multiply(order.getSize()));
    depositAsset.setSize(order.getPrice().multiply(order.getSize()));
    assetRepository.save(depositAsset);
  }
}

