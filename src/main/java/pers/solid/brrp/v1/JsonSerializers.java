package pers.solid.brrp.v1;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import org.joml.Vector3f;

public final class JsonSerializers {
  public static final JsonSerializer<Recipe<?>> RECIPE_JSON_PROVIDER = (src, type, context) -> Util.getResult(Recipe.CODEC.encodeStart(JsonOps.INSTANCE, src), IllegalStateException::new);
  public static final JsonSerializer<Either<?, ?>> EITHER = (src, type, context) -> src.map(context::serialize, context::serialize);
  public static final JsonSerializer<Vector3f> VECTOR_3F = (src, type, context) -> {
    final JsonArray array = new JsonArray();
    array.add(src.x);
    array.add(src.y);
    array.add(src.z);
    return array;
  };
  public static final JsonSerializer<StringIdentifiable> STRING_IDENTIFIABLE = (src, type, context) -> new JsonPrimitive(src.asString());

  private JsonSerializers() {
  }
}
