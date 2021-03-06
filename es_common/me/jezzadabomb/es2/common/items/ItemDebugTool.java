package me.jezzadabomb.es2.common.items;

import cpw.mods.fml.common.FMLCommonHandler;
import me.jezzadabomb.es2.client.ClientProxy;
import me.jezzadabomb.es2.common.packets.InventoryPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDebugTool extends ItemES{

    public ItemDebugTool(int id, String name) {
        super(id, name);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int sideHit, float hitVecX, float hitVecY, float hitVecZ) {
        if(FMLCommonHandler.instance().getEffectiveSide().isClient()){
            InventoryPacket packet = ClientProxy.hudRenderer.getPacket(x, y, z);
            if(packet != null){
                player.addChatMessage("Inventory Name: " + packet.inventoryTitle);
                player.addChatMessage("Contents: ");
                String tempString = packet.getItemStacksInfo();
                int lastIndex = 1;
                for(int i = 0; i < tempString.length(); i++){
                    if(tempString.charAt(i) == ','){
                        player.addChatMessage(tempString.substring(lastIndex, i));
                        lastIndex += i;
                    }
                }
            }
            return packet != null;
        }
        return false;
    }
    
}
