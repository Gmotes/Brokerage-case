package com.ing.brokerage.functions.model;

import com.ing.brokerage.entity.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchedOrders {

  private Order buyOrder;
  private Order sellOrder;

}
