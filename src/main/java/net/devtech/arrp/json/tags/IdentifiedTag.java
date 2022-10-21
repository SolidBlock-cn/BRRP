package net.devtech.arrp.json.tags;

import net.devtech.arrp.util.CanIgnoreReturnValue;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;

/**
 * <p>It's similar to {@link JTag}, but the identifier and type of the tag is stored within the tag itself.</p>
 * <p><b>Attention:</b> When chain-calling this object, please pay attention that the return type of the methods is {@link JTag} instead of {@link IdentifiedTag}, even if actually the returned values are. Therefore, the following code is invalid:</p>
 * <pre>{@code
 * IdentifiedTag myTag = new IdentifiedTag(type, identifier).add(...).add(...).add(...);
 * }</pre>
 * <p>However, the following codes are valid:</p>
 * <pre>{@code
 * IdentifiedTag myTag = (IdentifiedTag) new IdentifiedTag(type, identifier).add(...).add(...).add(...);
 * }</pre>
 * or
 * <pre>{@code
 * IdentifiedTag myTag = new IdentifiedTag(type, identifier);
 * myTag.add(...).add(...).add(...);
 * }</pre>
 *
 * @author SolidBlock
 */
@SuppressWarnings("unused")
public class IdentifiedTag extends JTag {
  /**
   * The type of the tag. It is usually one of the following values: {@code blocks entity_types fluids functions game_events items worldgen}, but a customized value is also OK.
   */
  public transient final String type;
  /**
   * The identifier without the type specification. This identifier is used in most situations, for example, in commands, other tags or other data-pack files.
   */
  public transient final Identifier identifier;
  /**
   * The identifier with the type specification. It's in the format of <code style=color:maroon><i>namespace</i>:<i>type</i>/<i>path</i></code>, where the <i>namespace</i> and <i>path</i> are those of the {@link #identifier}. It's used as a resource location, for example, when written into the runtime resource pack, or generated as a normal data pack.<br>
   * For example, for the block tag <code style=color:maroon>minecraft:logs</code>, the identifier is <code style=color:maroon>minecraft:logs</code> and the full identifier is <code style=color:maroon>minecraft:blocks/logs</code>.
   */
  public transient final Identifier fullIdentifier;

  /**
   * Create a new {@link IdentifiedTag} object with the specified type and the identifier. The full identifier will be automatically composed.
   *
   * @param type       The type of the tag. See {@link #type}.
   * @param identifier The identifier without the type specification. See {@link #identifier}.
   */
  public IdentifiedTag(String type, Identifier identifier) {
    this.type = type;
    this.identifier = identifier;
    fullIdentifier = new Identifier(identifier.getNamespace(), this.type + "/" + identifier.getPath());
  }

  /**
   * Create a new {@link IdentifiedTag} object with the specified type, and the namespace and path of the identifier. The identifier will be automatically constructed with the namespace and path, and the full identifier will be automatically composed.
   *
   * @param namespace The namespace of the identifier.
   * @param type      The type of the tag. See {@link #type}.
   * @param path      The path of the identifier, without the type specification.
   */
  public IdentifiedTag(String namespace, String type, String path) {
    this(type, new Identifier(namespace, path));
  }

  /**
   * Create a new {@link IdentifiedTag} object with the specified type, and the same replaced and values as this. This is useful when creating a new object with the same namespace and path but a different type.
   *
   * @param type The type of the tag. See {@link #type}.
   * @return A new {@link IdentifiedTag} object with the specified type.
   */
  @Contract("_ -> new")
  public IdentifiedTag identified(String type) {
    return identified(type, identifier);
  }

  /**
   * Write this tag into the runtime resource pack, using the {@link #fullIdentifier}.
   *
   * @param pack The runtime resource pack.
   */

  public byte[] write(RuntimeResourcePack pack) {
    return pack.addTag(fullIdentifier, this);
  }

  @Override
  public IdentifiedTag clone() {
    return (IdentifiedTag) super.clone();
  }
}
