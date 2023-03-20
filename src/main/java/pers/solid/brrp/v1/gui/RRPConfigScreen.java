package pers.solid.brrp.v1.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tooltip.FocusedTooltipPositioner;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import pers.solid.brrp.v1.PlatformBridge;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The configuration screen for the BRRP mod.
 */
@Environment(EnvType.CLIENT)
public class RRPConfigScreen extends Screen {
  protected final List<ResourcePack> beforeVanillaPacks, beforeUserPacks, afterVanillaPacks;
  private final Screen parent;
  protected PackListWidget beforeVanillaListWidget, beforeUserListWidget, afterVanillaListWidget;

  protected ButtonWidget beforeVanillaTabButton;
  protected ButtonWidget beforeUserTabButton;
  protected ButtonWidget afterVanillaTabButton;
  protected int currentTab = 0;
  protected ButtonWidget backButton;

  public RRPConfigScreen(Screen parent) {
    super(Text.translatable("brrp.configScreen.title"));
    this.parent = parent;
    beforeVanillaPacks = new ArrayList<>();
    beforeUserPacks = new ArrayList<>();
    afterVanillaPacks = new ArrayList<>();
    PlatformBridge.getInstance().postBefore(null, beforeVanillaPacks);
    PlatformBridge.getInstance().postBeforeUser(null, beforeUserPacks);
    PlatformBridge.getInstance().postAfter(null, afterVanillaPacks);
  }

  @Override
  public void close() {
    if (client != null) {
      client.setScreen(parent);
    }
  }

