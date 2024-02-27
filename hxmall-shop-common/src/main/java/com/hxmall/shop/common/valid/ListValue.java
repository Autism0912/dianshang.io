package com.hxmall.shop.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @version 1.0
 * @Description TODO
 * @Author ghl
 * @Date 2023/12/15 11:25
 * @email 815835618@qq.com
 */
@Documented
//指定校验器
@Constraint(validatedBy = {ListValueConstraintValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER,TYPE_USE})
@Retention(RUNTIME)
public @interface ListValue {

    String message() default "{com.hxmall.shop.common.valid.ListValue.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    int[] values() default { };
}
