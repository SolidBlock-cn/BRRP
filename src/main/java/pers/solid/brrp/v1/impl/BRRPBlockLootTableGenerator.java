package pers.solid.brrp.v1.impl;

import net.minecraft.data.server.loottable.vanilla.VanillaBlockLootTableGenerator;

public class BRRPBlockLootTableGenerator extends VanillaBlockLootTableGenerator {
  public static final BRRPBlockLootTableGenerator INSTANCE = new BRRPBlockLootTableGenerator();

  @Override
  public void generate() {
    throw new UnsupportedOperationException();
  }
}
