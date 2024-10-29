package net.hyze.skyblock.framework.plugin.misc.npc;

import java.util.Random;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;

public class CustomNPCStorage implements NPCDataStore {

    @Override
    public void clearData(NPC npc) {
    }

    @Override
    public int createUniqueNPCId(NPCRegistry npcr) {
        return new Random().nextInt();
    }

    @Override
    public void loadInto(NPCRegistry npcr) {
    }

    @Override
    public void saveToDisk() {
    }

    @Override
    public void saveToDiskImmediate() {
    }

    @Override
    public void store(NPC npc) {
    }

    @Override
    public void storeAll(NPCRegistry npcr) {
    }

}
