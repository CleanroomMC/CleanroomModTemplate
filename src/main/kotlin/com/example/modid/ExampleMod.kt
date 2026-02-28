package com.example.modid

import com.example.modid.proxy.IProxy
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
class ExampleMod {
    
    val LOGGER: Logger = LogManager.getLogger(Reference.MOD_NAME)
    @SidedProxy(modId = Reference.MOD_ID, clientSide = "com.example.modid.proxy.ClientProxy", serverSide = "com.example.modid.proxy.CommonProxy")
    var proxy: IProxy? = null
    /**
     * [
     * Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
    ](https://cleanroommc.com/wiki/forge-mod-development/event#overview) *
     */
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent?) {
        LOGGER.info("Hello From {}!", Reference.MOD_NAME)
    }
}