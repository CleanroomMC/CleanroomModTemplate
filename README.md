# CleanroomModTemplate
Mod development template for Cleanroom, uses Unimined

## Usage
### Choose Branch
Choose mixin branch if you want to use Mixin.

Use scala and kotlin branch if you want to write in non-Java language. 

There are 4 branches available:
- main
- mixin
- scala
- kotlin

If you want to use non-main branches, after clicked *Create a new repository* under *Use this template*, check the *Include all branches* checkbox.

### gradle.properties
Edit gradle.properties and set your modid, mod version, mod name, package, etc.

If you are writing a coremod, remember to set related settings to true.

### Reference Class
Mostly used to store mod version so you can fill it to `@Mod`.

You should change its location to fit your new package name.

You can find it under `src/main/java-templates`.

### Dependencies
You can find dependency script in `gradle/scripts/dependencies.gradle`.

No more `rfg.deobf()` or `fg.deobf` for mods, you should use `modImplementation`, `modCompileOnly` and `modRuntimeOnly`.

### Shadow
You can use `shadow` in dependency declaration to shadow libraries.

### Contain
You can use `contain` in dependency declaration to add non-mod libraries to artifact jar.

They will be extracted and loaded automatically in production.

### Mixin
MixinBooter API is deprecated in Cleanroom.

Current approach is to set json configs in `MixinConfigs` manifest key, which will be read by Cleanroom.

You don't need to set tons of json too. If you aren't mixining into Forge, all you need is two jsons for `DEFAULT` hase and `MOD` Phase.

All you need is to put `IEarlyMixinLoader` mixin to `DEFAULT` json, and, mixins from `ILateMixinLoader` to `MOD` json.

As for calling `Loader.isModLoaded()`, just fit an `IMixinConfigPlugin` to your json and call in `shouldApply()`

There are some example mixins and a HEI dependency in mixin fork, remove them before writing yours.

### Access Transformer
You MUST write AT file in MCP name. It will be remapped back to SRG name in artifact jar.

Rename AT file name to your modid before using it. There's an example entry in AT file, remove it if you want to use AT. 

**WARNING**: ATs from dependency won't be applied to vanilla source. 

### Source Code with Comments
Run `genSources` task in gradle.

If you want to `find usage` from vanilla like RFG, just change the scope in IntelliJ settings.

### Running Client or Server
You **MUST** add mods by using `modImplementation` or `modRuntimeOnly`, or mapping and ATs will break.

If you are using IntelliJ, **DO NOT** use the `Minecraft Client` configure with a blue icon. Just use the `runClient` Gradle task.

### GitHub Action
This template comes with three workflows.

`build.yml` will build and upload artifact for every commit. Useful when you want to provide test builds for debugging.

`release.yml` will make a GitHub release if you pushed a git tag.

`release-to-cf-mr.yml` can publish your mod to CurseForge and/or Modrinth.

You need to fill in your project IDs and configure your tokens in GitHub repository first.

By default, you will need to manually trigger the workflow in web page, but you can also enable tag triggering by merging it into `release.yml`.

### Credit
Thanks @Karnatour for fixing shadow plugin
Thanks @ghostflyby for making kotlin branch