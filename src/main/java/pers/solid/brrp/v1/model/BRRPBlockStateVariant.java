package pers.solid.brrp.v1.model;

import com.google.gson.JsonPrimitive;
import net.minecraft.data.client.BlockStateVariant;
import net.minecraft.data.client.VariantSetting;
import net.minecraft.data.client.VariantSettings;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static net.minecraft.data.client.VariantSettings.*;

/**
 * This is a simple extension for {@link BlockStateVariant}, which supports some additional functionalities to conveniently create your objects.
 */
public class BRRPBlockStateVariant extends BlockStateVariant {
  /**
   * Similar to {@link VariantSettings#X}, but takes an integer, instead of the enum constant.
   */
  public static final VariantSetting<Integer> INT_X = new VariantSetting<>("x", JsonPrimitive::new);
  /**
   * Similar to {@link VariantSettings#Y}, but takes an integer, instead of the enum constant.
   */
  public static final VariantSetting<Integer> INT_Y = new VariantSetting<>("y", JsonPrimitive::new);

  public static BRRPBlockStateVariant create() {
    return new BRRPBlockStateVariant();
  }

  public static BRRPBlockStateVariant create(Identifier modelId) {
    return new BRRPBlockStateVariant().model(modelId);
  }

  public static BRRPBlockStateVariant create(String namespace, String path) {
    return create(Identifier.of(namespace, path));
  }

  public static BRRPBlockStateVariant create(String modelId) {
    return create(Identifier.of(modelId));
  }

  @Override
  public <T> BRRPBlockStateVariant put(VariantSetting<T> key, T value) {
    super.put(key, value);
    return this;
  }

  public BRRPBlockStateVariant model(Identifier modelId) {
    return put(MODEL, modelId);
  }

  public BRRPBlockStateVariant uvLock(boolean uvLock) {
    return put(UVLOCK, uvLock);
  }

  public BRRPBlockStateVariant uvLock() {
    return put(UVLOCK, true);
  }

  public BRRPBlockStateVariant x(Rotation rotation) {
    return put(X, rotation);
  }

  public BRRPBlockStateVariant x(int rotation) {
    return put(INT_X, rotation);
  }

  public BRRPBlockStateVariant y(Rotation rotation) {
    return put(Y, rotation);
  }

  public BRRPBlockStateVariant y(int rotation) {
    return put(INT_Y, rotation);
  }

  /**
   * @param direction The horizontal direction. South represents 0. See {@link Direction#asRotation()}.
   */
  public BRRPBlockStateVariant y(Direction direction) {
    return put(INT_Y, (int) direction.asRotation());
  }

  public BRRPBlockStateVariant weight(int weight) {
    return put(WEIGHT, weight);
  }
}
