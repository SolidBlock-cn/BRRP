package pers.solid.brrp.v1.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.io.file.PathUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

/**
 * The screen to dump a runtime resource pack.
 */
@Environment(EnvType.CLIENT)
public class DumpScreen extends Screen {
  private static final Logger LOGGER = LoggerFactory.getLogger("BRRP/DumpScreen");
  private final Screen parent;
  private final RuntimeResourcePack pack;
  private final int[] dumpStat = new int[3];
  private CyclingButtonWidget<DumpType> dumpTypeButton;
  private @NotNull DumpType dumpType = DumpType.ALL;
  private Text dumpPathText;
  private TextFieldWidget dumpPathTextField;
  private Path dumpPath;
  private String dumpPathString;
  private InvalidPathException invalidPathException;
  private MultilineText dumpPathPreviewText;
  private Text summaryText;
  private Text dumpProgressText;
  private ButtonWidget dumpButton;
  private ButtonWidget interruptButton;
  private ButtonWidget backButton;
  private Thread dumpThread;

  protected DumpScreen(Screen parent, @NotNull RuntimeResourcePack pack) {
    super(Text.translatable("brrp.dumpScreen.title", pack.getDisplayName()));
    this.parent = parent;
    this.pack = pack;
    dumpPath = RuntimeResourcePack.DEFAULT_OUTPUT.resolve(pack.getId().getNamespace() + "/" + pack.getId().getPath());
    dumpPathString = dumpPath.toString();
  }

