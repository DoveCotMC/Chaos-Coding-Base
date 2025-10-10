package team.dovecotmc.ccb.client.hitbox;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class CCBHitResult extends EntityHitResult {
    private final InteractObject object;

    public CCBHitResult(Vector3d pos, InteractObject object) {
        super(new ClientBoundInteractionEntity(Minecraft.getInstance().level, object), new Vec3(pos.x(), pos.y(), pos.z()));
        this.object = object;
    }

    public Obb getBoundingBox() {
        return object.getBoundingBox();
    }

    public InteractObject getInteractionObject() {
        return object;
    }
}
