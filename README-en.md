BRRP (Better Runtime Resource Pack), is a library mod base on [ARRP](https://github.com/Devan-Kerman/ARRP) with some enhancements. This mod provides all features of ARRP, fixes some bugs that exist in ARRP, and provides some new features.

## What is a runtime resource pack?

Runtime resource pack (RRP) refers to asset and data files generated when Minecraft is running, in avoidance of some repetitive works. For mods with masses of similar blocks and items, runtime resource pack helps to significantly reduce the size of mod file.

For instance, the loot table of most blocks can be described as "drop the block itself". Some blocks drop only when with Silk Touch. For slab blocks, when double slab block is broken, two blocks are dropped instead of one. In loot tables, each block requires a loot table JSON file; creating them is quite time-consuming. Moreover, almost each block requires a "block states", block model and item model (block models may be more than one). If loot tables and recipes (if it exists) are counted, each block requires at least 5 JSONs to achieve full features. In contrast, runtime resource packs make them generated inside the game instance, instead of stored in the mod JAR file.

Runtime resource packs have no difference than regular resource packs with regard to features. Normal resource packs (including the mod's builtin resource and manually-installed resource-packs or data-packs) can override contents of runtime resource packs, or have relations to runtime resource packs. A typical example is, block models are defined in runtime resource packs, and their texture files are stored in mod files as usual (as it's not appropriate to generate texture files in runtime).

Compared to traditional resource packs, runtime resource packs reduce I/Os, but adds the process of object serialization, which is a drawback of runtime resource pack. When resource packs are generated, objects are converted to byte forms (usually JSON). This process is called *serialization*. When game instance reads these resource packs, these byte-form contents are analysed to generate objects in game. This process is called *deserialization*. In traditional resource packs, all resources are serialized, and the game instance needs only to deserialize them when loading resources. However, runtime resource packs require the process of both serialization and deserialization.

To summarize again the process traditional resource packs and runtime resource packs are read in game:

- *Traditional* File → (read as) byte form → (deserialized as) in-game objects
- *Runtime* Code → ARRP objects → (serialized as) byte form → (deserialized as) in-game objects

It's considered to directly use objects generated in game in future versions, without the process of deserialization and serialization. This is already possible in theory, but to be in the form of resource pack and allow overriding by traditional resource packs and data packs, some further research is required.

## About this mod

This mod (BRRP) can be seen as an extension to ARRP, supporting all features of ARRP. If you installed BRRP mod, and installed mods that depend on ARRP (not BRRP), the game can launch. Therefore, please *do not* install BRRP and ARRP simultaneously. But if some mods nest ARRP in their mod JARs, some issues may happen, so it's not recommended to nest this mod into your mod JAR.

This mod tries to reduce incompatible changes to ARRP, so no classes, fields or methods are deleted; they are just deprecated. If some mods apply mixins on ARRP, the mod may have some issues when run on BRRP, but the probability of this has been as reduced as possible.

As this mod is still under development, the way of making your mod depend on this mod is not provided, and nor is the download. However, source code is provided and published under the MPL license.

Relations of this mod and ARRP:

| Other mods installed: | only depends ARRP     | depends BRRP        |
|-----------------------|-----------------------|---------------------|
| only installed ARRP   | can run normally      | cannot run normally |
| only installed BRRP   | usually runs normally | can run normally    |

## How to register your runtime resource pack

Runtime resource packs, after created and written, take effect only after registration. Registration is as follows:

```java
public class MyClass implements ModInitializer {
  public static final RuntimeResourcePack pack = RuntimeResourePack.create("my_pack");

  public void onInitialize() {
    pack.writeXXX('...');
    RRPCallback.BEFORE_VANILLA.register(a -> a.add(PACK));
  }
}
```

You can generate resource and register your resource pack at any time. In the example above, the registration takes place at the `main` entrypoint at the end of initialization of Minecraft. You can also generate resources at `preLaunch` or `rrp:pregen` (implement `RRPPreGenEntryPoint`), but in this case you cannot use some contents of Minecraft, such as game registries.

Apart from adding normal resource, you can also add async resource, allowing adding required contents in `rrp:pregen`. In `rrp:pregen`, resource packs are generated in the form of multiple-thread.

## Runtime resource packs and data generation

Minecraft has vanilla data generation features, which are extended by Fabric API. BRRP is trying to bridge ARRP and Minecraft's vanilla classes.

In BRRP, some ARRP objects can directly use their corresponding objects of Minecraft. For example, in vanilla `BlockStateModelGenerated`, there are many methods to directly create "block states" objects (these methods had been private, but are access-widened by Fabric Data Generation API). And then `JBlockStates.delegate` can be directly used, as the following code snippet:

```
JBlockStates.delegate(BlockStateModelGenerator.createStairsBlockState(
    (Block) this, 
    blockModelId.brrp_append("_inner"), 
    blockModelId, 
    blockModelId.brrp_append("_outer")))
```

In this way, when JSONs are generated, Minecraft vanilla JSON generations are used.

Another example of BRRP bridging ARRP and Minecraft's vanilla class is, in loot tables, places related to value provides, the `JRoll` in ARRP is not recommended anymore, and Minecraft's vanilla `LootNumberProvider` is used instead, which can be directly serialized as JSON formats you need.

## Hot-swapping

Hot-swap is usually not supported when resource packs are created, as its contents are well-written before registration. If you'd like to enable hot-swap for your runtime resource packs, you can do the followings:

```java
public class MyClass implements ModInitializer {
  public static final RuntimeResourcePack pack = RuntimeResourePack.create("my_pack");

  public void onInitialize() {
    RRPCallback.BEFORE_VANILLA.register(a -> {
      pack.clearResources();
      pack.addXXX('...');
    });
  }
}
```

In this way, each time Minecraft loads resources, the runtime resource packs are re-generated. But please do *not* do this when publishing your mods, as it causes resources re-generated each time you reload resource packs (including game initialization and F3+T hotkey) or data packs (including entering word or running `/reload`), which is obvious unnecessary. Therefore, when publishing your mod, it's advised to generate all resources before registering the pack.