package com.example.modid

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, modLanguage = "scala") 
object ExampleMod {

  val LOGGER: Logger = LogManager.getLogger(Reference.MOD_NAME)
  /**
   * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
   * Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
   * </a>
   */
  @Mod.EventHandler 
  def preInit(event: FMLPreInitializationEvent): Unit = {
    ExampleMod.LOGGER.info("Hello From {}!", Reference.MOD_NAME)
  }
}
