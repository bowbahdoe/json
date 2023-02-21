package dev.mccue.json.internal;

import java.lang.annotation.*;

/**
 * Annotation to mark a class that can reasonably be made value based
 * in the future and that clients should not perform any identity
 * sensitive operations on instances.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ValueCandidate {
}
