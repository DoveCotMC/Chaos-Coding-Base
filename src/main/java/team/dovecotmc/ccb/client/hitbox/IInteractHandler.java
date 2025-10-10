package team.dovecotmc.ccb.client.hitbox;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

public interface IInteractHandler {
    InteractionResult interact(InteractionType type, Entity entity, Level level, Vector3d pos);

    enum InteractionType {
        ATTACK,
        INTERACT
    }
}
