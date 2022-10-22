package net.devtech.arrp.json.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.devtech.arrp.api.JsonSerializable;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;

public class JPattern implements Cloneable, JsonSerializable {
  protected final String[] rows;

  public JPattern(String... rows) {
    this.rows = rows;
  }

  /**
   * Creates an empty pattern.
   *
   * @deprecated Ambiguous name.
   */
  @Deprecated
  public static JPattern pattern() {
    return new JPattern("   ", "   ", "   ");
  }

  /**
   * @deprecated Please directly cal {@link #JPattern(String...)}.
   */
  @Deprecated
  public static JPattern pattern(String... rows) {
    return new JPattern(rows);
  }

  /**
   * Set the row of the pattern.
   */
  protected JPattern row(int index, String keys) {
    this.rows[index] = keys;

    return this;
  }

  @Contract("_ -> this")
  public JPattern row1(String keys) {
    return this.row(0, keys);
  }

  @Contract("_ -> this")
  public JPattern row2(String keys) {
    return this.row(1, keys);
  }

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

  @Deprecated
  public static class Serializer implements JsonSerializer<JPattern> {
    @Override
    public JsonElement serialize(final JPattern src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
      return src.serialize(typeOfSrc, context);
    }
  }
}
