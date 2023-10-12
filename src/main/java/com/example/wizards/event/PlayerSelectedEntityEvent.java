package com.example.wizards.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

// TODO maybe should be a LivingEvent so that NPC casters can select targets
public class PlayerSelectedEntityEvent extends Event {

    private int selectedEntityId;
    private ServerPlayer player;

    public PlayerSelectedEntityEvent(int selectedEntityId, ServerPlayer player) {
        this.selectedEntityId = selectedEntityId;
        this.player = player;
    }

    public int getSelectedEntityId() {
        return selectedEntityId;
    }

    public void setSelectedEntityId(int selectedEntityId) {
        this.selectedEntityId = selectedEntityId;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public void setPlayer(ServerPlayer player) {
        this.player = player;
    }
}
