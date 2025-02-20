package com.ing.brokerage.service;

import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.functions.block.BlockAssetBalanceCns;
import com.ing.brokerage.functions.block.BlockTRYBalanceCns;
import com.ing.brokerage.functions.model.BlockAsset;
import com.ing.brokerage.functions.model.BlockTRY;
import com.ing.brokerage.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {
  private final AssetRepository assetRepository;
  private final BlockAssetBalanceCns blockAssetBalanceCns;
  private final BlockTRYBalanceCns blockTRYBalanceCns;

  @Autowired
  public AssetService(
      final AssetRepository assetRepository,
      final BlockAssetBalanceCns blockAssetBalanceCns,
      final BlockTRYBalanceCns blockTRYBalanceCns
      ) {
    this.assetRepository      = assetRepository;
    this.blockAssetBalanceCns = blockAssetBalanceCns;
    this.blockTRYBalanceCns   = blockTRYBalanceCns;
  }

  public List<Asset> getAssetsByCustomerId(final Long customerId) {
    return assetRepository.findByCustomerId(customerId);
  }

  public Asset findByCustomerIdAndAssetName(final Long customerId,final String assetName) {
    if (assetRepository.findByCustomerIdAndAssetName(customerId,assetName).size() > 0) {
      final Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId,assetName).get(0);
      return asset;
    } else {
      return null;
    }
  }

  public void unBlockAssetBalance(final Order order) {
    final Asset asset = findByCustomerIdAndAssetName(order.getCustomerId(),order.getAssetName());
    BlockAsset blockAsset = new BlockAsset();
    blockAsset.setWillUnBlock(true);
    blockAsset.setAsset(asset);
    blockAsset.setSize(order.getSize());
    blockAssetBalanceCns.accept(blockAsset);
  }

  public void unBlockTRYBalance(final Order order) {
    final Asset asset = findByCustomerIdAndAssetName(order.getCustomerId(),BrokerageConstants.TRY);
    BlockTRY blockTRY = new BlockTRY();
    blockTRY.setWillUnBlock(true);
    blockTRY.setAsset(asset);
    blockTRY.setSize(order.getSize());
    blockTRY.setPrice(order.getPrice());
    blockTRYBalanceCns.accept(blockTRY);
  }
}
