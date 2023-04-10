package pers.solid.brrp.v1.util;

/**
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface FailableFunction<T, R, E extends Throwable> {
  R apply(T t) throws E;
}
