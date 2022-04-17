package net.devtech.arrp.json.loot;

/**
 * The value provider in the loot table.
 *
 * @deprecated Please use {@link net.minecraft.loot.provider.number.LootNumberProvider}.
 */
@SuppressWarnings("FieldCanBeLocal")
@Deprecated
public class JRoll implements Cloneable {
  private final int min;
  private final int max;

  public JRoll(int min, int max) {
    this.min = min;
    this.max = max;
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
