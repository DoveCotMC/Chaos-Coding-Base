package team.dovecotmc.ccb.client.hitbox;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientBoundInteractionEntity extends Entity {
    public ClientBoundInteractionEntity(Level level) {
        super(EntityType.INTERACTION, level);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        System.out.println("Interact: " + player.getName());
        return super.interact(player, interactionHand);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec3, InteractionHand interactionHand) {
        System.out.println("InteractAt: " + player.getName());
        return super.interactAt(player, vec3, interactionHand);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        System.out.println("Attacked: " + damageSource);
        return super.hurt(damageSource, f);
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
}
