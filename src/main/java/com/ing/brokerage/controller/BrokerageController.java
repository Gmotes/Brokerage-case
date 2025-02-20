package com.ing.brokerage.controller;

import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.model.DeleteRequestDTO;
import com.ing.brokerage.model.ListAssetRequestDTO;
import com.ing.brokerage.model.OrderListDTO;
import com.ing.brokerage.model.OrderRequestDTO;

import com.ing.brokerage.service.AssetService;
import com.ing.brokerage.service.OrderService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@OpenAPIDefinition(info = @Info(title = "Brokerage API", version = "v1"))

@RestController
@RequestMapping("/v1")
public final class BrokerageController {
  private static final Logger LOGGER = LoggerFactory.getLogger(BrokerageController.class);
  private OrderService orderService;
  private AssetService assetService;

  @Autowired
  public BrokerageController(
        final OrderService orderService,
        final AssetService assetService) {
    this.orderService = orderService;
    this.assetService = assetService;
  }

  @Operation(
      summary = "Creates an order buy or sell"
  )
  @PreAuthorize("hasRole('USER')")
  @PostMapping("orders:create")
  ResponseEntity<String> createOrder(@Valid @RequestBody final OrderRequestDTO orderRequestDTO) {
    LOGGER.info("Order request received for customer id {}",orderRequestDTO.getCustomerId());
    return orderService.createOrder(orderRequestDTO);
  }

  @Operation(
      summary = "List orders for customer and date period"
  )
  @Secured({"ADMIN","USER"})
  @PostMapping("orders:list")
  List<Order> listOrders(@Valid @RequestBody final OrderListDTO orderListDTO) {
    LOGGER.info("Order list request received for customer id {}",orderListDTO.getCustomerId());
    return orderService.getOrdersByCustomerAndDateRange(orderListDTO.getCustomerId(),orderListDTO.getStartDate(),orderListDTO.getEndDate());
  }

  @Operation(
      summary = "Delete customer's order"
  )
  @Secured({"ADMIN","USER"})
  @DeleteMapping("orders:delete")
  ResponseEntity<String> deleteOrder(@Valid @RequestBody final DeleteRequestDTO deleteRequestDTO) {
    LOGGER.info("Order delete request received for order id {}",deleteRequestDTO.getOrderId());
    boolean result = orderService.deleteOrder(deleteRequestDTO.getOrderId());
    if (result) {
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  @Operation(
      summary = "List assets for customer"
  )
  @Secured({"ADMIN","USER"})
  @PostMapping("assets:list")
  List<Asset> listAssets(@Valid @RequestBody final ListAssetRequestDTO listAssetRequestDTO) {
    LOGGER.info("Asset list request received for customer id {}",listAssetRequestDTO.getCustomerId());
    return assetService.getAssetsByCustomerId(listAssetRequestDTO.getCustomerId());
  }
}

