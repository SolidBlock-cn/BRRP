package pers.solid.brrp.v1.tag;

import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pers.solid.brrp.v1.mixin.TagBuilderAccessor;

import java.util.Arrays;
import java.util.function.Function;

/**
 * The class simply extends {@link TagBuilder}, and provides some utilities to add a registry entry (converted to id according to {@link #valueToId}) and tag keys.
 *
 * @see net.minecraft.data.server.tag.ValueLookupTagProvider.ObjectBuilder
 */
public class ObjectTagBuilder<T, Self extends ObjectTagBuilder<T, Self>> extends TagBuilder {
  /**
   * The function to convert a value to its id, which will be used for {@link #add(Object)}.
   */
  public final Function<T, Identifier> valueToId;

  protected ObjectTagBuilder(Function<T, Identifier> valueToId) {
    this.valueToId = valueToId;
  }

  /**
   * Create a new {@link ObjectTagBuilder} instance with a specified function to convert objects to ids.
   *
   * @param valueToId The function to convert object to id.
   */
  @Contract("_ -> new")
  public static <T> Impl<T> create(@NotNull Function<T, Identifier> valueToId) {
    return new Impl<>(valueToId);
  }

  /**
   * Create a new {@link ObjectTagBuilder} that used the registry to convert object to ids.
   *
   * @param registry The registry used to convert objects to ids, which will use {@link Registry#getId(Object)}.
   */
  @Contract("_ -> new")
  public static <T> Impl<T> create(@NotNull Registry<T> registry) {
    return create(registry::getId);
  }

  @Contract("-> this")
  @SuppressWarnings("unchecked")
  protected Self self() {
    return (Self) this;
  }

  @Override
  @Contract(mutates = "this")
  public Self add(@NotNull TagEntry entry) {
    super.add(entry);
    return self();
  }

  /**
   * Add an id of the entry.
   *
   * @param id The id of the entry to add. It is not a tag id.
   */
  @Override
  @Contract(mutates = "this")
  public Self add(@NotNull Identifier id) {
    super.add(id);
    return self();
  }

  /**
   * Add an optional id of the entry.
   *
   * @param id The id of the entry to add. It is not a tag id.
   */
  @Override
  @Contract(mutates = "this")
  public Self addOptional(@NotNull Identifier id) {
    super.addOptional(id);
    return self();
  }

  /**
   * Add another tag to this tag.
   *
   * @param id The id of the tag to be added to this tag.
   */
  @Override
  @Contract(mutates = "this")
  public Self addTag(@NotNull Identifier id) {
    super.addTag(id);
    return self();
  }

  /**
   * Add another tag to this tag.
   *
   * @param tagKey The other tag to be added to this tag.
   */
  @Contract(mutates = "this")
  public Self addTag(@NotNull TagKey<T> tagKey) {
    return addTag(tagKey.id());
  }

  /**
   * Add other tags to this tag.
   *
   * @param tagKeys The other tags to be added to this tag.
   */
  @SafeVarargs
  @Contract(mutates = "this")
  public final Self addTag(@NotNull TagKey<T>... tagKeys) {
    for (TagKey<T> tagKey : tagKeys) {
      this.addTag(tagKey);
    }
    return self();
  }

  @Contract(mutates = "this")
  public Self addTag(@NotNull IdentifiedTagBuilder<T> tagBuilder) {
    return addTag(tagBuilder.identifier);
  }

  @Contract(mutates = "this")
  @SafeVarargs
  public final Self addTag(@NotNull IdentifiedTagBuilder<T>... tagBuilders) {
    for (IdentifiedTagBuilder<T> tagBuilder : tagBuilders) {
      this.addTag(tagBuilder);
    }
    return self();
  }

  /**
   * Add an optional tag to this tag.
   *
   * @param id The id of the tag to be added to this tag.
   */
  @Contract(mutates = "this")
  @Override
  public Self addOptionalTag(@NotNull Identifier id) {
    super.addOptionalTag(id);
    return self();
  }

  /**
   * Add an object to this tag. The {@link #valueToId} will be used to get its id.
   *
   * @param value The object to be added to this tag.
   */
  @Contract(mutates = "this")
  public Self add(T value) {
    return add(valueToId.apply(value));
  }

  /**
   * Add objects to this tag. The {@link #valueToId} will be used to get their id.
   *
   * @param value The objects to be added to this tag.
   */
  @Contract(mutates = "this")
  @SafeVarargs
  public final Self add(T... value) {
    Arrays.stream(value).map(valueToId).forEach(this::add);
    return self();
  }

  @Contract(mutates = "this")
  public Self copy(TagBuilder copyFrom) {
    ((TagBuilderAccessor) copyFrom).getEntries().forEach(this::add);
    return self();
  }

  public static final class Impl<T> extends ObjectTagBuilder<T, Impl<T>> {
    public Impl(Function<T, Identifier> valueToId) {
      super(valueToId);
    }
  }
}
