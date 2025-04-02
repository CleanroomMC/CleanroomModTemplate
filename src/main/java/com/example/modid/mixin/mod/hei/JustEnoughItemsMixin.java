package com.example.modid.mixin.mod.hei;

import com.example.modid.ExampleMod;
import mezz.jei.JustEnoughItems;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JustEnoughItems.class, remap = false)
public class JustEnoughItemsMixin {
    @Inject(method = "preInit", at = @At("HEAD"))
    public void print(FMLPreInitializationEvent event, CallbackInfo ci) {
        ExampleMod.LOGGER.info("Hello world");
    }
}
