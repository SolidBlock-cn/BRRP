package pers.solid.brrp.v1.api;

import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import pers.solid.brrp.v1.BRRPMixins;
import pers.solid.brrp.v1.mixin.RegistryOpsAccessor;

import java.util.function.Function;

/**
 * <p>The resources that require {@link RegistryWrapper.WrapperLookup} objects to generate content, such as loot tables. In this case, this type of resource, instead of direct loot table objects (or other type of resource), are added into the runtime resource packs. When reading the resources, such as reading loot tables, the {@link #apply(RegistryWrapper.WrapperLookup)} will be called.
 * <p>Only some types of data pack contents support this. Besides, for dynamic registry contents, use {@link ByInfoGetter} instead.
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

  /**
   * <p>The resources that require {@link RegistryOps.RegistryInfoGetter} objects to generate content, such as dynamic registry contents. When reading dynamic registry contents, the {@link #applyFromInfoGetter(RegistryOps.RegistryInfoGetter)} will be called.
   * <p>Only dynamic registry contents support this.
   *
   * @param <T> {@inheritDoc}
   */
  interface ByInfoGetter<T> extends RegistryResourceFunction<T> {
    /**
     * In occasions where you have a direct {@link RegistryOps.RegistryInfoGetter} object, you may call {@link #applyFromInfoGetter(RegistryOps.RegistryInfoGetter)} instead.
     */
    @Deprecated
    @Override
    default T apply(RegistryWrapper.WrapperLookup registryLookup) {
      BRRPMixins.LOGGER.warn("Calling 'apply' with ByInfoGetter!");
      return applyFromInfoGetter(((RegistryOpsAccessor) registryLookup.getOps(NbtOps.INSTANCE)).getRegistryInfoGetter());
    }

    /**
     * Called then getting dynamic registry contents.
     *
     * @param registryInfoGetter The object obtained from {@link RegistryOps} used to read resources.
     * @return The object that represents the dynamic registry content.
     */
    T applyFromInfoGetter(RegistryOps.RegistryInfoGetter registryInfoGetter);
  }
}
