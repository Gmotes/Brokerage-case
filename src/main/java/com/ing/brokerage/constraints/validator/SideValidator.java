package com.ing.brokerage.constraints.validator;

import com.ing.brokerage.constants.BrokerageConstants;
import com.ing.brokerage.constraints.SideCheck;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SideValidator implements ConstraintValidator<SideCheck, String> {

  @Override
  public boolean isValid(String side, ConstraintValidatorContext context) {
    return side != null && (side.equalsIgnoreCase(BrokerageConstants.BUY) || side.equalsIgnoreCase(BrokerageConstants.SELL)) ;
  }
}
