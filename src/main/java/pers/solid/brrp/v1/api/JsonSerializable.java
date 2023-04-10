package pers.solid.brrp.v1.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * <p>Implement this interface, and in the {@link RuntimeResourcePack#GSON} the objects implementing this interface will be used it to serialize to JSON objects.
 * <p>To make your own GSON able to use the method, do this in your GSONBuilder:
 * <pre>
 *   {@code
 *       new GsonBuilder()
 *        .registerTypeHierarchyAdapter(JSONSerializable.class, JSONSerializable.SERIALIZER)}
 * </pre>
 * <p>Do not implement this method in anonymous classes, as GSON may fail to correctly serialize it.
 */
public interface JsonSerializable {
  /**
   * The serializer to be registered in your GSONBuilder.
   */
  JsonSerializer<JsonSerializable> SERIALIZER = JsonSerializable::serialize;

  /**
   * Your class should override this method to specify how to serialize. This is slightly like {@link JsonSerializer#serialize(Object, Type, JsonSerializationContext)}, while the parameter <code>src</code> is replaced with <code>this</code>.
   *
   * @param typeOfSrc the actual type (fully genericized version) of the source object.
   * @return a JsonElement corresponding to the specified object.
   */
  JsonElement serialize(Type typeOfSrc, JsonSerializationContext context);
}
