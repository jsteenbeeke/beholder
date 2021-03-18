package com.jeroensteenbeeke.topiroll.beholder.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@Inherited
public @interface NoTransactionRequired {
}
