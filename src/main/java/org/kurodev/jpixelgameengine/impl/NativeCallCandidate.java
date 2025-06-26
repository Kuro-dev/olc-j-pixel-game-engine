package org.kurodev.jpixelgameengine.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker interface indicating that this method is called from native C code.
 * The Method signature should not be changed and will have to be changed in the underlying native code as well.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NativeCallCandidate {
}
