package net.devtech.arrp.json.blockstate;

import com.google.gson.*;
import net.devtech.arrp.api.JsonSerializable;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.minecraft.data.client.When;
import net.minecraft.state.StateManager;
import net.minecraft.util.Pair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * <p>This is an object used to specify the condition of a multipart, and if the condition matches, the part will be used.</p>
 * <p>However, it has several issues. It does not supported complex situations, or even a simple situation with multiple properties stated. It does not even distinguage property condition and logical condition.</p>
 * <p>As a result, as you may have seen, this class is deprecated in BRRP. You should use {@link JWhenProperties} or {@link JWhenLogical} instead. As you can see as well, this class as well as several improved classes in BRRP, implements {@link When}, which is a vanilla class.</p>
 * <p>However, you can directly use {@link #delegate(When)}, which creates an object with vanilla serialization. You can create the delegate with {@link When#create()} , {@link #anyOf(When...)} or {@link #allOf(When...)}. Do not use classes in <i>this</i> mod as parameters of {@link #delegate}.</p>
 *
 * @see net.minecraft.data.client.When
 */
@Deprecated
public class JWhen implements Cloneable, JsonSerializable, When {
  private final List<Pair<String, String[]>> OR = new ArrayList<>();

  public JWhen() {
  }

  public JWhen add(String condition, String... states) {
    this.OR.add(new Pair<>(condition, states));
    return this;
  }

  @Override
  public JWhen clone() {
    try {
      return (JWhen) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }

  @Override
  public JsonElement get() {
    return RuntimeResourcePackImpl.GSON.toJsonTree(this);
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    if (this.OR.size() == 1) {
      JsonObject json = new JsonObject();
      Pair<String, String[]> val = this.OR.get(0);
      json.addProperty(val.getLeft(), String.join("|", Arrays.asList(val.getRight())));
      return json;
    } else {
      JsonObject or = new JsonObject();
      JsonArray array = new JsonArray();
      for (Pair<String, String[]> val : this.OR) {
        JsonObject json = new JsonObject();
        json.addProperty(val.getLeft(), String.join("|", Arrays.asList(val.getRight())));
        array.add(json);
      }
      or.add("OR", array);
      return or;
    }
  }

  @Override
  public void validate(StateManager<?, ?> stateManager) {
    // It does not need validation
  }

  /**
   * Create a delegated object, whose serialization will be same as the delegate.
   *
   * @param delegate The delegate object. It is usually a vanilla {@link When} object ({@link net.minecraft.data.client.When.PropertyCondition} or {@link net.minecraft.data.client.When.LogicalCondition}, which can be generated with {@link When#create()}, {@link When#anyOf(When...)}, or {@link When#allOf(When...)}. Its serialization will be directly used.
   * @return The delegated object.
   */
  public When delegate(When delegate) {
    if (delegate instanceof JWhen || delegate instanceof JWhenProperties || delegate instanceof JWhenLogical) {
      throw new IllegalArgumentException("Only vanilla 'When' objects can be delegated.");
    }
    return new Delegate(delegate);
  }

  /**
   * @deprecated This class is kept for compatibility.
   */
  @Deprecated
  public static class Serializer implements JsonSerializer<JWhen> {
    @Override
    public JsonElement serialize(JWhen src, Type typeOfSrc, JsonSerializationContext context) {
      return src.serialize(typeOfSrc, context);
    }
  }

  private static final class Delegate implements JsonSerializable, Supplier<JsonElement>, When {
    public final When delegate;

    private Delegate(When delegate) {
      this.delegate = delegate;
    }

    @Override
    public JsonElement get() {
      return delegate.get();
    }

    @Override
    public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
      return delegate.get();
    }

    @Override
    public void validate(StateManager<?, ?> stateManager) {
      delegate.validate(stateManager);
    }
  }
}
