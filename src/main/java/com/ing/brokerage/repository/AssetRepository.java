package com.ing.brokerage.repository;

import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
  List<Asset> findByCustomerId(Long customerId);

  @Query("SELECT a FROM Asset a WHERE a.customerId = :customerId AND a.assetName = :assetName")
  List<Asset> findByCustomerIdAndAssetName(
      @Param("customerId") Long customerId,
      @Param("assetName") String assetName
  );
}

