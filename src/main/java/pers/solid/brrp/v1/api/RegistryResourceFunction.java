package pers.solid.brrp.v1.api;

import net.minecraft.registry.RegistryWrapper;

import java.util.function.Function;

/**
 * <p>The resources that require {@link RegistryWrapper.WrapperLookup} objects to generate content, such as loot tables. In this case, this type of resource, instead of direct loot table objects (or other type of resource), are added into the runtime resource packs. When reading the resources, such as reading loot tables, the {@link #apply(RegistryWrapper.WrapperLookup)} will be called.
 *
 * @param <T> The type of the resource, which is the return type of {@link #apply(RegistryWrapper.WrapperLookup)}.
 */
@FunctionalInterface
public interface RegistryResourceFunction<T> extends Function<RegistryWrapper.WrapperLookup, T> {
  /**
   * Called then getting server resource. Only some types of contents are supported.
   *
   * @param registryLookup the registry wrapper lookup used to read server resources.
   * @return The object that represents actual generated resource, such as loot tables or recipes.
   */
  @Override
  T apply(RegistryWrapper.WrapperLookup registryLookup);

}
