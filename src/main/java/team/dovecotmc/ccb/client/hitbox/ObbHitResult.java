package team.dovecotmc.ccb.client.hitbox;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class ObbHitResult extends EntityHitResult {
    private final Obb boundingBox;

    public ObbHitResult(Vector3d pos, Obb boundingBox) {
        super(new ClientBoundInteractionEntity(Minecraft.getInstance().level), new Vec3(pos.x(), pos.y(), pos.z()));
        this.boundingBox = boundingBox;
    }

    public Obb getBoundingBox() {
        return boundingBox;
    }
}
