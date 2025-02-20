package com.ing.brokerage.functions.block;

import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.functions.model.BlockAsset;
import com.ing.brokerage.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

/** Blocks or unblocks Asset **/
@Transactional
@Component
public class BlockAssetBalanceCns implements Consumer<BlockAsset> {
  private final AssetRepository assetRepository;

  @Autowired
  public BlockAssetBalanceCns(
      final AssetRepository assetRepository) {
    this.assetRepository = assetRepository;
  }

  @Override
  public void accept(final BlockAsset blockAsset) {
    Asset asset = blockAsset.getAsset();
    if (blockAsset.isWillBlock()) {
      asset.setUsableSize(asset.getUsableSize().subtract(blockAsset.getSize()));
    } else if (blockAsset.isWillUnBlock()) {
      asset.setUsableSize(asset.getUsableSize().add(blockAsset.getSize()));
    } else {
      // TO DO : LOGGING
    }
    assetRepository.save(asset);
  }
}
