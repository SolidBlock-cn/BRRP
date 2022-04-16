package net.devtech.arrp.json.tags;

import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.util.Identifier;

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
public class IdentifiedTag extends JTag {
  /**
   * The type of the identifier. It is usually one of the following values: {@code blocks entity_types fluids functions game_events items worldgen}, but a customized value is also OK.
   */
  public transient final String type;
  /**
   * The identifier without the type specification. This identifier is used in most situations, for example, in commands, other tags or other data-pack files.
   */
  public transient final Identifier identifier;
  /**
   * The identifier with the type specification. It's in the format of <code><i>namespace</i>:<i>type</i>/<i>path</i></code>, where the <i>namespace</i> and <i>path</i> are those of the {@link #identifier}. It's used as a resource location, for example, when written into the runtime resource pack, or generated as a normal data pack.<br>
   * For example, for the block tag {@code minecraft:logs}, the identifier is {@code minecraft:logs} and the full identifier is {@code minecraft:blocks/logs}.
   */
  public transient final Identifier fullIdentifier;

  public IdentifiedTag(String type, Identifier identifier) {
    this.type = type;
    this.identifier = identifier;
    fullIdentifier = new Identifier(identifier.getNamespace(), this.type + "/" + identifier.getPath());
  }

  public IdentifiedTag(String namespace, String type, String path) {
    this(type, new Identifier(namespace, path));
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
