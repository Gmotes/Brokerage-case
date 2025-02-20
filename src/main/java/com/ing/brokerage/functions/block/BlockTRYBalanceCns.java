package com.ing.brokerage.functions.block;

import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.functions.model.BlockTRY;
import com.ing.brokerage.repository.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

/** Blocks or unblocks TRY **/
@Transactional
@Component
public class BlockTRYBalanceCns implements Consumer<BlockTRY> {
  private final AssetRepository assetRepository;

  private static final Logger LOGGER = LoggerFactory.getLogger(BlockTRYBalanceCns.class);

  @Autowired
  public BlockTRYBalanceCns(
      final AssetRepository assetRepository) {
    this.assetRepository = assetRepository;
  }

  @Override
  public void accept(final BlockTRY blockTRY) {
    Asset asset = blockTRY.getAsset();

    if (blockTRY.isWillBlock()) {
      asset.setUsableSize(asset.getUsableSize().subtract(blockTRY.getSize().multiply(blockTRY.getPrice())));
    } else if (blockTRY.isWillUnBlock()) {
      asset.setUsableSize(asset.getUsableSize().add(blockTRY.getSize().multiply(blockTRY.getPrice())));
    }
    assetRepository.save(asset);
  }
}
