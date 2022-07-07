package net.devtech.arrp.json.models;

import com.google.gson.annotations.SerializedName;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.devtech.arrp.json.loot.JCondition;
import net.minecraftforge.api.distmarker.Dist;
import org.jetbrains.annotations.ApiStatus;

/**
 * <p>An overriding situation. It specifies a condition, which is the {@link #predicate}, and when the condition is met, the {@link #model} will be used, when Minecraft renders the item.</p>
 * <p>It consists of a {@linkplain #modelPredicate predicate} and a {@linkplain #model}. If your predicate consists of one entry, you may simple call {@link #JOverride(String, float, String)} without manually calling the constructor of {@code JPredicate}.</p>
 */
@PreferredEnvironment(Dist.CLIENT)
public class JOverride implements Cloneable {
  /**
   * @deprecated You should not use this field, as {@link JCondition} is used for loot tables, instead of models. Please use {@link #modelPredicate} instead.
   */
  @Deprecated
  public transient final JCondition predicate;
  @SerializedName("predicate")
  public final JPredicate modelPredicate;
  public final String model;

  @SuppressWarnings("DeprecatedIsStillUsed")
  @ApiStatus.Internal
  @Deprecated
  private JOverride(JCondition predicate, JPredicate modelPredicate, String model) {
    this.predicate = predicate;
    this.modelPredicate = modelPredicate;
    this.model = model;
  }

  /**
   * You should not use a {@link JCondition} in a model file. Therefore, you should use another constructor method: {@link #JOverride(JPredicate, String)}.
   */
  @Deprecated
  public JOverride(JCondition condition, String model) {
    this(condition, null, model);
  }

  public JOverride(JPredicate predicate, String model) {
    this(null, predicate, model);
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