  @Override
  protected void init() {
    super.init();
    dumpTypeButton = new CyclingButtonWidget.Builder<DumpType>(dumpType -> Text.translatable("brrp.dumpScreen.dumpType." + dumpType.asString()))
        .values(DumpType.values())
        .initially(dumpType)
        .tooltip(value -> Collections.singletonList(Text.translatable("brrp.dumpScreen.dumpType.tooltip." + value.asString()).asOrderedText()))
        .build(width / 2 - 100, 30, 200, 20, Text.translatable("brrp.dumpScreen.dumpType"), (button, value) -> dumpType = value);
    addDrawableChild(dumpTypeButton);
    dumpPathText = Text.translatable("brrp.dumpScreen.dumpPath.title");
    dumpPathTextField = new TextFieldWidget(textRenderer, 20, 85, width - 40, 20, Text.translatable("brrp.dumpScreen.dumpPath.message"));
    dumpPathTextField.setMaxLength(64);
    dumpPathTextField.setChangedListener(s -> {
      dumpPathString = s;
      MutableText text;
      try {
        dumpPath = Path.of(s);
        invalidPathException = null;
        text = Text.translatable("brrp.dumpScreen.dumpToPath", dumpPath.toAbsolutePath().toString()).formatted(Formatting.GREEN);
        dumpPathPreviewText = MultilineText.create(textRenderer, text, width - 20);
      } catch (InvalidPathException exception) {
        invalidPathException = exception;
        text = Text.translatable("brrp.dumpScreen.dumpInvalidPath", invalidPathException.getMessage()).formatted(Formatting.RED);
        dumpPathPreviewText = MultilineText.create(textRenderer, text, width - 20);
        dumpButton.active = false;
      }
    });
    addDrawableChild(dumpPathTextField);

    dumpPathTextField.setText(dumpPathString);


    summaryText = Text.translatable("brrp.configScreen.summary",
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.rootResources.", pack.numberOfRootResources()),
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.clientResources.", pack.numberOfClientResources()),
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.serverData.", pack.numberOfServerData()));
    dumpProgressText = ScreenTexts.EMPTY;
    dumpButton = new ButtonWidget(width / 2 - 200, height - 53, 200, 20, Text.translatable("brrp.dumpScreen.dump"), button -> runDump());
    addDrawableChild(dumpButton);
    interruptButton = new ButtonWidget(width / 2, height - 53, 200, 20, Text.translatable("brrp.dumpScreen.interrupt"), button -> {
      if (dumpThread != null) {
        dumpThread.interrupt();
        try {
          dumpThread.join();
        } catch (InterruptedException e) {
          LOGGER.error("Interrupted dump screen:", e);
        }
      }
    }, (button, matrices, mouseX, mouseY) -> renderOrderedTooltip(matrices, textRenderer.wrapLines(Text.translatable("brrp.dumpScreen.interrupt.tooltip"), 250), mouseX, mouseY));
    interruptButton.active = false;
    addDrawableChild(interruptButton);
    addDrawableChild(backButton = new ButtonWidget(this.width / 2 - 100, this.height - 28, 200, 20, ScreenTexts.BACK, button -> close()));
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    renderBackground(matrices);
    dumpPathPreviewText.drawCenterWithShadow(matrices, width / 2, 120, 16, 0xffffff);
    textRenderer.draw(matrices, dumpPathText, 20, 65, 0xcccccc);
    drawCenteredText(matrices, textRenderer, summaryText, width / 2, height - 89, 0xffcccccc);
    drawCenteredText(matrices, textRenderer, dumpProgressText, width / 2, height - 73, 0xffcccccc);
    drawCenteredTextWithShadow(matrices, this.textRenderer, this.title.asOrderedText(), this.width / 2, 8, 0xFFFFFF);
    super.render(matrices, mouseX, mouseY, delta);
    if (dumpPathTextField.isMouseOver(mouseX, mouseY)) {
      renderOrderedTooltip(matrices, textRenderer.wrapLines(Text.translatable("brrp.dumpScreen.dumpPath.tooltip"), 250), mouseX, mouseY);
    }
  }

  @Override
  public void tick() {
    super.tick();
    dumpButton.active = invalidPathException == null && (dumpThread == null || !dumpThread.isAlive());
    if (dumpThread != null && dumpThread.isAlive()) {
      if (dumpStat[0] == -1) {
        dumpProgressText = Text.translatable("brrp.dumpScreen.removeExisting");
      } else {
        dumpProgressText = Text.translatable("brrp.dumpScreen.dumpSummary",
            RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.rootResources.", dumpStat[0]),
            RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.clientResources.", dumpStat[1]),
            RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.serverData.", dumpStat[2]));
      }
    } else if (dumpThread != null) {
      dumpProgressText = ScreenTexts.DONE;
    }
  }

  private void runDump() {
    runDump(false);
  }

  private void runDump(boolean ignoreExistingWarning) {
    try {
      if (!ignoreExistingWarning && PathUtils.isDirectory(dumpPath) && !PathUtils.isEmptyDirectory(dumpPath)) {
        final long size = Files.walk(dumpPath).limit(2001).count();
        if (client != null) {
          dumpProgressText = ScreenTexts.EMPTY;
          client.setScreen(new ConfirmScreen(value -> {
            if (client != null) {
              client.setScreen(this);
              if (value) {
                runDump(true);
              }
            }
          }, Text.translatable("brrp.dumpScreen.existing.title"), Text.translatable("brrp.dumpScreen.existing.message", size > 2000 ? Text.translatable("brrp.dumpScreen.existing.size", 2000) : size, dumpPath.toAbsolutePath().toString())));
          return;
        }
      }
    } catch (IOException e) {
      LOGGER.error("Checking whether the dump file exists:", e);
    }
    dumpThread = new Thread(() -> {
      interruptButton.active = true;
      backButton.active = false;
      dumpButton.setMessage(Text.translatable("brrp.dumpScreen.dumping"));
      Arrays.fill(dumpStat, 0);
      pack.dumpInPath(dumpPath, dumpType.resourceType, dumpStat);
      dumpButton.setMessage(Text.translatable("brrp.dumpScreen.dump"));
      interruptButton.active = false;
      backButton.active = true;
    }, "Dump pack");
    dumpThread.start();
  }

  @Override
  public void close() {
    if (dumpThread != null && dumpThread.isAlive()) return;
    if (client != null) {
      client.setScreen(parent);
    }
  }

  @Environment(EnvType.CLIENT)
  public enum DumpType implements StringIdentifiable {
    ALL("all", null),
    CLIENT_RESOURCES("client_resources", ResourceType.CLIENT_RESOURCES),
    SERVER_DATA("server_data", ResourceType.SERVER_DATA);

    public final ResourceType resourceType;
    private final String name;

    DumpType(String name, ResourceType resourceType) {
      this.name = name;
      this.resourceType = resourceType;
    }

    @Override
    public String asString() {
      return name;
    }
  }
}
