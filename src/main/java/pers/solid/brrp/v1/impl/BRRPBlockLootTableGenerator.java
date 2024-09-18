package pers.solid.brrp.v1.impl;

import net.minecraft.data.server.loottable.vanilla.VanillaBlockLootTableGenerator;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.WeakHashMap;

public class BRRPBlockLootTableGenerator extends VanillaBlockLootTableGenerator {
  private static final Map<RegistryWrapper.WrapperLookup, BRRPBlockLootTableGenerator> INSTANCES = new WeakHashMap<>();

  /**
   * Create a new instance.
   *
   * @see #of
   */
  public BRRPBlockLootTableGenerator(RegistryWrapper.WrapperLookup registryLookup) {
    super(registryLookup);
  }

  /**
   * Create a new instance, or get an existing instance if there is one that was created with a same {@code registryLookup} before. This is used to avoid redundant object creation and unnecessary memory usage.
   */
  @ApiStatus.AvailableSince("1.1.0")
  public static BRRPBlockLootTableGenerator of(RegistryWrapper.WrapperLookup registryLookup) {
    return INSTANCES.computeIfAbsent(registryLookup, BRRPBlockLootTableGenerator::new);
  }

  @Override
  public void generate() {
    throw new UnsupportedOperationException();
  }
}
