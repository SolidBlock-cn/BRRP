package net.devtech.arrp.json.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Please directly use {@code List<JIngredient>}.
 */
@Deprecated
public class JIngredients {
  protected final List<JIngredient> ingredients;

  public JIngredients() {
    this.ingredients = new ArrayList<>();
  }

  /**
   * @deprecated Please directly call {@link #JIngredients()}.
   */
  @Deprecated
  public static JIngredients ingredients() {
    return new JIngredients();
  }

  public JIngredients add(final JIngredient ingredient) {
    this.ingredients.add(ingredient);

    return this;
  }

  public static class Serializer implements JsonSerializer<JIngredients> {
    @Override
    public JsonElement serialize(final JIngredients src,
                                 final Type typeOfSrc,
                                 final JsonSerializationContext context) {
      return context.serialize(src.ingredients);
    }
  }
}
