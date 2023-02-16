package net.devtech.arrp.json.models;

import net.devtech.arrp.annotations.PreferredEnvironment;
import net.fabricmc.api.EnvType;

/**
 * <p>An overriding situation. It specifies a condition, which is the {@link #predicate}, and when the condition is met, the {@link #model} will be used, when Minecraft renders the item.</p>
 * <p>It consists of a {@linkplain #predicate predicate} and a {@linkplain #model}. If your predicate consists of one entry, you may simple call {@link #JOverride(String, float, String)} without manually calling the constructor of {@code JPredicate}.</p>
 */
@PreferredEnvironment(EnvType.CLIENT)
public class JOverride implements Cloneable {
  public final JPredicate predicate;
  public final String model;

  public JOverride(JPredicate predicate, String model) {
    this.predicate = predicate;
    this.model = model;
  }

  /**
   * This method is a faster way because you do not need to call the constructor of {@link JPredicate} in the code. You may use this method if there is one entry in the predicate.<br>
   * For example, <pre>{@code
   * new JOverride(new JPredicate().addPredicate("time", 0.4609375), "item/clock_30")
   * }</pre>
   * is identical to <pre>{@code
   * new JOverride("time", 0.4609375, "item/clock_30")}</pre>
   */
  public JOverride(String name, float value, String model) {
    this(JPredicate.of(name, value), model);
  }

  @Override
  public JOverride clone() {
    try {
      return (JOverride) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
