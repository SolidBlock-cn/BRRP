package net.devtech.arrp.json.loot;

import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.UniformLootTableRange;

/**
 * The value provider in the loot table.
 *
 * @deprecated Please use {@link LootTableRange}.
 */
@Deprecated
public class JRoll implements Cloneable {
  private final int min;
  private final int max;

  public JRoll(int min, int max) {
    this.min = min;
    this.max = max;
  }

  /**
   * Convert this object to the preferred loot number provider.
   */
  public LootTableRange asLootTableRange() {
    return UniformLootTableRange.between(min, max);
  }

  @Override
  public JRoll clone() {
    try {
      return (JRoll) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }
}
