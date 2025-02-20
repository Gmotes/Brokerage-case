package com.ing.brokerage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long customerId;
  private String assetName;
  private String orderSide;
  private BigDecimal size;
  private BigDecimal price;
  private String status;
  private LocalDateTime createDate;

}
