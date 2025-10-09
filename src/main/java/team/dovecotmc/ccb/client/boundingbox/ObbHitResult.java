package team.dovecotmc.ccb.client.boundingbox;

import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ObbHitResult extends HitResult {
    protected ObbHitResult(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public @NotNull Type getType() {
        return Type.ENTITY;
    }
}
