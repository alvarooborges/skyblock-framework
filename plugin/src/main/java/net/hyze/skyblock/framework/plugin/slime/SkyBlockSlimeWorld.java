package net.hyze.skyblock.framework.plugin.slime;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import net.hyze.core.shared.misc.utils.Printer;
import net.hyze.slime.world.SlimeWorld;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.World;

public class SkyBlockSlimeWorld extends SlimeWorld {

    private final Map<Class<? extends SlimeExtra>, SlimeExtra> extraMap = Maps.newHashMap();

    @Getter
    @Setter
    private long currentMillis;

    @Getter
    private boolean speeding;

    public SkyBlockSlimeWorld(SlimeWorld parent) {
        super(parent.getLoader(), parent.getWorldName(), parent.getChunks(), parent.getExtraData(), parent.isV1_13());

        for (Class<? extends SlimeExtra> extraClass : extraRegistry) {
            try {
                SlimeExtra extra = extraClass.getConstructor(SkyBlockSlimeWorld.class).newInstance(this);
                extraMap.put(extraClass, extra);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        deserializeExtra(this.getExtraData());
    }

    /*
     */
    private static final Set<Class<? extends SlimeExtra>> extraRegistry = Sets.newHashSet();

    public static void registerExtra(Class<? extends SlimeExtra> extraClass) {
        extraRegistry.add(extraClass);
    }

    public <T extends SlimeExtra> T getExtra(Class<T> extraClass) {
        return (T) extraMap.get(extraClass);
    }

    /*
     */
    @Override
    public void tick(World world) {
        super.tick(world);

        this.currentMillis += 50;

        long dayTick = getDayTick();
        world.setFullTime(dayTick);

        extraMap.values().forEach(SlimeExtra::tick);
    }

    public void speedup(World world, long finalMillis) {
        this.speeding = true;
        while (this.currentMillis < finalMillis) {
            tick(world);
        }
        this.speeding = false;
    }

    /*

     */

    public int getDayTick() {
        return getDayTick(this.currentMillis);
    }

    public int getMinute() {
        return getMinute(this.currentMillis);
    }

    public int getHour() {
        return getHour(this.currentMillis);
    }

    public int getDay() {
        return getDay(this.currentMillis);
    }

    public WorldSeason getSeason() {
        return getSeason(this.currentMillis);
    }

    public static Calendar getDate(long currentMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, getDay(currentMillis));
        calendar.set(Calendar.HOUR_OF_DAY, getHour(currentMillis));
        calendar.set(Calendar.MINUTE, getMinute(currentMillis));
        return calendar;
    }

    public static int getDayTick(long currentMillis) {
        double millisPerTick = 50;

        double minute = Math.floor(currentMillis / millisPerTick);
        return (int) (((long) (minute + 18000)) % 24000);
    }

    public static int getMinute(long currentMillis) {
        double millisPerMinute = 50 * 1000 / 60.0;

        int minute = (int) Math.floor(currentMillis / millisPerMinute);
        return minute % 60;
    }

    public static int getHour(long currentMillis) {
        double millisPerHour = 50 * 1000;

        int hour = (int) Math.floor(currentMillis / millisPerHour);
        return hour % 24;
    }

    public static int getDay(long currentMillis) {
        double millisPerDay = 50 * 24000;

        int day = (int) Math.floor(currentMillis / millisPerDay);
        return day % 30;
    }

    public static WorldSeason getSeason(long currentMillis) {
        long daysPerSeason = 90;
        double millisPerSeason = 50 * daysPerSeason * 24000;

        int season = (int) Math.floor(currentMillis / millisPerSeason);
        return WorldSeason.values()[season % WorldSeason.values().length];
    }

    /*

     */

    /*
     */
    @Override
    public void serializeExtra(NBTTagCompound compound) {
        super.serializeExtra(compound);

        extraMap.values().forEach(extra -> extra.serializeExtra(compound));
    }

    public void deserializeExtra(NBTTagCompound compound) {
        extraMap.values().forEach(extra -> extra.deserializeExtra(compound));
    }
}
