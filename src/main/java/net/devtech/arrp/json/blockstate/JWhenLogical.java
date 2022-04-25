package net.devtech.arrp.json.blockstate;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.devtech.arrp.api.JsonSerializable;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;
import net.minecraft.data.client.model.When;
import net.minecraft.state.StateManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * <p>A <b>JWhenLogical</b> is a logical connection of multiple "when"s. It's composed of two parts:</p>
 * <ul><li>
 *   <b>{@linkplain  #operator}</b> - The logical representation of these several conditions. It is usually "OR", while "AND" is <i>defined but not used</i> in vanilla Minecraft, and there is no guarantee that "AND" is valid.
 * </li><li>
 *   <b>{@linkplain  #components}</b> - The multiple conditions, which is a simple list of "{@link When}" objects.
 * </li></ul>
 * <p>This is a simple <i>forwarding list</i> for {@link #components}, so you can call list methods, such as {@link #add}, {@link #addAll}, to directly modify the {@code components}.</p>
 * <p>It quite resembles {@link net.minecraft.data.client.model.When.LogicalOperator}, as it's just written based on it, with some enhancements.</p>
 *
 * @see net.minecraft.data.client.model.When.LogicalOperator
 */
public class JWhenLogical extends ForwardingList<When> implements When, JsonSerializable {
  public final @NotNull LogicalOperator operator;
  public final @NotNull List<When> components;

  /**
   * Create a simple JWhenLogical object. However, directly calling the constructor is <i>not</i> recommended, unless you're sure to do that, for example, making multiple objects share one list of components, or making the list immutable. In most cases, you should directly call {@link #anyOf(When...)} or {@link #allOf(When...)}.
   *
   * @param operator   The logical operator.
   * @param components The list of components. It will be directly used as the field. It can be immutable, making methods like {@link #addCondition(When)} fail.
   */
  @ApiStatus.Internal
  public JWhenLogical(@NotNull LogicalOperator operator, @NotNull List<When> components) {
    this.operator = operator;
    this.components = components;
  }

  /**
   * Create a simple JWhenLogical object, which, when used for rendering, passes as long as one of the conditions is met. This is an example:
   * <pre>{@code
   * anyOf(
   *  JWhenProperties.of("direction", "up", "down"),
   *  JWhenProperties.of("waterlogged", "false"));
   * }</pre>
   * which can be serialized as followings:
   * <pre>{@code
   * {"OR": [
   *   {"direction": "up|down"},
   *   {"waterlogged": "fase" } ]}
   * }</pre>
   */
  public static JWhenLogical anyOf(When... conditions) {
    return new JWhenLogical(LogicalOperator.OR, Lists.newArrayList(conditions));
  }

  /**
   * Create a simple JWhenLogical object, which, when used for rendering, passes only if all conditions is met. Note that vanilla Minecraft does not use this type of logical condition, and there is no guarantee that it takes effect.
   */
  public static JWhenLogical allOf(When... conditions) {
    return new JWhenLogical(LogicalOperator.AND, Lists.newArrayList(conditions));
  }

  /**
   * Add a condition to its components. Of course, you can also assemble the conditions well when constructing. It is quite similar to {@link List#add(Object)}, but returns the object itself.
   */
  @CanIgnoreReturnValue
  @Contract("_ -> this")
  public JWhenLogical addCondition(When condition) {
    components.add(condition);
    return this;
  }

  /**
   * Add conditions to its components. Of course, you can also assemble the conditions well when constructing. It is similar to {@link List#add(Object)}, but returns the object itself.
   */
  @CanIgnoreReturnValue
  @Contract("_ -> this")
  public JWhenLogical addCondition(When... condition) {
    components.addAll(Arrays.asList(condition));
    return this;
  }

  @Override
  protected @NotNull List<When> delegate() {
    return components;
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    JsonArray jsonArray = new JsonArray();
    this.components.stream().map(Supplier::get).forEach(jsonArray::add);
    JsonObject jsonObject = new JsonObject();
    jsonObject.add(this.operator.name(), jsonArray);
    return jsonObject;
  }

  @Override
  public void validate(StateManager<?, ?> stateManager) {
    this.components.forEach(component -> component.validate(stateManager));
  }

  @Override
  public JsonElement get() {
    return RuntimeResourcePackImpl.GSON.toJsonTree(this);
  }
}
