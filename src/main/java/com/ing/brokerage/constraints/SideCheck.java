package com.ing.brokerage.constraints;

import com.ing.brokerage.constraints.validator.SideValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SideValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SideCheck {

  String message() default "Side can only be BUY or SELL";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
