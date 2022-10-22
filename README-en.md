BRRP (Better Runtime Resource Pack), is a branch of [ARRP](https://github.com/Devan-Kerman/ARRP) mod. This mod provides all features of ARRP, fixes some bugs that exist in ARRP, and provides some new features.

Welcome to join Tencent QQ group **587928350** or KaiHeiLa channel invitation code **KlFS0n** to experience the latest update of this mod.

[阅读本文档的中文版。](README.md)

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

This mod is open-source and published under the MPLv2 license.

Relations of this mod and ARRP:

| Other mods installed: | only depends ARRP     | depends BRRP        |
|-----------------------|-----------------------|---------------------|
| only installed ARRP   | can run normally      | cannot run normally |
| only installed BRRP   | usually runs normally | can run normally    |

Currently, the following mods are compatible with this mod. You can install BRRP without having to install ARRP:

- Extended Block Shapes (1.4.0-1.18.1)
- Mishang Urban Construction (1.18.2-0.1.7)
- Minekea (1.18.2-2.4.0)
- Pannotias Parcels (1.18.2-1.1.0)

Besides, ctft mod may be incompatible with BRRP because it nests ARRP.

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

In BRRP, some ARRP objects can directly use their corresponding objects of vanilla Minecraft. For example, in vanilla `BlockStateModelGenerated`, there are many methods to directly create "block states" objects (these methods had been private, but are access-widened by Fabric Data Generation API). And then `JBlockStates.delegate` can be directly used, as the following code snippet:

```java
public class XXX /*extends ...*/ {
  @Override
  public BlockStates getBlockStates() {
    return JBlockStates.delegate(BlockStateModelGenerator.createStairsBlockState(
        this,
        blockModelId.brrp_append("_inner"),
        blockModelId,
        blockModelId.brrp_append("_outer")));
  }
}
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

      a.add(pack);
    });
  }
}
```

In this way, each time Minecraft loads resources, the runtime resource packs are re-generated. But please do *not* do this when publishing your mods, as it causes resources re-generated each time you reload resource packs (including game initialization and F3+T hotkey) or data packs (including entering word or running `/reload`), which is obvious unnecessary. Therefore, when publishing your mod, it's advised to generate all resources before registering the pack.

## What are assets and data

Usually, in Minecraft, "assets" include each file used by the client to render, display or play, including "block states", models, textures, language files and sounds. In Minecraft, packs storing these contents are called "resource packs". The "resource packs" mentioned here includes vanilla Minecraft's builtin resource pack, and the pack provided the Fabric Loader mod, not necessarily what's found in `resourcepacks` folder. Assets are only used in client side, not server side, but the server can ask the client to install the resource pack provided by the server when the player attempts to join. Language files are the only type of assets that are used by the server.

Data includes all files used by the server, including recipes, tags, and advancements. Data is loaded only when the server is created. Data packs store data that is used by the server. The "data packs" mentioned here includes vanilla Minecraft's builtin data pack, the data packs provided by Fabric Loader and mods, and the installable, customized data packs stored in `data` folder of server archives (including single-player worlds).

Let's summarize here: assets are provided by resource packs to use in the client. Language files are the sole type of assets used by the server. Data is provided by data packs and is used for server.

However, when it comes to modding, these concepts become messy. When modding, the "assets" and "data" mentioned before are all named "resources". In this case, "resource pack" refers to the `resources` folder in the mod file, including the client assets and server data mentioned above (You should have realized that, words "client" and "server" are prepended in avoidance of ambiguity). The "runtime resource pack" of this mod also includes client assets and server data.

What is generated by data generator of Minecraft and Fabric API, is not only data. It includes all client assets and server data.

Let's re-summarize here: when it comes to modding, "resources" and "data" have similar meanings, which include both client assets and server data. To avoid ambiguity, to correctly express what's used in one of the sides, words "client" and "server" are prepended.

## How to use this mod as your project's dependency

To use this mod as your project's dependency, and use this mod's API, you can at first download these two files: <code>**brrp-version-gameVersion.jar**</code> 和 <code>**brrp-version-gameVersion-sources.jar**</code>, stored in any place in your device (places inside or nearby your project folder are preferred, the two files should be in the same folder, and, if needed, add these files to `.gitignore`).

And write following content in `build.gradle`:

```groovy
repositories {
    // [...]

    // the string in "dir" is the folder that stores these files; relative directory OK.
    flatDir { dir "the folder storing the two .jar files" }
}


dependencies {
    // [...]

    // the "version" and "gameVersion" should be identical of that of the two files you downloaded. Please subscribe to updates of BRRP.
    // don't stupidly write literally the words "version" and "gameVersion"
    modImplementation("net.devtech:brrp:version-gameVersion")
}
```

Refresh the project, and check if this library is normally loaded. For example, in IntelliJ IDEA, you can double-click Shift, input `RuntimeResourcePack`. If you can find this class, and codes and comments are correctly loaded, you can conclude that the project is correctly loaded.

And then, in your `fabric.mod.json`, add:

```json lines
{
  // [...]
  "depends": {
    // [...]

    // "*" means "any version"; you can also specify versions, such as ">=0.6.0".
    // Don't leave these comment in the JSON
    "better_runtime_resource_pack": "*"
  }
}
```

**Note:** If your mod uses features that only exist in some versions (classes, methods or fields annotated `@ApiStatus.AvailableSince`), the version should also be specified in your JSON. For example, if some APIs used in your mod are annotated `@ApiStatus.AvailableSince("0.8.0")`, you're supposed to write `"better_runtime_resource_pack": ">=0.8.0"` in your `fabric.mod.json`, in prevention of some potential unexpected errors when user installed BRRP lower than 0.8.0.