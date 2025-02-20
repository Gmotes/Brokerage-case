package com.ing.brokerage.service;

import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.functions.block.CheckAndBlockAssetBalanceFn;
import com.ing.brokerage.functions.block.CheckAndBlockTRYBalanceFn;
import com.ing.brokerage.model.OrderRequestDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class ControlService  {
  private final CheckAndBlockTRYBalanceFn checkAndBlockTRYBalanceFn;
  private final CheckAndBlockAssetBalanceFn checkAndBlockAssetBalanceFn;

  @Autowired
  public ControlService(final @Lazy CheckAndBlockTRYBalanceFn checkAndBlockTRYBalanceFn,
                        final @Lazy CheckAndBlockAssetBalanceFn checkAndBlockAssetBalanceFn) {
    this.checkAndBlockTRYBalanceFn  = checkAndBlockTRYBalanceFn;
    this.checkAndBlockAssetBalanceFn = checkAndBlockAssetBalanceFn;
  }

  public boolean orderCheck(final OrderRequestDTO orderRequestDTO){
    // BUY ASSET BLOCK TRY
    if (orderRequestDTO.getSide().equalsIgnoreCase(BrokerageConstants.BUY)
        && !orderRequestDTO.getAssetName().equalsIgnoreCase(BrokerageConstants.TRY)) {
      return checkAndBlockTRYBalanceFn.apply(orderRequestDTO);
    }
    // SELL ASSET BLOCK ASSET
    if (orderRequestDTO.getSide().equalsIgnoreCase(BrokerageConstants.SELL)
        && !orderRequestDTO.getAssetName().equalsIgnoreCase(BrokerageConstants.TRY))
    {
      return checkAndBlockAssetBalanceFn.apply(orderRequestDTO);
    }
    // WITHDRAW TRY BLOCK TRY
    if (orderRequestDTO.getSide().equalsIgnoreCase(BrokerageConstants.SELL)
        && orderRequestDTO.getAssetName().equalsIgnoreCase(BrokerageConstants.TRY))
    {
      return checkAndBlockTRYBalanceFn.apply(orderRequestDTO);
    }

    // DEPOSIT TRY
    if (orderRequestDTO.getSide().equalsIgnoreCase(BrokerageConstants.BUY)
        && orderRequestDTO.getAssetName().equalsIgnoreCase(BrokerageConstants.TRY))
    {
      return true;
    }

    return false;
  }

}
