package net.hyze.skyblock.framework.plugin.slime;

import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

@RequiredArgsConstructor
public abstract class SlimeExtra {

    protected final SkyBlockSlimeWorld slimeWorld;

    /*

     */

    public void tick() {

    }

    public void speedup(long speedupMillis) {
    }

    public abstract void serializeExtra(NBTTagCompound compound);

    public abstract void deserializeExtra(NBTTagCompound compound);
}
