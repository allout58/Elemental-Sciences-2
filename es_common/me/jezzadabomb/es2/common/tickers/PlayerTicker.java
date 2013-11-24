package me.jezzadabomb.es2.common.tickers;

import java.util.EnumSet;

import me.jezzadabomb.es2.common.ModItems;
import me.jezzadabomb.es2.common.hud.InventoryInstance;
import me.jezzadabomb.es2.common.hud.StoredQueues;
import me.jezzadabomb.es2.common.packets.InventoryRequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class PlayerTicker implements ITickHandler {
    
    private int ticked = 0;
    private int dis = 8;
    private int oldX, oldY, oldZ, notMoveTick;

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        //long timing = System.nanoTime();
        
        int playerX = (int) Math.round(player.posX);
        int playerY = (int) Math.round(player.posY);
        int playerZ = (int) Math.round(player.posZ);
        World world = player.worldObj;
        
        if (player.getCurrentItemOrArmor(4) != null && player.getCurrentItemOrArmor(4).getItem() == ModItems.glasses) {
            if(playerMoved(playerX, playerY, playerZ) || notMoveTick == 10){
                notMoveTick = 0;
                for (int x = -dis; x < dis; x++) {
                    for (int y = -dis; y < dis; y++) {
                        for (int z = -dis; z < dis; z++) {
                            if (!world.isAirBlock(playerX + x, playerY + y, playerZ + z)) {
                                if (world.blockHasTileEntity(playerX + x, playerY + y, playerZ + z)) {
                                    TileEntity tileEntity = world.getBlockTileEntity(playerX + x, playerY + y, playerZ + z);
                                    if (tileEntity instanceof IInventory) {
                                        StoredQueues.instance().putTempInventory(new InventoryInstance(((IInventory) tileEntity).getInvName(), tileEntity, playerX + x, playerY + y, playerZ + z));
                                        if (!StoredQueues.instance().isAlreadyInQueue(new InventoryInstance(((IInventory) tileEntity).getInvName(), tileEntity, playerX + x, playerY + y, playerZ + z))) {
                                            if (StoredQueues.instance().isAtXYZ(playerX + x, playerY + y, playerZ + z)) {
                                                System.out.println(StoredQueues.instance().getPlayer().size());
                                                StoredQueues.instance().replaceAtXYZ(x, y, z, new InventoryInstance(((IInventory) tileEntity).getInvName(), tileEntity, playerX + x, playerY + y, playerZ + z));
                                            } else {
                                                StoredQueues.instance().putInventory(((IInventory) tileEntity).getInvName(), tileEntity, playerX + x, playerY + y, playerZ + z);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                this.oldX = playerX;
                this.oldY = playerY;
                this.oldZ = playerZ;
                
                //Removes all old inventories that it couldn't detect.
                StoredQueues.instance().retainInventories(StoredQueues.instance().getTempInv());
                for(InventoryInstance i : StoredQueues.instance().getPlayer()){
                    System.out.println("Player: " + i);
                }
                for(InventoryInstance i : StoredQueues.instance().getRequestList()){
                    System.out.println("Requested: " + i);
                }
                for(InventoryInstance i : StoredQueues.instance().getTempInv()){
                    System.out.println("Temp: " + i);
                }
                
                StoredQueues.instance().removeTemp();
                
                for(InventoryInstance i : StoredQueues.instance().getRequestList()){
                    System.out.println("2Requested: " + i);
                }
                for(InventoryInstance i : StoredQueues.instance().getTempInv()){
                    System.out.println("2Temp: " + i);
                }
                //Stores temp list in request list.
                StoredQueues.instance().setLists();
                for(InventoryInstance i : StoredQueues.instance().getRequestList()){
                    System.out.println("3Requested: " + i);
                }
                for(InventoryInstance i : StoredQueues.instance().getTempInv()){
                    System.out.println("3Temp: " + i);
                }
                //Requests requestsList of packets.
//                requestPackets(player);
                //Clears the temp list.
                StoredQueues.instance().clearTempInv();
                System.out.println("Cleared Temp Inv");
                
                if(ticked > 2){
                    Minecraft.getMinecraft().shutdown();
                }else{
                    ticked++;
                }
            }else{
                notMoveTick++;
            }
            
        }else{
            StoredQueues.instance().getPlayer().clear();
        }
//        for(InventoryInstance i : StoredQueues.instance().getPlayer()){
//            System.out.println(i);
//        }
        //System.out.println(StoredQueues.instance().getPlayer().size());
        //System.out.println(System.nanoTime() - timing);
    }

    public void requestPackets(EntityPlayer player){
        for(InventoryInstance i : StoredQueues.instance().getRequestList()){
            System.out.println("2Requested: " + i);
            //PacketDispatcher.sendPacketToServer(new InventoryRequestPacket(i).makePacket());
        }
    }
    
    public boolean playerMoved(int x, int y, int z) {
        return (oldX != x || oldY != y || oldZ != z);
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public String getLabel() {
        return "ES2-GlassesTicker";
    }

}