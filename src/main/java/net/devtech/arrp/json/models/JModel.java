package net.devtech.arrp.json.models;

import com.google.common.collect.Lists;
import net.devtech.arrp.annotations.PreferredEnvironment;
import net.devtech.arrp.json.loot.JCondition;
import net.fabricmc.api.EnvType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>This is the class representing the models in resource packs. It includes block models and item models.</p>
 * <p>Don't be confused with {@link net.devtech.arrp.json.blockstate.JBlockModel}, which is used in a block states definition file.</p>
 *
 * @see net.minecraft.data.client.model.Model
 * @see net.minecraft.client.render.block.BlockModels
 * @see net.minecraft.client.render.model.BakedModel
 * @see net.minecraft.client.render.model.BasicBakedModel
 * @see net.minecraft.client.render.model.UnbakedModel
 * @see net.minecraft.client.render.model.json.JsonUnbakedModel
 */
@SuppressWarnings("unused")
@PreferredEnvironment(EnvType.CLIENT)
public class JModel implements Cloneable {
  /**
   * The identifier of the model it is based on. It should be an identifier. However, simple string is OK.
   */
  public String parent;
  /**
   * Whether the model uses ambient occlusion. If not specified, it is <code>true</code> by default when rendering in game.
   */
  @SuppressWarnings("SpellCheckingInspection")
  public Boolean ambientocclusion;
  /**
   * Specify the display positions in many situations.
   */
  public JDisplay display;
  /**
   * The texture variables of the model.
   */
  public JTextures textures;
  /**
   * The list of model elements. If this field exists, it will totally override the one in the parent model when rendering.
   */
  public List<JElement> elements;
  public List<JOverride> overrides;

  public JModel() {
  }

  public JModel(String parent) {
    this();
    this.parent = parent;
  }

  public JModel(Identifier parent) {
    this();
    this.parent = parent.toString();
  }

  /**
   * @return a new model that does not override it's parent's elements
   * @deprecated Please directly use the constructor {@link #JModel()}.
   */
  @Deprecated
  @Contract("-> new")
  public static JModel modelKeepElements() {
    JModel model = new JModel();
    model.elements = null;
    return model;
  }

  /**
   * @return a new model that does not override it's parent's elements
   * @deprecated Please directly use the constructor {@link #JModel(String)}.
   */
  @Deprecated
  @Contract("_ -> new")
  public static JModel modelKeepElements(String parent) {
    JModel model = new JModel();
    model.parent = parent;
    model.elements = null;
    return model;
  }

  /**
   * @deprecated Please directly use the constructor {@link #JModel(Identifier)}.
   */
  @Deprecated
  @Contract("_ -> new")
  public static JModel modelKeepElements(Identifier identifier) {
    return modelKeepElements(identifier.toString());
  }

  /**
   * @deprecated Please directly use the constructor {@link #JModel()}.
   */
  @Deprecated
  @Contract("-> new")
  public static JModel model() {
    return new JModel();
  }

  /**
   * @deprecated Please directly use the constructor {@link #JModel(String)}.
   */
  @Deprecated
  @Contract("_ -> new")
  public static JModel model(String parent) {
    JModel model = new JModel();
    model.parent = parent;
    return model;
  }

  /**
   * @deprecated Please directly use the constructor {@link JOverride#JOverride(JCondition, String)}.
   */
  @Deprecated
  public static JOverride override(JCondition predicate, Identifier model) {
    return new JOverride(predicate, model.toString());
  }

  /**
   * @deprecated Please directly use the constructor {@link JCondition#JCondition()}.
   */
  @Deprecated
  @Contract("-> new")
  public static JCondition condition() {
    return new JCondition((String) null);
  }

  /**
   * @deprecated Please directly use the constructor {@link #JModel(Identifier)}.
   */
  @Deprecated
  @Contract("_ -> new")
  public static JModel model(Identifier identifier) {
    return model(identifier.toString());
  }

  /**
   * @deprecated Please directly use the constructor {@link JDisplay#JDisplay()}.
   */
  @Deprecated
  @Contract("-> new")
  public static JDisplay display() {
    return new JDisplay();
  }

  /**
   * @deprecated Please directly use the constructor {@link JElement#JElement()}.
   */
  @Deprecated
  @Contract("-> new")
  public static JElement element() {
    return new JElement();
  }

  /**
   * @deprecated Please directly use the constructor {@link JFace#JFace(String)}.
   */
  @Deprecated
  @Contract("_ -> new")
  public static JFace face(String texture) {
    return new JFace(texture);
  }

