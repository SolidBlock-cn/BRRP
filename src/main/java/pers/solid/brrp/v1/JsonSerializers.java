package pers.solid.brrp.v1;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.mojang.datafixers.util.Either;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Vec3f;

public final class JsonSerializers {
  public static final JsonSerializer<RecipeJsonProvider> RECIPE_JSON_PROVIDER = (src, type, context) -> src.toJson();
  public static final JsonSerializer<Either<?, ?>> EITHER = (src, type, context) -> src.map(context::serialize, context::serialize);
  public static final JsonSerializer<Vec3f> VECTOR_3F = (src, type, context) -> {
    final JsonArray array = new JsonArray();
    array.add(src.getX());
    array.add(src.getY());
    array.add(src.getZ());
    return array;
  };
  public static final JsonSerializer<StringIdentifiable> STRING_IDENTIFIABLE = (src, type, context) -> new JsonPrimitive(src.asString());

  private JsonSerializers() {
  }
}
