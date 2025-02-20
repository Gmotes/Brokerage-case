package com.ing.brokerage.functions.order;


import com.ing.brokerage.functions.model.MatchedOrders;
import com.ing.brokerage.functions.transaction.deposit.DepositAssetCns;
import com.ing.brokerage.functions.transaction.deposit.DepositTRYCns;
import com.ing.brokerage.functions.transaction.withdraw.WithdrawAssetCns;
import com.ing.brokerage.functions.transaction.withdraw.WithdrawTRYCns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Component
@Transactional
public class MatchedOrdersFn implements Consumer<MatchedOrders> {
  private final DepositTRYCns depositTRYCns;
  private final DepositAssetCns depositAssetCns;
  private final WithdrawAssetCns withdrawAssetCns;
  private final WithdrawTRYCns withdrawTRYCns;

  @Autowired
  public MatchedOrdersFn(
      final DepositTRYCns depositTRYCns,
      final DepositAssetCns depositAssetCns,
      final WithdrawTRYCns withdrawTRYCns,
      final WithdrawAssetCns withdrawAssetCns) {
    this.depositTRYCns = depositTRYCns;
    this.depositAssetCns = depositAssetCns;
    this.withdrawTRYCns = withdrawTRYCns;
    this.withdrawAssetCns = withdrawAssetCns;
  }

  @Override
  public void accept(final MatchedOrders matchedOrders) {

    // ASSET Transactions
    depositAssetCns.accept(matchedOrders.getBuyOrder());
    withdrawAssetCns.accept(matchedOrders.getSellOrder());

    // TRY Transactions
    depositTRYCns.accept(matchedOrders.getSellOrder());
    withdrawTRYCns.accept(matchedOrders.getBuyOrder());

  }

}


