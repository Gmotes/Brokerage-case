package com.ing.brokerage.functions.block;

import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.functions.model.BlockAsset;
import com.ing.brokerage.model.OrderRequestDTO;
import com.ing.brokerage.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Transactional
@Component
public class CheckAndBlockAssetBalanceFn implements Function<OrderRequestDTO, Boolean> {
  private final AssetService assetService;
  private final BlockAssetBalanceCns blockAssetBalanceCns;

  @Autowired
  public CheckAndBlockAssetBalanceFn(
      final AssetService assetService,
      final BlockAssetBalanceCns blockAssetBalanceCns) {
    this.assetService = assetService;
    this.blockAssetBalanceCns = blockAssetBalanceCns;
  }

  @Override
  public Boolean apply(final OrderRequestDTO orderRequestDTO) {
    final Asset asset = assetService.findByCustomerIdAndAssetName(orderRequestDTO.getCustomerId(), orderRequestDTO.getAssetName());
    if (asset != null) {
      if (asset.getUsableSize().compareTo(orderRequestDTO.getSize()) >= 0) {
        BlockAsset blockAsset = new BlockAsset();
        blockAsset.setAsset(asset);
        blockAsset.setWillBlock(true);
        blockAsset.setSize(orderRequestDTO.getSize());
        blockAssetBalanceCns.accept(blockAsset);
        return true;
      }
    } else {
      return false;
    }
    return true;
  }
}

