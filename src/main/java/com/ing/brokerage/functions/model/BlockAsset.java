package com.ing.brokerage.functions.model;

import com.ing.brokerage.entity.Asset;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BlockAsset {

  private Asset asset;
  private BigDecimal size;
  // Blocks Asset
  private boolean willBlock;
  // Unblocks Asset
  private boolean willUnBlock;

}
