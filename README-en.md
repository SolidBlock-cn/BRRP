# Better Runtime Resource Pack

[点击此处阅读本文档的中文版。](README.md)

BRRP (Better Runtime Resource Pack), is a library mod used for generate resources at runtime, which is a branch of [ARRP](https://github.com/Devan-Kerman/ARRP) (Advanced Runtime Resource Pack) mod.

Welcome to join Tencent QQ group **587928350** or KOOK (KaiHeiLa) channel invitation code **KlFS0n** to experience the latest update of this mod.

Notice: Since version 1.0.0, the ID of the mod was changed from `better_runtime_resource_pack` to `brrp_v1`, and modified the code without compatibility for older versions, but can co-exist with older versions. Unless needed, please do not used old version.

## What is a runtime resource pack?

Runtime resource pack (RRP) refers to asset and data files generated when Minecraft is running, in avoidance of some repetitive works. For mods with masses of similar blocks and items, runtime resource pack helps to significantly reduce the size of mod file.

For instance, the loot table of most blocks can be described as "drop the block itself". Some blocks drop only when with Silk Touch. For slab blocks, when double slab block is broken, two blocks are dropped instead of one. In loot tables, each block requires a loot table JSON file; creating them is quite time-consuming. Moreover, almost each block requires a "block states", block model and item model (block models may be more than one). If loot tables and recipes (if it exists) are counted, each block requires at least 5 JSONs to achieve full features. In contrast, runtime resource packs make them generated inside the game instance, instead of stored in the mod JAR file.

Runtime resource packs have no difference than regular resource packs with regard to features. Normal resource packs (including the mod's builtin resource and manually-installed resource-packs or data-packs) can override contents of runtime resource packs, or have relations to runtime resource packs. A typical example is, block models are defined in runtime resource packs, and their texture files are stored in mod files as usual (as it's not appropriate to generate texture files in runtime).

Compared to traditional resource packs, runtime resource packs reduce I/Os, but adds the process of object serialization, which is a drawback of runtime resource pack. When resource packs are generated, objects are converted to byte forms (usually JSON). This process is called *serialization*. When game instance reads these resource packs, these byte-form contents are analysed to generate objects in game. This process is called *deserialization*. In traditional resource packs, all resources are serialized, and the game instance needs only to deserialize them when loading resources. However, runtime resource packs require the process of both serialization and deserialization.

To summarize again the process traditional resource packs and runtime resource packs are read in game:

- *Traditional* File → (read as) byte form → (deserialized as) in-game objects
- *Runtime* Code → ARRP objects → (serialized as) byte form → (deserialized as) in-game objects

It's been in my plan to make it possible to directly use objects generated in game in future versions, without the process of deserialization and serialization. This is already possible in theory, but to be in the form of resource pack and allow overriding by traditional resource packs and data packs, some further research is required.

## About this mod

This mod (BRRP) is a branch of ARRP. Since 1.0.0, this mod has become independent of ARRP, no longer provides features of ARRP, but can co-exist with ARRP mod.

This mod is open-source and published under the MPLv2 license.

## How to register your runtime resource pack

Runtime resource packs, after created and written, take effect only after registration. Registration is as follows:

### Fabric:

```java
public class MyClass implements ModInitializer {
  public static final RuntimeResourcePack pack = RuntimeResourePack.create(new Identifier("my_mod", "my_pack"));

  @Override
  public void onInitialize() {
    // you may invoke 'write' methods for 'pack' here to write something into it.

    RRPCallback.BEFORE_VANILLA.register(resources -> resources.add(pack));
  }
}
```

### Forge

For Forge versions, you may use `RRPEvent` to register resource packs on your mod's event bus, as following:

```java

@Mod("my_mod_id")
public class MyClass {
  public static final RuntimeResourcePack pack = RuntimeResourePack.create(new Identifier("my_mod", "my_pack"));

  public MyClass() {
    // you may invoke 'write' methods for 'pack' here to write something into it.

    FMLJavaModLoadingContext.get().getModEventBus().addListener((RRPEvent.BeforeVanilla event) -> event.addPack(pack));
  }
}
```

### Supporting both Forge and Fabric

The mod supports a `RRPEventHelper` that supports both Forge and Fabric. For example:

```java
public class MyClass implements ModInitializer {
  public static final RuntimeResourcePack pack = RuntimeResourePack.create(new Identifier("my_mod", "my_pack"));

  @Override
  public void onInitialize() {
    // you may invoke 'write' methods for 'pack' here to write something into it.

    RRPEventHelper.BEFORE_VANILLA.registerPack(pack);
  }
}
```

## What are assets and data

Usually, in Minecraft, "assets" include each file used by the client to render, display or play, including "block states", models, textures, language files and sounds. In Minecraft, packs storing these contents are called "resource packs". The "resource packs" mentioned here includes vanilla Minecraft's builtin resource pack, and the pack provided the Fabric Loader mod, not necessarily what's found in `resourcepacks` folder. Assets are only used in client side, not server side, but the server can ask the client to install the resource pack provided by the server when the player attempts to join. Language files are the only type of assets that are used by the server.

Data includes all files used by the server, including recipes, tags, and advancements. Data is loaded only when the server is created. Data packs store data that is used by the server. The "data packs" mentioned here includes vanilla Minecraft's builtin data pack, the data packs provided by Fabric Loader and mods, and the installable, customized data packs stored in `data` folder of server archives (including single-player worlds).

Let's summarize here: assets are provided by resource packs to use in the client. Language files are the sole type of assets used by the server. Data is provided by data packs and is used for server.

However, when it comes to modding, these concepts become messy. When modding, the "assets" and "data" mentioned before are all named "resources". In this case, "resource pack" refers to the `resources` folder in the mod file, including the client assets and server data mentioned above (You should have realized that, words "client" and "server" are prepended in avoidance of ambiguity). The "runtime resource pack" of this mod also includes client assets and server data.

What is generated by data generator of Minecraft and Fabric API, is not only data. It includes all client assets and server data.

Let's re-summarize here: when it comes to modding, "resources" and "data" have similar meanings, which include both client assets and server data. To avoid ambiguity, to correctly express what's used in one of the sides, words "client" and "server" are prepended.

## How to use this mod as your project's dependency

To use this mod as your project's dependency, and use this mod's API, you have two ways:

### Method 1: By using the repository in GitHub

Add following content to the `repositories` and `dependencies` part of your `build.gradle` respectively.

```groovy
repositories {
    maven {
        url 'https://raw.githubusercontent.com/SolidBlock-cn/mvn-repo/main'

        // If the website above is beyond reach, you may try mirror websites, such as:
        // url 'https://raw.nuaa.cf/SolidBlock-cn/mvn-repo/main'
        // Note that there is no guarantee that the mirror website can be connected stably and contents are not mutated, and that the mirror website above is only an example.
    }
}

dependencies {
    modImplementation "pers.solid:brrp-fabric:<mod version>-<Minecraft version>"
    // Note: For Forge versions, replace the word `fabric` with `forge`.
    // For version 0.8.1 and above, please replace `pers.solid` with `net.devtech`. Old version is not recommended.
    // The difference between `modImplementation` and `modApi` is, when other projects depend on your project, if your project uses `modApi`, that project will also load what you depend on; if uses `modImplementation` then not. You can choose by yourself.
}
```

**Note:** Since 1.0.0, for Fabric versions, this mod has been depending on Mod Menu. Therefore, for Fabric, you may also do any of the following:
- Add following content to the `repositories` in the code above:
```groovy
    // The repository that Mod Menu depends on (for Fabric only)
    maven { url "https://maven.terraformersmc.com/releases/" }
```
- **or** change the content in `dependencies` into:
```groovy
    // Not depending on Mod Menu
    modImplementation("pers.solid:brrp-fabric:<mod-version>-<Minecraft version>") {
        transitive false
    }
```

### Method 2: Download files to local

You can at first download these two files: **`brrp-<mod version>-<Minecraft version>.jar`** and **`brrp-<mod version>-<Minecraft version>-sources.jar`**, which may be accessible in the "release" part of GitHub or in Modrinth. Save the two files in any place in your device (places inside or nearby your project folder are preferred, the two files should be in the same folder, and, if needed, you can add the downloaded files to `.gitignore`).

And then, add following content to the `repositories` and `dependencies` part of your `build.gradle` respectively.

```groovy
repositories {
    // the string in "dir" is the folder that stores these files; relative directory OK.
    flatDir { dir "the folder storing the two .jar files" }
}


dependencies {
    modImplementation("pers.solid:brrp-fabric:<mod version>-<minecraft_version>")
}
```
The note in the "Method 1" also applies to "Method 2".

### Check whether the configuration is finished

Refresh the project, and check if this library is normally loaded. For example, in IntelliJ IDEA, you can double-click Shift, input `RuntimeResourcePack`. If you can find this class, and codes and comments are correctly loaded, you can conclude that the project is correctly loaded.

And then, in the `depends` part of your `fabric.mod.json` of your Fabric project, add:

```json lines
{
  // [...]
  "depends": {
    // [...]

    // "*" means "any version"; you can also specify versions, such as ">=1.0.0".
    // Don't leave these comments in the JSON
    "brrp_v1": "*"
  }
}
```

Or add into `mods.toml` in your Forge project:

```toml
[[dependencies.'the id of your mod']]
modId = "brrp_v1"
mandatory = true
versionRange = "[1.0.0,)"
ordering = "NONE"
side = "BOTH"
```