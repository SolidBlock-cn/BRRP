package pers.solid.brrp.v1.model;

import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pers.solid.brrp.v1.annotations.PreferredEnvironment;

/**
 * The overriding rule of a model, usually used for item models.
 *
 * @see net.minecraft.client.render.model.json.ModelOverride
 */
@PreferredEnvironment(EnvType.CLIENT)
public class ModelOverrideBuilder implements Cloneable {
  @SerializedName("model")
  public final Identifier modelId;
  @SerializedName("predicate")
  public Object2FloatMap<Identifier> conditions;

  public ModelOverrideBuilder(Identifier modelId) {
    this.modelId = modelId;
  }

  public ModelOverrideBuilder(String namespace, String value) {
    this(Identifier.of(namespace, value));
  }

  public ModelOverrideBuilder(String modelId) {
    this(Identifier.of(modelId));
  }

  @Contract(pure = true)
  public static ModelOverrideBuilder of(Identifier modelId, Object2FloatMap<Identifier> conditions) {
    final ModelOverrideBuilder builder = new ModelOverrideBuilder(modelId);
    builder.conditions = conditions;
    return builder;
  }

  @Contract(pure = true)
  public static ModelOverrideBuilder of(Identifier modelId, Identifier type, float threshold) {
    return new ModelOverrideBuilder(modelId).addCondition(type, threshold);
  }

  @Contract(value = "_ -> this")
  public ModelOverrideBuilder setConditions(Object2FloatMap<Identifier> conditions) {
    this.conditions = conditions;
    return this;
  }

  @Contract(value = "_, _ -> this")
  public ModelOverrideBuilder addCondition(@NotNull Identifier type, float threshold) {
    if (conditions == null) {
      conditions = new Object2FloatLinkedOpenHashMap<>();
    }
    conditions.put(type, threshold);
    return this;
  }

  @Contract(value = "_, _, _ -> this")
  public ModelOverrideBuilder addCondition(String namespace, String value, float threshold) {
    return addCondition(Identifier.of(namespace, value), threshold);
  }

  @Contract(value = "_, _ -> this")
  public ModelOverrideBuilder addCondition(String identifier, float threshold) {
    return addCondition(Identifier.of(identifier), threshold);
  }

  /**
   * Convert the object to vanilla {@link ModelOverride} object. This method is client-only.
   *
   * @return The vanilla {@link ModelOverride} object.
   */
  @Environment(EnvType.CLIENT)
  @Contract(pure = true)
  public ModelOverride asModelOverride() {
    return new ModelOverride(modelId, conditions.object2FloatEntrySet().stream().map(entry -> new ModelOverride.Condition(entry.getKey(), entry.getFloatValue())).toList());
  }

  @Override
  public ModelOverrideBuilder clone() {
    try {
      ModelOverrideBuilder clone = (ModelOverrideBuilder) super.clone();
      clone.conditions = new Object2FloatOpenHashMap<>(this.conditions);
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
