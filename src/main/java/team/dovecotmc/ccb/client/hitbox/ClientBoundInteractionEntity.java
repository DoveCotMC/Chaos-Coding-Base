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
import org.joml.Vector3d;

public class ClientBoundInteractionEntity extends Entity {
    private final InteractObject object;

    public ClientBoundInteractionEntity(Level level, InteractObject object) {
        super(EntityType.INTERACTION, level);
        this.object = object;
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec3, InteractionHand interactionHand) {
        return object.getHandler().interact(IInteractHandler.InteractionType.INTERACT, player, player.level(), new Vector3d(vec3.toVector3f()));
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        return object.getHandler().interact(IInteractHandler.InteractionType.ATTACK, damageSource.getEntity(), damageSource.getEntity().level(), new Vector3d(damageSource.getSourcePosition().toVector3f())) == InteractionResult.SUCCESS;
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
