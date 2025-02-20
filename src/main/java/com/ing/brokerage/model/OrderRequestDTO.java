package com.ing.brokerage.model;

import com.ing.brokerage.constraints.SideCheck;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequestDTO {
    @NotNull
    private Long customerId;
    @NotNull
    @NotBlank
    private String assetName;
    @NotNull
    @NotBlank
    @SideCheck
    private String side;
    @NotNull
    private BigDecimal price;
    @NotNull
    private BigDecimal size;
}
