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
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.*;
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
    super(new TranslatableText("brrp.configScreen.title"));
    this.parent = parent;
    beforeVanillaPacks = new ArrayList<>();
    beforeUserPacks = new ArrayList<>();
    afterVanillaPacks = new ArrayList<>();
    PlatformBridge.getInstance().postBefore(null, beforeVanillaPacks);
    PlatformBridge.getInstance().postBeforeUser(null, beforeUserPacks);
    PlatformBridge.getInstance().postAfter(null, afterVanillaPacks);
  }

  @Override
  public void onClose() {
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
    addDrawableChild(beforeVanillaTabButton = new ButtonWidget(width / 2 - 120, 20, 80, 20, new TranslatableText("brrp.configScreen.tab.beforeVanilla", beforeVanillaPacks.size()), button -> setTab(0)));//.narrationSupplier(textSupplier -> new TranslatableText("brrp.configScreen.tab.description", textSupplier.get(), 1, 3)).dimensions().build());
    addDrawableChild(beforeUserTabButton = new ButtonWidget(width / 2 - 40, 20, 80, 20, new TranslatableText("brrp.configScreen.tab.beforeUser", beforeUserPacks.size()), button -> setTab(1)));//.narrationSupplier(textSupplier -> new TranslatableText("brrp.configScreen.tab.description", textSupplier.get(), 2, 3)).dimensions().build());
    addDrawableChild(afterVanillaTabButton = new ButtonWidget(width / 2 + 40, 20, 80, 20, new TranslatableText("brrp.configScreen.tab.afterVanilla", afterVanillaPacks.size()), button -> setTab(2)));//.narrationSupplier(textSupplier -> new TranslatableText("brrp.configScreen.tab.description", textSupplier.get(), 3, 3)).dimensions().build());

    beforeVanillaListWidget = new PackListWidget(beforeVanillaPacks);
    beforeUserListWidget = new PackListWidget(beforeUserPacks);
    afterVanillaListWidget = new PackListWidget(afterVanillaPacks);
    addDrawableChild(backButton = new ButtonWidget(this.width / 2 - 100, this.height - 28, 200, 20, ScreenTexts.BACK, button -> onClose()));

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
    return super.getNarratedTitle().shallowCopy().append(ScreenTexts.LINE_BREAK).append(new TranslatableText("brrp.configScreen.tab.narration", currentTab + 1, 3));
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
    private static final Text NOTHING_HERE = new TranslatableText("brrp.configScreen.nothing");
    private final List<ResourcePack> packList;
    private final @Nullable Text nothingHereText;

    public PackListWidget(List<ResourcePack> packList) {
      super(RRPConfigScreen.this.client, RRPConfigScreen.this.width, RRPConfigScreen.this.height, 40, RRPConfigScreen.this.height - 37, 36);
      this.packList = packList;
      for (ResourcePack resourcePack : packList) {
        addEntry(new Entry(resourcePack));
      }

      if (packList.isEmpty()) {
        this.nothingHereText = NOTHING_HERE;
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
        drawCenteredText(matrices, textRenderer, nothingHereText, width / 2, height / 2 - 8, 0xffffff);
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
      if (getFocused() != null && (getFocused().getFocused() == null)) {
        builder.put(NarrationPart.TITLE, getFocused().titleText);
        builder.put(NarrationPart.USAGE, getFocused().description);
      }
    }

    @Environment(EnvType.CLIENT)
    public class Entry extends ElementListWidget.Entry<Entry> {
      private final ButtonWidget regenerateButton;
      private final ButtonWidget dumpButton;
      private final ResourcePack resourcePack;

      private final Text titleText;
      private final Text description;
      private long lastHoveredTime;

      public Entry(ResourcePack resourcePack) {
        this.resourcePack = resourcePack;
        this.regenerateButton = new ButtonWidget(0, 0, 60, 20, new TranslatableText("brrp.configScreen.regenerate"), button -> {
          if (resourcePack instanceof RuntimeResourcePack runtimeResourcePack) {
            client.setScreen(new RegenerateScreen(RRPConfigScreen.this, runtimeResourcePack));
          }
        }, (button, matrices, mouseX, mouseY) -> {
          if (button.isMouseOver(mouseX, mouseY)) renderOrderedTooltip(matrices, textRenderer.wrapLines(new TranslatableText("brrp.configScreen.regenerate.tooltip").append(resourcePack instanceof RuntimeResourcePack runtimeResourcePack && runtimeResourcePack.hasRegenerationCallback() ? new LiteralText("") : new LiteralText("\n").formatted(Formatting.RED).append(new TranslatableText("brrp.configScreen.regenerate.notSupported"))), 250), mouseX, mouseY);
        });
        this.dumpButton = new ButtonWidget(0, 0, 60, 20, new TranslatableText("brrp.configScreen.dump"), button -> {
          if (resourcePack instanceof RuntimeResourcePack runtimeResourcePack) client.setScreen(new DumpScreen(RRPConfigScreen.this, runtimeResourcePack));
        }, (button, matrices, mouseX, mouseY) -> {
          if (button.isMouseOver(mouseX, mouseY)) renderOrderedTooltip(matrices, textRenderer.wrapLines(new TranslatableText("brrp.configScreen.dump.tooltip"), 250), mouseX, mouseY);
        });
        List<Text> descriptionList = new ArrayList<>();
        if (resourcePack instanceof RuntimeResourcePack runtimeResourcePack) {
          titleText = runtimeResourcePack.getDisplayName();
          final Text description = runtimeResourcePack.getDescription();
          if (description != null) {
            descriptionList.add(new LiteralText("").append(description));
          }
          descriptionList.add(new TranslatableText("brrp.configScreen.summary",
              singleOrPlural("brrp.configScreen.summary.rootResources.", runtimeResourcePack.numberOfRootResources()),
              singleOrPlural("brrp.configScreen.summary.clientResources.", runtimeResourcePack.numberOfClientResources()),
              singleOrPlural("brrp.configScreen.summary.serverData.", runtimeResourcePack.numberOfServerData())));
        } else {
          titleText = new LiteralText(resourcePack.getName());
        }
        @NotNull Set<String> namespaces = Sets.newLinkedHashSet(Iterables.concat(resourcePack.getNamespaces(ResourceType.CLIENT_RESOURCES), resourcePack.getNamespaces(ResourceType.SERVER_DATA)));
        descriptionList.add(new TranslatableText("brrp.configScreen.namespaces", namespaces.isEmpty() ? new TranslatableText("gui.none") : Texts.join(namespaces, Texts.GRAY_DEFAULT_SEPARATOR_TEXT, LiteralText::new).styled(style -> style.withColor(0xddc4aa))).styled(style -> style.withColor(0x909090)));
        this.description = Texts.join(descriptionList, ScreenTexts.LINE_BREAK);
      }

      static MutableText singleOrPlural(String prefix, int number) {
        return new TranslatableText(prefix + (number == 1 ? "single" : "plural"), number);
      }

      @Override
      public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (isMouseOver(mouseX, mouseY)) {
          lastHoveredTime = Util.getMeasuringTimeMs();
        }
        drawTextWithShadow(matrices, textRenderer, titleText, x + 5, y + 2, 0xFFFFFF);
        MultilineText.create(textRenderer, description, width - 151, 2).draw(matrices, x + 5, y + 12, 10, 0xaaaaaa);

        MutableText tooltipContent = new LiteralText("").append(titleText).append(ScreenTexts.LINE_BREAK);
        if (resourcePack instanceof RuntimeResourcePack runtimeResourcePack) {
          tooltipContent.append(new LiteralText(runtimeResourcePack.getId().toString()).formatted(Formatting.GRAY)).append(ScreenTexts.LINE_BREAK);
        }
        tooltipContent.append(description.shallowCopy().formatted(Formatting.GRAY));
        if (isMouseOver(mouseX, mouseY) && !regenerateButton.isHovered() && !dumpButton.isHovered()) {
          renderOrderedTooltip(matrices, textRenderer.wrapLines(tooltipContent, 250), mouseX, mouseY);
        }

        regenerateButton.x = width - 136;
        regenerateButton.y = y + entryHeight / 2 - regenerateButton.getHeight() / 2;
        regenerateButton.active = resourcePack instanceof RuntimeResourcePack runtimeResourcePack && runtimeResourcePack.hasRegenerationCallback();
        regenerateButton.render(matrices, mouseX, mouseY, tickDelta);
        regenerateButton.active = true;
        dumpButton.x = width - 76;
        dumpButton.y = y + entryHeight / 2 - dumpButton.getHeight() / 2;
        dumpButton.render(matrices, mouseX, mouseY, tickDelta);
      }

      @Override
      public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) {
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
        return ImmutableList.of(regenerateButton, dumpButton);
      }
    }
  }
}
