package team.dovecotmc.ccb.client.boundingbox;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ObbHitResult extends EntityHitResult {
    public ObbHitResult(Entity entity) {
        super(entity);
    }

    public ObbHitResult(Entity entity, Vec3 vec3) {
        super(entity, vec3);
    }

    public interface Handler {
        InteractionResult handle(Player player, Level level, Vec3 pos);
    }
}
