package net.devtech.arrp.json.recipe;


import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;

public class JPattern implements Cloneable, JsonSerializable {
  protected final String[] rows;

  public JPattern(String... rows) {
    this.rows = rows;
  }

  /**
   * Set the row of the pattern.
   */
  protected JPattern row(int index, String keys) {
    this.rows[index] = keys;

    return this;
  }

  @CanIgnoreReturnValue
  @Contract("_ -> this")
  public JPattern row1(String keys) {
    return this.row(0, keys);
  }

  @CanIgnoreReturnValue
  @Contract("_ -> this")
  public JPattern row2(String keys) {
    return this.row(1, keys);
  }

  @CanIgnoreReturnValue
  @Contract("_ -> this")
  public JPattern row3(String keys) {
    return this.row(2, keys);
  }

  @Override
  public JPattern clone() {
    try {
      return (JPattern) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(rows);
  }

}
