package org.kurodev.jpixelgameengine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker interface indicating that this method is called from native C code.
 * The name should not be changed and will have to be change in the underlying native code as well.
 * NEITHER CLASS NAME NOR FIELDS SHOULD BE CHANGED
 * ALSO NOT METHOD NAMES
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NativeCallCandidate {
}
