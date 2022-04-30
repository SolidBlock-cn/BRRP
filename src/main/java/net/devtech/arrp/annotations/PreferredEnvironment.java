package net.devtech.arrp.annotations;

import net.devtech.arrp.generator.BlockResourceGenerator;
import net.fabricmc.api.EnvType;

import java.lang.annotation.*;

/**
 * <p>It indicates that the API has something that prefers to be annotated with {@link net.fabricmc.api.Environment} to exist in the specified environment. It itself is not annotated {@code @Environment} for compatibility and same rare exception uses, but it's highly recommended to, when overriding, annotate the overriding methods with {@code @Environment}.</p>
 * <p>For example, {@link BlockResourceGenerator#getBlockModel()} is annotated with {@code @PreferredEnvironment(EnvType.CLIENT)} as it does not exist in server side. The API itself is not annotated with {@code @Environment(EnvType.CLIENT)}, which means the base methods exists in server side. However, it's highly recommended to annotate overriding methods with {@code @Environment(EnvType.CLIENT)}, unless you have some special cases that you're sure to let the server use models.</p>
 * <p>Remember that this annotation is only a reminder. It does not take real effect.</p>
 *
 * @see net.fabricmc.api.Environment
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PACKAGE})
@Documented
public @interface PreferredEnvironment {
  /**
   * Represents the environment that the annotated element is preferred to be only present in, and overriding methods are preferred to be annotated with <code>@Environment(<i>the value</i>)</code>.
   */
  EnvType value();
}
