package net.devtech.arrp.json.models;

import net.devtech.arrp.json.loot.JCondition;
import net.minecraft.util.Identifier;

public class JOverride implements Cloneable {
  public final JCondition predicate;
  public final String model;

  /**
   * @see JModel#override(JCondition, Identifier)
   */
  public JOverride(JCondition condition, String model) {
    this.predicate = condition;
    this.model = model;
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
