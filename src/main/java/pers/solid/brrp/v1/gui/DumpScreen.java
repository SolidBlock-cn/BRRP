package pers.solid.brrp.v1.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
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
  private TextWidget dumpPathText;
  private TextFieldWidget dumpPathTextField;
  private Path dumpPath;
  private String dumpPathString;
  private InvalidPathException invalidPathException;
  private MultilineText dumpPathPreviewText;
  private TextWidget summaryText;
  private TextWidget dumpProgressText;
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
        .tooltip(value -> Tooltip.of(Text.translatable("brrp.dumpScreen.dumpType.tooltip." + value.asString())))
        .build(width / 2 - 100, 30, 200, 20, Text.translatable("brrp.dumpScreen.dumpType"), (button, value) -> dumpType = value);
    addDrawableChild(dumpTypeButton);
    dumpPathText = new TextWidget(width / 2 - 100, 65, 200, 20, Text.translatable("brrp.dumpScreen.dumpPath.title"), textRenderer).setTextColor(0xcccccc);
    addDrawableChild(dumpPathText);
    dumpPathTextField = new TextFieldWidget(textRenderer, 20, 85, width - 40, 20, Text.translatable("brrp.dumpScreen.dumpPath.message"));
    dumpPathTextField.setTooltip(Tooltip.of(Text.translatable("brrp.dumpScreen.dumpPath.tooltip")));
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
      dumpPathTextField.setTooltip(Tooltip.of(Text.translatable("brrp.dumpScreen.dumpPath.tooltip"), text.copy().append(ScreenTexts.LINE_BREAK).append(Text.translatable("brrp.dumpScreen.dumpPath.tooltip"))));
    });
    addDrawableChild(dumpPathTextField);

    dumpPathTextField.setText(dumpPathString);


    summaryText = new TextWidget(20, height - 89, width - 40, 20, Text.translatable("brrp.configScreen.summary",
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.rootResources.", pack.numberOfRootResources()),
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.clientResources.", pack.numberOfClientResources()),
        RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.serverData.", pack.numberOfServerData())), textRenderer).setTextColor(0xffcccccc);
    addDrawableChild(summaryText);
    dumpProgressText = new TextWidget(20, height - 73, width - 40, 20, ScreenTexts.EMPTY, textRenderer);
    addDrawableChild(dumpProgressText);
    dumpButton = ButtonWidget.builder(Text.translatable("brrp.dumpScreen.dump"), button -> runDump()).dimensions(width / 2 - 200, height - 53, 200, 20).build();
    addDrawableChild(dumpButton);
    interruptButton = ButtonWidget.builder(Text.translatable("brrp.dumpScreen.interrupt"), button -> {
      if (dumpThread != null) {
        dumpThread.interrupt();
        try {
          dumpThread.join();
        } catch (InterruptedException e) {
          LOGGER.error("Interrupted dump screen:", e);
        }
      }
    }).tooltip(Tooltip.of(Text.translatable("brrp.dumpScreen.interrupt.tooltip"))).dimensions(width / 2, height - 53, 200, 20).build();
    interruptButton.active = false;
    addDrawableChild(interruptButton);
    addDrawableChild(backButton = ButtonWidget.builder(ScreenTexts.BACK, button -> close()).dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build());
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    renderBackground(matrices);
    dumpPathPreviewText.drawCenterWithShadow(matrices, width / 2, 120, 16, 0xffffff);
    drawCenteredTextWithShadow(matrices, this.textRenderer, this.title.asOrderedText(), this.width / 2, 8, 0xFFFFFF);
    super.render(matrices, mouseX, mouseY, delta);
  }

  @Override
  public void tick() {
    super.tick();
    dumpButton.active = invalidPathException == null && (dumpThread == null || !dumpThread.isAlive());
    if (dumpThread != null && dumpThread.isAlive()) {
      dumpProgressText.setMessage(Text.translatable("brrp.dumpScreen.dumpSummary",
          RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.rootResources.", dumpStat[0]),
          RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.clientResources.", dumpStat[1]),
          RRPConfigScreen.PackListWidget.Entry.singleOrPlural("brrp.configScreen.summary.serverData.", dumpStat[2])));
    } else if (dumpThread != null) {
      dumpProgressText.setMessage(ScreenTexts.DONE);
    }
  }

  private void runDump() {
    runDump(false);
  }

  private void runDump(boolean ignoreExistingWarning) {
    try {
      if (!ignoreExistingWarning && PathUtils.isDirectory(dumpPath) && !PathUtils.isEmptyDirectory(dumpPath)) {
        final long size = Files.walk(dumpPath).limit(501).count();
        if (client != null) {
          dumpProgressText.setMessage(ScreenTexts.EMPTY);
          client.setScreen(new ConfirmScreen(value -> {
            if (client != null) {
              client.setScreen(this);
              if (value) {
                runDump(true);
              }
            }
          }, Text.translatable("brrp.dumpScreen.existing.title"), Text.translatable("brrp.dumpScreen.existing.message", size > 500 ? Text.translatable("brrp.dumpScreen.existing.size", 500) : size, dumpPath.toAbsolutePath().toString())));
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
