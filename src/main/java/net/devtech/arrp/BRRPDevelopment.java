package net.devtech.arrp;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.BlockStatesDefinition;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.VariantDefinition;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.models.JModel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BRRPDevelopment implements ModInitializer {
  public static final Block LAVA_BLOCK = Registry.register(Registry.BLOCK, new Identifier("brrp", "lava_block"), new Block(FabricBlockSettings.of(Material.LAVA).luminance(15)));
  public static final BlockItem LAVA_BLOCK_ITEM = Registry.register(Registry.ITEM, new Identifier("brrp", "lava_block"), new BlockItem(LAVA_BLOCK, new FabricItemSettings().group(ItemGroup.TRANSPORTATION)));
  public static final RuntimeResourcePack PACK = RuntimeResourcePack.create("brrp");

  @Override
  public void onInitialize() {
    PACK.addBlockState(
        BlockStatesDefinition.variants(new VariantDefinition().addVariant("", new JBlockModel(new Identifier("brrp", "block/lava_block")))),
        new Identifier("brrp", "lava_block"));
    PACK.addModel(new JModel("block/cube_all").addTexture("all", "block/lava_still"), new Identifier("brrp", "block/lava_block"));
    PACK.addModel(new JModel("brrp:block/lava_block"), new Identifier("brrp", "item/lava_block"));
    PACK.addLang(new Identifier("brrp", "en_us"), new JLang().block(new Identifier("brrp", "lava_block"), "Lava Block"));
    RRPCallback.AFTER_VANILLA.register(a -> a.add(PACK));
  }
}
