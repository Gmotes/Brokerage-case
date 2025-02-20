package com.ing.brokerage.controller;


import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.entity.Asset;
import com.ing.brokerage.entity.Order;
import com.ing.brokerage.model.DeleteRequestDTO;
import com.ing.brokerage.model.ListAssetRequestDTO;
import com.ing.brokerage.model.OrderListDTO;
import com.ing.brokerage.model.OrderRequestDTO;
import com.ing.brokerage.service.AssetService;
import com.ing.brokerage.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class BrokerageControllerTest {
  @InjectMocks
  private BrokerageController brokerageController;
  @Mock
  private OrderService orderService;
  @Mock
  private AssetService assetService;

  @DisplayName("Test create order")
  @Test
  public void createOrder()
  {
    OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
    orderRequestDTO.setCustomerId(Long.valueOf("1"));
    orderRequestDTO.setAssetName(BrokerageConstants.TRY);
    orderRequestDTO.setSide(BrokerageConstants.BUY);
    orderRequestDTO.setPrice(BigDecimal.ONE);

    ResponseEntity<String> response = ResponseEntity
        .status(HttpStatus.ACCEPTED)
        .body("Order received");

    given(orderService.createOrder(orderRequestDTO)).willReturn(response);
    ResponseEntity<String> receivedResponse = brokerageController.createOrder(orderRequestDTO);
    assertThat(response).isEqualTo(receivedResponse);
  }

  @DisplayName("Test delete order")
  @Test
  public void deleteOrder() throws Exception
  {
    DeleteRequestDTO deleteRequestDTO = new DeleteRequestDTO();
    deleteRequestDTO.setOrderId(Long.valueOf("1"));

    given(orderService.deleteOrder(Long.valueOf("1"))).willReturn(true);
    ResponseEntity<String> receivedResponse = brokerageController.deleteOrder(deleteRequestDTO);
    assertThat(new ResponseEntity<>(HttpStatus.OK)).isEqualTo(receivedResponse);
  }


  @DisplayName("Test list order")
  @Test
  public void listOrder()
  {
    final LocalDateTime startDate =LocalDateTime.now().minusDays(1l);
    final LocalDateTime endDate =LocalDateTime.now().minusDays(1l);
    OrderListDTO orderListDTO = new OrderListDTO();
    orderListDTO.setCustomerId(Long.valueOf("1"));
    orderListDTO.setStartDate(startDate);
    orderListDTO.setEndDate(endDate);

    List<Order> orderList = new ArrayList<>();

    given(orderService.getOrdersByCustomerAndDateRange(Long.valueOf("1"),startDate,endDate))
        .willReturn(orderList);
    List <Order> returnedOrderList = brokerageController.listOrders(orderListDTO);
    assertEquals(orderList.size(),returnedOrderList.size());
  }

  @DisplayName("Test list asset")
  @Test
  public void listAsset()
  {
    final LocalDateTime startDate =LocalDateTime.now().minusDays(1l);
    final LocalDateTime endDate =LocalDateTime.now().minusDays(1l);
    ListAssetRequestDTO listAssetRequestDTO = new ListAssetRequestDTO();
    listAssetRequestDTO.setCustomerId(Long.valueOf("1"));
    List<Asset> assetList = new ArrayList<>();

    given(assetService.getAssetsByCustomerId(Long.valueOf("1")))
        .willReturn(assetList);
    List <Asset> returnedAssetList = brokerageController.listAssets(listAssetRequestDTO);
    assertEquals(assetList.size(),returnedAssetList.size());
  }

}

