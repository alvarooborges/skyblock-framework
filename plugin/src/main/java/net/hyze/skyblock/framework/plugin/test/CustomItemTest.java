package net.hyze.skyblock.framework.plugin.test;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.World;

public class CustomItemTest extends EntityItem {


  protected double centerX, centerY, centerZ;

  protected double radius = 1.0;

  public CustomItemTest(World world, double centerX, double centerY, double centerZ,
      ItemStack itemstack) {
    super(world, centerX, centerY, centerZ, itemstack);

    this.centerX = centerX;
    this.centerY = centerY;
    this.centerZ = centerZ;
  }

  @Override
  public void t_() {
    if (this.getItemStack() == null) {
      this.die();
    } else {
      this.K();

      this.lastTick = MinecraftServer.currentTick;

      double theta = (this.ticksLived % 40) / 40 * Math.PI * 2;
      this.setPosition(Math.sin(theta) * radius + this.centerX, this.centerY, Math.cos(theta) * radius + this.centerZ);
    }
  }

  /*

   */

  @Override
  public void inactiveTick() {
  }

  @Override
  public void w() {
  }

  @Override
  public boolean a(EntityItem entityitem) {
    return false;
  }

  @Override
  protected void burn(int i) {
  }

  @Override
  public boolean damageEntity(DamageSource damagesource, float f) {
    return false;
  }

  @Override
  public void b(NBTTagCompound nbttagcompound) {
  }

  @Override
  public void a(NBTTagCompound nbttagcompound) {
  }

  @Override
  public void d(EntityHuman entityhuman) {
  }
}
