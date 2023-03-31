package pers.solid.brrp.v1;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.util.StringIdentifiable;
import org.joml.Vector3f;

public final class JsonSerializers {
  private JsonSerializers() {
  }

  public static final JsonSerializer<RecipeJsonProvider> RECIPE_JSON_PROVIDER = (src, type, context) -> src.toJson();
  public static final JsonSerializer<Either<?, ?>> EITHER = (src, type, context) -> src.map(context::serialize, context::serialize);
  public static final JsonSerializer<Vector3f> VECTOR_3F = (src, type, context) -> {
    final JsonArray array = new JsonArray();
    array.add(src.x);
    array.add(src.y);
    array.add(src.z);
    return array;
  };
  public static final JsonSerializer<StringIdentifiable> STRING_IDENTIFIABLE = (src, type, context) -> new JsonPrimitive(src.asString());
  public static final JsonSerializer<JsonUnbakedModel.GuiLight> GUI_LIGHT =(src, typeOfSrc, context) -> new JsonPrimitive(src.isSide() ? "side":"front");
}
