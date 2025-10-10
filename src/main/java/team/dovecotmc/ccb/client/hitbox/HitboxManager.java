package team.dovecotmc.ccb.client.hitbox;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import team.dovecotmc.ccb.entries.ChaosBase;

import java.util.ArrayList;
import java.util.List;

public class HitboxManager {
    private static final List<InteractObject> INTERACTION_OBJECTS = new ArrayList<>();

    public static void pick(Player player) {
        if (ChaosBase.loadDevelopmentContent) {
            Obb testObb = new Obb(new Vector3d(0, 0, 0), new Vector3d(0.5, 0.5, 0.5));
            testObb.setRotation(new Quaterniond().rotationX(30));
            INTERACTION_OBJECTS.add(new InteractObject(testObb, (type, player1, level, pos) -> {
                ChaosBase.LOGGER.info("Interaction: {}", type);
                return InteractionResult.SUCCESS;
            }));
        }

        Vector3d pos = new Vector3d(player.getEyePosition().toVector3f());
        Vector3d sight = new Vector3d(player.getLookAngle().toVector3f());

        Vector3d hit = null;
        double distance = 0;
        InteractObject interactObject = null;
        for (InteractObject object : INTERACTION_OBJECTS) {
            Vector3d hitPos = object.getBoundingBox().rayIntersect(pos, sight);
            if (hitPos != null && object.getBoundingBox().contains(hitPos)) {
                distance = 0;
                hit = pos;
                interactObject = object;
            } else if (hitPos != null) {
                hit = hitPos;
                distance = distanceTo(hitPos, player);
                interactObject = object;
                break;
            }
        }

        if (Minecraft.getInstance().hitResult != null && hit != null && Minecraft.getInstance().hitResult.distanceTo(player) > distance) {
            Minecraft.getInstance().hitResult = new CCBHitResult(hit, interactObject);
        }

        INTERACTION_OBJECTS.clear();
    }

    private static double distanceTo(Vector3d location, Entity entity) {
        double d = location.x - entity.getX();
        double e = location.y - entity.getY();
        double f = location.z - entity.getZ();
        return d * d + e * e + f * f;
    }
}
