package net.devtech.arrp.json.recipe;


import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;

public class JResult implements Cloneable, JsonSerializable {
  /**
   * The identifier (as string) of the resulting item.
   */
  public final String item;
  /**
   * The count of the result.
   */
  public Integer count;

  public JResult(final String id) {
    this.item = id;
  }

  public JResult(final Identifier id) {
    this(id.toString());
  }

  /**
   * This method will query the id of the item. You should ensure that the item has been registered.
   */
  public JResult(final ItemConvertible item) {
    this(Registries.ITEM.getId(item.asItem()));
  }

  /**
   * Set the count of this result.
   */
  @Contract("_ -> this")
  @CanIgnoreReturnValue
  public JResult count(int count) {
    this.count = count;
    return this;
  }

  @Override
  public JResult clone() {
    try {
      return (JResult) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    final JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("item", item);
    if (count != null) jsonObject.addProperty("count", count);
    return jsonObject;
  }
}