  /**
   * @deprecated Please directly use the constructor {@link JFaces#JFaces()}.
   */
  @Deprecated
  @Contract("-> new")
  public static JFaces faces() {
    return new JFaces();
  }

  /**
   * @deprecated Please directly use the constructor {@link JPosition#JPosition()}.
   */
  @Deprecated
  @Contract("-> new")
  public static JPosition position() {
    return new JPosition();
  }

  /**
   * @deprecated Please directly use the constructor {@link JRotation#JRotation(Direction.Axis)}.
   */
  @Deprecated
  @Contract("_ -> new")
  public static JRotation rotation(Direction.Axis axis) {
    return new JRotation(axis);
  }

  /**
   * @deprecated Please directly use the constructor {@link JTextures#JTextures()}.
   */
  @Deprecated
  @Contract("-> new")
  public static JTextures textures() {
    return new JTextures();
  }

  @Contract(value = "_ -> this", mutates = "this")
  public JModel addOverride(JOverride override) {
    if (this.overrides == null) {
      this.overrides = Lists.newArrayList(override);
    } else {
      this.overrides.add(override);
    }
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public JModel addOverride(JOverride... overrides) {
    return addOverride(Arrays.asList(overrides));
  }

  @Contract(value = "_ -> this", mutates = "this")
  public JModel addOverride(Collection<? extends JOverride> overrides) {
    if (this.overrides == null) {
      this.overrides = new ArrayList<>(overrides);
    } else {
      this.overrides.addAll(overrides);
    }
    return this;
  }

  /**
   * Set the identifier of model parent. You can also specify the model parent in the {@linkplain #JModel(String) constructor}.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JModel parent(String parent) {
    this.parent = parent;
    return this;
  }

  /**
   * Set the identifier of model parent. You can also specify the model parent in the {@linkplain #JModel(Identifier) constructor}.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JModel parent(Identifier parent) {
    this.parent = parent.toString();
    return this;
  }

  @Contract(value = "-> this", mutates = "this")
  public JModel noAmbientOcclusion() {
    this.ambientocclusion = false;
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public JModel ambientOcclusion(@SuppressWarnings("SpellCheckingInspection") boolean ambientocclusion) {
    this.ambientocclusion = ambientocclusion;
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public JModel display(JDisplay display) {
    this.display = display;
    return this;
  }

  @Contract(value = "_, _ -> this", mutates = "this")
  public JModel addDisplay(JDisplay.DisplayPosition displayPosition, JPosition position) {
    if (this.display == null) display = new JDisplay();
    this.display.put(displayPosition, position);
    return this;
  }

  /**
   * Set the {@linkplain #textures texture variables} of the model. If it exists already, will override it, instead of append it.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JModel textures(JTextures textures) {
    this.textures = textures;
    return this;
  }

  /**
   * Add a texture variable to the {@link #textures} field. This method is a convenient way. For example,
   * <pre>{@code
   * new JModel("block/template_button")
   *    .textures(JTextures.of("texture", "block/stone"))}
   * </pre>
   * can be reduced to
   * <pre>{@code
   * new JModel("block/template_button").addTexture("texture", "block/stone")}</pre>
   */
  @Contract(value = "_, _ -> this", mutates = "this")
  public JModel addTexture(String name, String val) {
    if (textures == null) textures = new JTextures();
    textures.var(name, val);
    return this;
  }

  /**
   * Add several texture variables to the {@link #textures} field. This method is a convenient way.
   *
   * @param strings The texture names and values. Must be an even number.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JModel addTextures(String... strings) {
    if (textures == null) textures = new JTextures();
    textures.vars(strings);
    return this;
  }

  /**
   * Add one or more elements to the model. If the {@link #elements} field already exists, they will be appended to the field.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JModel element(JElement... elements) {
    return element(Arrays.asList(elements));
  }

  /**
   * Add a collection of elements to the model. If the {@link #elements} field already exists, they will be appended to the field.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JModel element(Collection<? extends JElement> elements) {
    if (this.elements == null) {
      this.elements = new ArrayList<>(elements);
    } else {
      this.elements.addAll(elements);
    }
    return this;
  }

  /**
   * Set the {@link #elements} field. If the field exists, it will be directly replaced.<p>
   * In this case you can make different {@code JModel}s share the same {@link #elements} field, which can be an immutable list.
   */
  @Contract(value = "_ -> this", mutates = "this")
  public JModel setElements(List<JElement> elements) {
    this.elements = elements;
    return this;
  }

  @Override
  public JModel clone() {
    try {
      return (JModel) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e);
    }
  }
}
