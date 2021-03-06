package me.jezzadabomb.es2.client.renderers;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

import me.jezzadabomb.es2.client.utils.RenderUtils;
import me.jezzadabomb.es2.common.core.utils.UtilHelpers;
import me.jezzadabomb.es2.common.hud.StoredQueues;
import me.jezzadabomb.es2.common.lib.TextureMaps;
import me.jezzadabomb.es2.common.packets.InventoryPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HUDRenderer {
    private ArrayList<InventoryPacket> packetList = new ArrayList<InventoryPacket>();
    private ArrayList<InventoryPacket> removeList = new ArrayList<InventoryPacket>();
    private final RenderItem customItemRenderer;

    public HUDRenderer() {
        customItemRenderer = new RenderItem() {
            @Override
            public boolean shouldBob() {
                return false;
            }

            @Override
            public boolean shouldSpreadItems() {
                return false;
            }
        };
        customItemRenderer.setRenderManager(RenderManager.instance);
    }

    public InventoryPacket getPacket(int x, int y, int z) {
        for (InventoryPacket packet : packetList) {
            if (packet.x == x && packet.y == y && packet.z == z) {
                return packet;
            }
        }
        return null;
    }

    public void printPacketList() {
        for (InventoryPacket packet : packetList) {
            System.out.println(packet);
        }
    }

    public void addPacketToList(InventoryPacket p) {
        if (!packetList.contains(p)) {
            if (doesPacketAlreadyExistAtXYZ(p)) {
                packetList.set(getPosInList(p), p);
            } else {
                packetList.add(p);
            }
        }
    }

    private boolean doesPacketAlreadyExistAtXYZ(InventoryPacket p) {
        for (InventoryPacket packet : packetList) {
            if (p.inventoryTitle.equals(packet.inventoryTitle) && p.x == packet.x && p.y == packet.y && p.z == packet.z) {
                return true;
            }
        }
        return false;
    }

    public int getPosInList(InventoryPacket p) {
        for (InventoryPacket packet : packetList) {
            if (p.inventoryTitle.equals(packet.inventoryTitle) && p.x == packet.x && p.y == packet.y && p.z == packet.z) {
                return packetList.indexOf(packet);
            }
        }
        return -1;
    }

    @ForgeSubscribe
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (packetList.isEmpty())
            return;

        for (InventoryPacket packet : packetList) {
            if (!StoredQueues.instance().getStrXYZ(packet.inventoryTitle, packet.x, packet.y, packet.z)) {
                removeList.add(packet);
            }
        }
        packetList.removeAll(removeList);

        for (InventoryPacket p : packetList) {
            if (UtilHelpers.canShowDebugHUD()) {
                RenderUtils.renderRedBox(event, p);
                RenderUtils.drawTextInAir(p.x, p.y + 0.5F, p.z, event.partialTicks, p.inventoryTitle);
            }
            renderInfoScreen2(p.x, p.y, p.z, event.partialTicks, p);
        }
    }

    private void renderInfoScreen2(double x, double y, double z, double partialTicks, InventoryPacket p) {
        if ((Minecraft.getMinecraft().renderViewEntity instanceof EntityPlayer)) {
            EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft().renderViewEntity;
            double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
            double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
            double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

            glPushMatrix();
            glDisable(GL_CULL_FACE);
            glDisable(GL_LIGHTING);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            glTranslated(-iPX + x + 0.5D, -iPY + y + 1.5D, -iPZ + z + 0.5D);
            glColor4f(1.0F, 1.0F, 1.0F, 0.6F);

            float xd = (float) (iPX - (x + 0.5D));
            float zd = (float) (iPZ - (z + 0.5D));
            float rotYaw = (float) (Math.atan2(xd, zd) * 180.0D / 3.141592653589793D);

            glRotatef(rotYaw + 180.0F, 0.0F, 1.0F, 0.0F);

            glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            glScalef(0.01F, 0.006F, 0.01F);
            glScalef(0.6F, 0.6F, 0.6F);

            int xTextureOffset = 11;
            int yTextureOffset = 18;
            int xInventoryPos = -80;
            int yInventoryPos = -90;
            if (!player.worldObj.isAirBlock(p.x, p.y + 1, p.z)) {
                yInventoryPos = 190;
            }

            // Inventory background
            RenderUtils.bindTexture(TextureMaps.HUD_INVENTORY);
            RenderUtils.drawTexturedQuad(xInventoryPos, yInventoryPos, 0, 0, 172, 250, 0);
            glTranslated(xInventoryPos + xTextureOffset, yInventoryPos + yTextureOffset, 0.0D);
            int xOffset = 52;
            int yOffset = 74;

            int indexNum = -1;
            int rowNum = 0;
            int totalSlots = 0;
            for (ItemStack itemStack : p.getItemStacks()) {
                if (totalSlots > 8) {
                    break;
                }
                if (indexNum < 2) {
                    indexNum++;
                } else {
                    indexNum = 0;
                    rowNum++;
                }
                RenderUtils.drawItemAndSlot(indexNum * xOffset, rowNum * yOffset, itemStack, customItemRenderer, -2);
                totalSlots++;
            }

            glPopMatrix();
        }
    }
}
