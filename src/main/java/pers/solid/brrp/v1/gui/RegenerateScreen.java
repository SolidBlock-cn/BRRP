package pers.solid.brrp.v1.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

/**
 * The screen to regenerate resources for a runtime resource pack.
 */
@Environment(EnvType.CLIENT)
public class RegenerateScreen extends Screen {
  private static final Logger LOGGER = LoggerFactory.getLogger("BRRP/RegenerateScreen");
  protected static final Text STATE_REGEN_CLIENT = Text.translatable("brrp.regenerateScreen.state.client");
  protected static final Text STATE_REGEN_SERVER = Text.translatable("brrp.regenerateScreen.state.server");
  protected static final Text STATE_REGEN_ALL = Text.translatable("brrp.regenerateScreen.state.all");
  protected static final Text STATE_IDLE = Text.translatable("brrp.regenerateScreen.state.idle");
  private final Screen parent;
  private final RuntimeResourcePack pack;
  protected Thread currentThread;
  private TextWidget stateText;
  private MultilineTextWidget summaryText;
  private ButtonWidget onlyRegenClientButton, onlyRegenServerButton, regenButton;
  private ButtonWidget interruptButton;
  private ButtonWidget backButton;

  protected RegenerateScreen(Screen parent, @NotNull RuntimeResourcePack pack) {
    super(Text.translatable("brrp.regenerateScreen.title", pack.getDisplayName()));
    this.parent = parent;
    this.pack = pack;
  }

  @Override
  public Text getNarratedTitle() {
    return super.getNarratedTitle().copy().append(ScreenTexts.LINE_BREAK).append(STATE_IDLE);
  }

  @Override
  protected void init() {
    super.init();
    stateText = new TextWidget(10, 25, width - 20, 20, STATE_IDLE, textRenderer).alignCenter().setTextColor(0xeeee66);
    summaryText = new MultilineTextWidget(10, 45, ScreenTexts.EMPTY, textRenderer).setCentered(true).setMaxWidth(width - 20).setTextColor(0xffcccccc);
    onlyRegenClientButton = ButtonWidget.builder(Text.translatable("brrp.regenerateScreen.onlyRegenClient"), button -> regenClientOnly()).dimensions(width / 2 - 100, 90, 200, 20).build();
    onlyRegenServerButton = ButtonWidget.builder(Text.translatable("brrp.regenerateScreen.onlyRegenServer"), button -> regenServerOnly()).dimensions(width / 2 - 100, 110, 200, 20).build();
    regenButton = ButtonWidget.builder(Text.translatable("brrp.regenerateScreen.regenAll"), button -> regenAll()).dimensions(width / 2 - 100, 130, 200, 20).build();
    addDrawableChild(stateText);
    addDrawableChild(summaryText);
    addDrawableChild(onlyRegenClientButton);
    addDrawableChild(onlyRegenServerButton);
    addDrawableChild(regenButton);
    addDrawableChild(interruptButton = ButtonWidget.builder(Text.translatable("brrp.regenerateScreen.interrupt"), button -> {
      if (currentThread != null) {
        if (currentThread.isAlive()) LOGGER.warn("Interrupting thread {}!", currentThread);
        currentThread.interrupt();
        try {
          currentThread.join();
        } catch (InterruptedException e) {
          LOGGER.warn("Waiting the thread to finish:", e);
        }
      }
    }).dimensions(width/2-100, height - 48, 200, 20).tooltip(Tooltip.of(Text.translatable("brrp.regenerateScreen.interrupt.tooltip"))).build());
    interruptButton.active = false;
    addDrawableChild(backButton = ButtonWidget.builder(ScreenTexts.BACK, button -> close()).dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build());
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    renderBackground(matrices);
    drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
    summaryText.setX(width / 2 - summaryText.getWidth() / 2);
    boolean isIdle = currentThread == null || !currentThread.isAlive();
    onlyRegenClientButton.active = isIdle && pack.hasSidedRegenerationCallback(ResourceType.CLIENT_RESOURCES);
    onlyRegenServerButton.active = isIdle && pack.hasSidedRegenerationCallback(ResourceType.SERVER_DATA);
    regenButton.active = isIdle && pack.hasRegenerationCallback();
    backButton.active = isIdle;
    interruptButton.active = !isIdle;
    if (isIdle) {
      stateText.setMessage(STATE_IDLE);
    }
    super.render(matrices, mouseX, mouseY, delta);
  }

  private void regenClientOnly() {
    currentThread = new Thread(() -> {
      try {
        pack.regenerateSided(ResourceType.CLIENT_RESOURCES);
      } catch (InterruptedException e) {
        LOGGER.warn("Interrupted:", e);
      }
    }, "Regenerate client resources");
    stateText.setMessage(STATE_REGEN_CLIENT);
    currentThread.start();
  }

  private void regenServerOnly() {
    currentThread = new Thread(() -> {
      try {
        pack.regenerateSided(ResourceType.SERVER_DATA);
      } catch (InterruptedException e) {
        LOGGER.warn("Interrupted:", e);
      }
    }, "Regenerate server data");
    stateText.setMessage(STATE_REGEN_SERVER);
    currentThread.start();
  }

  private void regenAll() {
    currentThread = new Thread(() -> {
      try {
        pack.regenerate();
      } catch (InterruptedException e) {
        LOGGER.warn("Interrupted:", e);
      }
    }, "Regenerate all resources");
    stateText.setMessage(STATE_REGEN_ALL);
    currentThread.start();
  }

  @Override
  public void tick() {
    super.tick();
    summaryText.setMessage(Text.translatable("brrp.configScreen.summary",
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.rootResources.", pack.numberOfRootResources()),
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.clientResources.", pack.numberOfClientResources()),
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.serverData.", pack.numberOfServerData())));
  }

  @Override
  public void close() {
    if (currentThread != null && currentThread.isAlive()) return;
    if (client != null) {
      client.setScreen(parent);
    }
  }
}
