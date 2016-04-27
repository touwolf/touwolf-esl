
package com.touwolf.esl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WithHeader
{
    String name();

    String value();
}
