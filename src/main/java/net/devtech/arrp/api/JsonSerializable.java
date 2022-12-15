package net.devtech.arrp.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * <p>Implement this interface, and in the {@link net.devtech.arrp.impl.RuntimeResourcePackImpl#GSON}  the method {@link #serialize} will be used to serialize.
 * <p>To make your GSON able to use the method, do this in your GSONBuilder:
 * <pre>
 *   {@code
 *       new GsonBuilder()
 *        .registerTypeHierarchyAdapter(JSONSerializable.class, JSONSerializable.SERIALIZER)}
 * </pre>
 * <p>Do not implement this method as anonymous classes, as GSON may fail to correctly serialize it.
 */
public interface JsonSerializable {
  /**
   * Your class should override this method to specify how to serialize. This is slightly like {@link JsonSerializer#serialize(Object, Type, JsonSerializationContext)}, while the parameter <code>src</code> is replaced with <code>this</code>.
   *
   * @param typeOfSrc the actual type (fully genericized version) of the source object.
   * @return a JsonElement corresponding to the specified object.
   */
  JsonElement serialize(Type typeOfSrc, JsonSerializationContext context);

  /**
   * The serializer to be registered in your GSONBuilder.
   */
  JsonSerializer<JsonSerializable> SERIALIZER = JsonSerializable::serialize;
}
