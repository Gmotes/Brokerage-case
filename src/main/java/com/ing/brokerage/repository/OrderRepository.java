package com.ing.brokerage.repository;

import com.ing.brokerage.entity.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  // TODO : PAGINATION
  @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND o.createDate BETWEEN :startDate AND :endDate")
  List<Order> findByCustomerIdAndDateRange(
      @Param("customerId") Long customerId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate
  );
  @Query("SELECT o FROM Order o WHERE o.customerId != :customerId AND o.assetName = :assetName AND o.price = :price AND o.size = :size AND o.orderSide = :orderSide AND o.status = 'PENDING'")
  List<Order> findPendingOrderForAsset(
      @Param("customerId") Long customerId,
      @Param("assetName") String assetName,
      @Param("orderSide") String orderSide,
      @Param("price") BigDecimal price,
      @Param("size") BigDecimal size
  );
}
