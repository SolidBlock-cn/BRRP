package pers.solid.brrp.v1.impl;

import net.minecraft.data.server.loottable.vanilla.VanillaBlockLootTableGenerator;
import net.minecraft.registry.RegistryWrapper;

public class BRRPBlockLootTableGenerator extends VanillaBlockLootTableGenerator {
  public BRRPBlockLootTableGenerator(RegistryWrapper.WrapperLookup registryLookup) {
    super(registryLookup);
  }

  @Override
  public void generate() {
    throw new UnsupportedOperationException();
  }
}
