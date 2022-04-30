package net.devtech.arrp.json.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.devtech.arrp.api.JsonSerializable;
import net.devtech.arrp.json.loot.JCondition;
import net.fabricmc.api.EnvType;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;

/**
 * <p>A predicate for overriding. It is in essence a map, in which a key is a string representing a predicate name, and the value is the float value.</p>
 * <p>Currently, Minecraft does not support non-float values for model predicates. Even if {@code "custom_model_data"} takes an int, the value will be converted to float as well.</p>
 * <p>If you still need non-float values, you may just override {@link #serialize(Type, JsonSerializationContext)} method.</p>
 * <p>Usually the model predicate supports the following keys:</p><code>
 * <ul>
 *   <li>"angle"</li>
 *   <li>"blocking"</li>
 *   <li>"broken"</li>
 *   <li>"cast"</li>
 *   <li>"cooldown"</li>
 *   <li>"damage"</li>
 *   <li>"damaged"</li>
 *   <li>"lefthanded"</li>
 *   <li>"pull"</li>
 *   <li>"pulling"</li>
 *   <li>"throwing"</li>
 *   <li>"time"</li>
 *   <li>"custom_model_data"</li>
 * </ul></code>
 *
 * <p>The class is used for models. If you mean a condition in a loot table, please use {@link JCondition} instead.</p>
 *
 * @see net.minecraft.client.item.ModelPredicateProviderRegistry
 */
@PreferredEnvironment(EnvType.CLIENT)
public class JPredicate extends Object2FloatLinkedOpenHashMap<String> implements JsonSerializable {
  /**
   * This method quite resembles {@link it.unimi.dsi.fastutil.objects.Object2FloatMap#put(Object, float)}, but returns the object itself, making it possible to chain-call.
   */
  @Contract(value = "_, _ -> this", mutates = "this")
  public JPredicate addPredicate(String name, float value) {
    put(name, value);
    return this;
  }

  /**
   * This static method is a simplified version for a simple predicate. Most predicates have only one entry. So you can use this method, for example, <pre>{@code
   * JPredicate.of("time", 0.125);
   * }</pre>
   * in place of<pre>{@code
   * new JPredicate().addPredicate("time", 0.125);
   * }</pre>
   */
  @Contract("_, _ -> new")
  public static JPredicate of(String name, float value) {
    return new JPredicate().addPredicate(name, value);
  }

  @Override
  public JsonElement serialize(Type typeOfSrc, JsonSerializationContext context) {
    final JsonObject object = new JsonObject();
    forEach(object::addProperty);
    return object;
  }
}
