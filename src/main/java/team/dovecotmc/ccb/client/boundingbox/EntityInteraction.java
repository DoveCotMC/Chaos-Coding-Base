package team.dovecotmc.ccb.client.boundingbox;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import team.dovecotmc.ccb.common.entities.CCBEntities;

public class EntityInteraction extends Entity {
    public EntityInteraction(EntityType<?> entityType, Level level) {
        super(CCBEntities.INTERACTION, level);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec3, InteractionHand interactionHand) {
        System.out.println("Interact At:");
        System.out.println(player);
        System.out.println(interactionHand);
        return super.interactAt(player, vec3, interactionHand);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        System.out.println("Interact:");
        System.out.println(player);
        System.out.println(interactionHand);
        return super.interact(player, interactionHand);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
