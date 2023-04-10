package pers.solid.brrp.v1.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.util.StringIdentifiable;
import pers.solid.brrp.v1.annotations.PreferredEnvironment;

import java.lang.reflect.Type;

/**
 * Similar to {@link ModelTransformation.Mode}, but not limited to client. The serialization can be seen in {@link ModelTransformation.Deserializer#deserialize(JsonElement, Type, JsonDeserializationContext)}.
 */
@PreferredEnvironment(EnvType.CLIENT)
public enum ModelTransformationMode implements StringIdentifiable {
  NONE("none"),
  THIRD_PERSON_LEFT_HAND("thirdperson_lefthand"),
  THIRD_PERSON_RIGHT_HAND("thirdperson_righthand"),
  FIRST_PERSON_LEFT_HAND("firstperson_lefthand"),
  FIRST_PERSON_RIGHT_HAND("firstperson_righthand"),
  HEAD("head"),
  GUI("gui"),
  GROUND("ground"),
  FIXED("fixed");

  private final String name;

  ModelTransformationMode(String name) {
    this.name = name;
  }

  @Override
  public String asString() {
    return name;
  }

  /**
   * Convert the string into vanilla {@link ModelTransformation.Mode} object, which is available only in client.
   *
   * @return The vanilla object.
   */
  @Environment(EnvType.CLIENT)
  public ModelTransformation.Mode asVanilla() {
    return ModelTransformation.Mode.valueOf(name());
  }
}
