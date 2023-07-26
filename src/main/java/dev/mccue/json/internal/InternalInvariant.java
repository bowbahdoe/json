package dev.mccue.json.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An invariant that should be maintained by code internal to this module,
 * but isn't enforced by internal constructors for efficiency.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.RECORD_COMPONENT, ElementType.CONSTRUCTOR, ElementType.METHOD})
@interface InternalInvariant {
    String[] value();
}