  protected void setTab(int tab) {
    beforeVanillaTabButton.active = tab != 0;
    beforeUserTabButton.active = tab != 1;
    afterVanillaTabButton.active = tab != 2;
    if (getFocused() instanceof PackListWidget packListWidget) {
      setFocused(null);
      packListWidget.setFocused(null);
    }
    remove(beforeVanillaListWidget);
    remove(beforeUserListWidget);
    remove(afterVanillaListWidget);
    remove(backButton);  // to ensure it is the last child

    switch (tab) {
      case 0 -> addSelectableChild(beforeVanillaListWidget);
      case 1 -> addSelectableChild(beforeUserListWidget);
      case 2 -> addSelectableChild(afterVanillaListWidget);
    }
    addDrawableChild(backButton);
    currentTab = tab;
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_TAB && hasControlDown()) {
      setTab((currentTab + 3 + (hasShiftDown() ? -1 : 1)) % 3);
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  protected void init() {
    addDrawableChild(beforeVanillaTabButton = ButtonWidget.builder(Text.translatable("brrp.configScreen.tab.beforeVanilla", beforeVanillaPacks.size()), button -> setTab(0)).narrationSupplier(textSupplier -> Text.translatable("brrp.configScreen.tab.description", textSupplier.get(), 1, 3)).dimensions(width / 2 - 120, 20, 80, 20).build());
    addDrawableChild(beforeUserTabButton = ButtonWidget.builder(Text.translatable("brrp.configScreen.tab.beforeUser", beforeUserPacks.size()), button -> setTab(1)).narrationSupplier(textSupplier -> Text.translatable("brrp.configScreen.tab.description", textSupplier.get(), 2, 3)).dimensions(width / 2 - 40, 20, 80, 20).build());
    addDrawableChild(afterVanillaTabButton = ButtonWidget.builder(Text.translatable("brrp.configScreen.tab.afterVanilla", afterVanillaPacks.size()), button -> setTab(2)).narrationSupplier(textSupplier -> Text.translatable("brrp.configScreen.tab.description", textSupplier.get(), 3, 3)).dimensions(width / 2 + 40, 20, 80, 20).build());

    beforeVanillaListWidget = new PackListWidget(beforeVanillaPacks);
    beforeUserListWidget = new PackListWidget(beforeUserPacks);
    afterVanillaListWidget = new PackListWidget(afterVanillaPacks);
    addDrawableChild(backButton = ButtonWidget.builder(ScreenTexts.BACK, button -> close()).dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build());

    setTab(currentTab);
  }

  @Override
  protected void addElementNarrations(NarrationMessageBuilder builder) {
    super.addElementNarrations(builder);
    switch (currentTab) {
      case 0 -> {
        if (beforeVanillaListWidget.nothingHereText != null) {
          builder.nextMessage().put(NarrationPart.USAGE, PackListWidget.NOTHING_HERE);
        }
      }
      case 1 -> {
        if (beforeUserListWidget.nothingHereText != null) {
          builder.nextMessage().put(NarrationPart.USAGE, PackListWidget.NOTHING_HERE);
        }
      }
      case 2 -> {
        if (afterVanillaListWidget.nothingHereText != null) {
          builder.nextMessage().put(NarrationPart.USAGE, PackListWidget.NOTHING_HERE);
        }
      }
    }
  }

  @Override
  public Text getNarratedTitle() {
    return super.getNarratedTitle().copy().append(ScreenTexts.LINE_BREAK).append(Text.translatable("brrp.configScreen.tab.narration", currentTab + 1, 3));
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    this.renderBackgroundTexture(0);

    switch (currentTab) {
      case 0 -> beforeVanillaListWidget.render(matrices, mouseX, mouseY, delta);
      case 1 -> beforeUserListWidget.render(matrices, mouseX, mouseY, delta);
      case 2 -> afterVanillaListWidget.render(matrices, mouseX, mouseY, delta);
    }
    drawCenteredTextWithShadow(matrices, this.textRenderer, this.title.asOrderedText(), this.width / 2, 8, 0xFFFFFF);
    super.render(matrices, mouseX, mouseY, delta);
  }

  @Environment(EnvType.CLIENT)
  public class PackListWidget extends ElementListWidget<PackListWidget.Entry> {
    private static final Text NOTHING_HERE = Text.translatable("brrp.configScreen.nothing");
    private final List<ResourcePack> packList;
    private final @Nullable TextWidget nothingHereText;

    public PackListWidget(List<ResourcePack> packList) {
      super(RRPConfigScreen.this.client, RRPConfigScreen.this.width, RRPConfigScreen.this.height, 40, RRPConfigScreen.this.height - 37, 36);
      this.packList = packList;
      for (ResourcePack resourcePack : packList) {
        addEntry(new Entry(resourcePack));
      }

      if (packList.isEmpty()) {
        this.nothingHereText = new TextWidget(NOTHING_HERE, textRenderer);
        nothingHereText.setWidth(width);
      } else {
        this.nothingHereText = null;
      }
    }

    @Override
    public int getRowWidth() {
      return width - 14;
    }

    @Override
    protected int getScrollbarPositionX() {
      return width - 6;
    }

    @Override
    protected boolean isSelectedEntry(int index) {
      return Objects.equals(getSelectedOrNull(), children().get(index));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
      super.render(matrices, mouseX, mouseY, delta);
      if (nothingHereText != null) {
        nothingHereText.setPos(0, height / 2 - 8);
        nothingHereText.render(matrices, mouseX, mouseY, delta);
      }
    }

    @Override
    protected boolean isFocused() {
      return RRPConfigScreen.this.getFocused() == this;
    }

    @Override
    public void setFocused(@Nullable Element focused) {
      if (getFocused() != null && getFocused() != focused) {
        getFocused().setFocused(null);
      }
      super.setFocused(focused);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
      super.appendNarrations(builder);
      if (getFocused() != null && (getFocused().getFocused() == null || getFocused().getFocused() == getFocused().keyboardPlaceHolder)) {
        builder.put(NarrationPart.TITLE, getFocused().titleText);
        builder.put(NarrationPart.USAGE, getFocused().description);
      }
    }

    @Environment(EnvType.CLIENT)
    public class Entry extends ElementListWidget.Entry<Entry> {
      /**
       * It is not rendered; it is used for keyboard navigation so that you can make the resource pack tooltip render.
       */
      private final TextWidget keyboardPlaceHolder;
      private final ButtonWidget regenerateButton;
      private final ButtonWidget dumpButton;
      private final ResourcePack resourcePack;

      private final Text titleText;
      private final Text description;
      private final TextWidget area;
      private long lastHoveredTime;

      public Entry(ResourcePack resourcePack) {
        this.resourcePack = resourcePack;
        this.keyboardPlaceHolder = new TextWidget(0, 0, 60, 20, ScreenTexts.EMPTY, textRenderer);
        keyboardPlaceHolder.active = true;
        this.regenerateButton = ButtonWidget.builder(Text.translatable("brrp.configScreen.regenerate"), button -> {
          if (resourcePack instanceof RuntimeResourcePack runtimeResourcePack) {
            client.setScreen(new RegenerateScreen(RRPConfigScreen.this, runtimeResourcePack));
          }
        }).dimensions(0, 0, 60, 20).tooltip(Tooltip.of(Text.translatable("brrp.configScreen.regenerate.tooltip").append(resourcePack instanceof RuntimeResourcePack runtimeResourcePack && runtimeResourcePack.hasRegenerationCallback() ? Text.empty() : Text.literal("\n").formatted(Formatting.RED).append(Text.translatable("brrp.configScreen.regenerate.notSupported"))))).build();
        this.dumpButton = ButtonWidget.builder(Text.translatable("brrp.configScreen.dump"), button -> {
          if (resourcePack instanceof RuntimeResourcePack runtimeResourcePack) client.setScreen(new DumpScreen(RRPConfigScreen.this, runtimeResourcePack));
        }).tooltip(Tooltip.of(Text.translatable("brrp.configScreen.dump.tooltip"))).dimensions(0, 0, 60, 20).build();
        List<Text> descriptionList = new ArrayList<>();
        if (resourcePack instanceof RuntimeResourcePack runtimeResourcePack) {
          titleText = runtimeResourcePack.getDisplayName();
          final Text description = runtimeResourcePack.getDescription();
          if (description != null) {
            descriptionList.add(Text.empty().append(description));
          }
          descriptionList.add(Text.translatable("brrp.configScreen.summary",
              singleOrPlural("brrp.configScreen.summary.rootResources.", runtimeResourcePack.numberOfRootResources()),
              singleOrPlural("brrp.configScreen.summary.clientResources.", runtimeResourcePack.numberOfClientResources()),
              singleOrPlural("brrp.configScreen.summary.serverData.", runtimeResourcePack.numberOfServerData())));
        } else {
          titleText = Text.literal(resourcePack.getName());
        }
        @NotNull Set<String> namespaces = Sets.newLinkedHashSet(Iterables.concat(resourcePack.getNamespaces(ResourceType.CLIENT_RESOURCES), resourcePack.getNamespaces(ResourceType.SERVER_DATA)));
        descriptionList.add(Text.translatable("brrp.configScreen.namespaces", namespaces.isEmpty() ? Text.translatable("gui.none") : Texts.join(namespaces, Texts.GRAY_DEFAULT_SEPARATOR_TEXT, Text::literal).styled(style -> style.withColor(0xddc4aa))).styled(style -> style.withColor(0x909090)));
        this.description = Texts.join(descriptionList, ScreenTexts.LINE_BREAK);
        keyboardPlaceHolder.setTooltip(Tooltip.of(description));

        area = new TextWidget(width / 2 - getRowWidth() / 2, 0, getRowWidth(), itemHeight, Text.empty(), textRenderer);
      }

      static MutableText singleOrPlural(String prefix, int number) {
        return Text.translatable(prefix + (number == 1 ? "single" : "plural"), number);
      }

      @Override
      public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (isMouseOver(mouseX, mouseY)) {
          lastHoveredTime = Util.getMeasuringTimeMs();
        }
        enableScissor(5, y, width - 141, y + entryHeight);
        drawTextWithShadow(matrices, textRenderer, titleText, x + 5, y + 2, 0xFFFFFF);
        MultilineText.create(textRenderer, description, width - 151).draw(matrices, x + 5, y + 12, 10, 0xaaaaaa);
        disableScissor();

        area.setPos(width / 2 - entryWidth / 2, y);
        area.setWidth(entryWidth);
        MutableText tooltipContent = Text.empty().append(titleText).append(ScreenTexts.LINE_BREAK);
        if (resourcePack instanceof RuntimeResourcePack runtimeResourcePack) {
          tooltipContent.append(Text.literal(runtimeResourcePack.getId().toString()).formatted(Formatting.GRAY)).append(ScreenTexts.LINE_BREAK);
        }
        tooltipContent.append(description.copy().formatted(Formatting.GRAY));
        if (isMouseOver(mouseX, mouseY) && !regenerateButton.isMouseOver(mouseX, mouseY) && !dumpButton.isMouseOver(mouseX, mouseY)) {
          setTooltip(tooltipContent);
        } else if (PackListWidget.this.isFocused() && (PackListWidget.this.getSelectedOrNull() == this || area.isHovered()) && (getFocused() == null || keyboardPlaceHolder.isFocused())) {
          setTooltip(Tooltip.of(tooltipContent), new FocusedTooltipPositioner(area), true);
        }
        keyboardPlaceHolder.setPos(width - 196, y + entryHeight / 2 - keyboardPlaceHolder.getHeight() / 2);   // don't render it;

        regenerateButton.setPos(width - 136, y + entryHeight / 2 - regenerateButton.getHeight() / 2);
        regenerateButton.active = resourcePack instanceof RuntimeResourcePack runtimeResourcePack && runtimeResourcePack.hasRegenerationCallback();
        regenerateButton.render(matrices, mouseX, mouseY, tickDelta);
        regenerateButton.active = true;
        dumpButton.setPos(width - 76, y + entryHeight / 2 - dumpButton.getHeight() / 2);
        dumpButton.render(matrices, mouseX, mouseY, tickDelta);
      }

      @Override
      public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (keyboardPlaceHolder.isMouseOver(mouseX, mouseY) || !super.mouseClicked(mouseX, mouseY, button)) {
          setSelected(this);
          this.setFocused(null);
          PackListWidget.this.setFocused(this);
          return false;
        }
        return true;
      }

      @Override
      public void setFocused(@Nullable Element focused) {
        super.setFocused(focused);
        PackListWidget.this.setFocused(this);
        PackListWidget.this.setSelected(this);
      }

      @Override
      public List<? extends Selectable> selectableChildren() {
        return ImmutableList.of(regenerateButton, dumpButton);
      }

      @Override
      public List<? extends Element> children() {
        return ImmutableList.of(keyboardPlaceHolder, regenerateButton, dumpButton);
      }
    }
  }
}
