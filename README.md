# CleanroomModTemplate
Mod development template for Cleanroom, uses a custom [Unimined fork](https://github.com/kappa-maintainer/Unimined) ([original](https://github.com/unimined/Unimined))

### WARNING: Custom Unimined Fork
May have issues, report here or [here](https://github.com/kappa-maintainer/Unimined) when you encountered impossible field names or impossible Scala compiler errors. 

## DOs and DON'Ts
### Choose Branch
Choose mixin branch if you want to use Mixin.

Use scala and kotlin branch if you want to use those languages. 

There are 4 branches available:
- main
- mixin
- scala
- kotlin

If you want to use non-main branches, after clicked *Create a new repository* under *Use this template*, check the *Include all branches* checkbox.

### Running Client or Server
If you are using IntelliJ, **DO NOT** use the `Minecraft Client` configure with a blue icon. Just use the `2. Run Client` Gradle task.

### Adding Mod Dependencies
You can find dependencies block in `gradle/scripts/dependencies.gradle`.

No more `rfg.deobf()` or `fg.deobf`. You **MUST** add mods by using `modImplementation` or `modRuntimeOnly`, or the game will crash when running.

### Non-Mod Dependencies
Two new configuration types `contain` and `shadow` are available, check more details in `dependencies.gradle`.

### gradle.properties
Edit gradle.properties and set your modid, mod version, mod name, package, etc.

If you are writing a coremod, remember to set related settings to true.

### Reference Class
There will be a `Reference` class under your top package.

This is used to store mod version so you can fill it to `@Mod` annotation.

You should change its location to fit your new package name.

You can find its template under `src/main/java-templates`.

### Mixin
1. Rename json config file to include your modid. You will need one json per phase (`PRE_INIT`, `DEFAULT`, `MOD`) 
2. Add your mixin classes there.
3. Use `IMixinConfigPlugin` to control if certain mixin should be enabled. You can call `Loader.isModLoaded()` for `MOD` phase mixins.
4. Don't worry about refmap, Unimined will handle it automatically. You can still `disableRefmap()` manually though

### Access Transformer
You **MUST** write AT file in MCP name. It will be remapped back to SRG name in artifact jar.

Rename AT file name to your modid before using it. There's an example entry in AT file, remove it if you want to use AT.

### Vanilla Source Code with Comments
Run `genSources` task in gradle. If it didn't work, run again until a file with `-sources.jar` suffix appeared.

If you want to `find usage` from vanilla like RFG, just change the scope in IntelliJ settings.

### GitHub Action
This template comes with three workflows.

`build.yml` will build and upload artifact for every commit. Useful when you want to provide test builds for debugging.

`release.yml` will make a GitHub release if you pushed a git tag.

`release-to-cf-mr.yml` can publish your mod to CurseForge and/or Modrinth.

You need to fill in your project IDs and configure your tokens in GitHub repository first.

By default, you will need to manually trigger the workflow in web page, but you can also enable tag triggering by merging the third yml into `release.yml`.

### Credit
Thanks @Karnatour for fixing shadow plugin

Thanks @ghostflyby for making kotlin branch