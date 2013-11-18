package me.jezzadabomb.es2;

import java.io.File;

import me.jezzadabomb.es2.client.creativetab.ES2Tab;
import me.jezzadabomb.es2.common.ModBlocks;
import me.jezzadabomb.es2.common.ModItems;
import me.jezzadabomb.es2.common.lib.Reference;
import me.jezzadabomb.es2.common.lib.handlers.ConfigHandler;
import me.jezzadabomb.es2.common.lib.handlers.PacketHandler;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
@NetworkMod(channels = { Reference.CHANNEL_NAME }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class ElementalSciences2 {

    @Instance(Reference.MOD_ID)
    public static ElementalSciences2 instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;
    
    public static CreativeTabs creativeTab = new ES2Tab(CreativeTabs.getNextID(), Reference.MOD_ID);
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(event.getModConfigurationDirectory().getAbsolutePath() + File.separator + Reference.CHANNEL_NAME.toLowerCase() + File.separator);
        ModBlocks.init();
        ModItems.init();
        proxy.runClientSide();
    }

    
    @EventHandler
    public void load(FMLInitializationEvent event) {
        proxy.registerTileEntities();
    }

    @EventHandler
    public void postLoaded(FMLPostInitializationEvent event) {
        
    }
    
    @EventHandler
    public void onServerStart(FMLServerStartingEvent event){
        proxy.registerTickHandlers();
    }
    
}