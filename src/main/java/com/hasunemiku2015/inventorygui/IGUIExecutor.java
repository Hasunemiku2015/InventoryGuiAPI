package com.hasunemiku2015.inventorygui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a IGUIExecutor method.
 * name: The name of the yml file.
 * slots: The slots required for the executor to listen on.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IGUIExecutor {
    String name();
    int[] slots();
}
