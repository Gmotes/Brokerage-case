package com.ing.brokerage.functions.block;

import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.functions.model.BlockTRY;
import com.ing.brokerage.model.OrderRequestDTO;
import com.ing.brokerage.service.AssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Transactional
@Component
public class CheckAndBlockTRYBalanceFn implements Function<OrderRequestDTO, Boolean> {
  private final AssetService assetService;
  private final BlockTRYBalanceCns blockTRYBalanceCns;

  private static final Logger LOGGER = LoggerFactory.getLogger(CheckAndBlockTRYBalanceFn.class);

  @Autowired
  public CheckAndBlockTRYBalanceFn(
      final AssetService assetService,
      final BlockTRYBalanceCns blockTRYBalanceCns) {
    this.assetService       = assetService;
    this.blockTRYBalanceCns = blockTRYBalanceCns;
  }

  @Override
  public Boolean apply(final OrderRequestDTO orderRequestDTO) {
    final Asset asset = assetService.findByCustomerIdAndAssetName(orderRequestDTO.getCustomerId(),BrokerageConstants.TRY);
    if (asset != null) {
      if (asset.getUsableSize().compareTo(orderRequestDTO.getPrice().multiply(orderRequestDTO.getSize())) >= 0) {
        BlockTRY blockTRY = new BlockTRY();
        blockTRY.setAsset(asset);
        blockTRY.setWillBlock(true);
        blockTRY.setSize(orderRequestDTO.getSize());
        blockTRY.setPrice(orderRequestDTO.getPrice());
        blockTRYBalanceCns.accept(blockTRY);
        return Boolean.TRUE;
      } else {
        // INSUFFICIENT BALANCE
        return Boolean.FALSE;
      }
    } else {
      return Boolean.FALSE;
    }
  }
}
