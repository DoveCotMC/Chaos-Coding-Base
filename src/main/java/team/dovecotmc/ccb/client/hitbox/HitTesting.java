package team.dovecotmc.ccb.client.hitbox;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class HitTesting {
    private static final List<Obb> BOXES = new ArrayList<>();

    public static void pick(Player player) {
        // TODO: Debug
        Obb testObb = new Obb(new Vector3d(0, 0, 0), new Vector3d(0.5, 0.5, 0.5));
        testObb.setRotation(new Quaterniond().rotationX(30));
        BOXES.add(testObb);

        Vector3d pos = new Vector3d(player.getEyePosition().toVector3f());
        Vector3d sight = new Vector3d(player.getLookAngle().toVector3f());

        Vector3d hit = null;
        Obb box = null;
        for (Obb obb : BOXES) {
            Vector3d hitPos = obb.rayIntersect(pos, sight);
            if (hitPos != null) {
                hit = hitPos;
                box = obb;
                break;
            }
        }

        if (hit != null && Minecraft.getInstance().hitResult.distanceTo(player) > distanceTo(hit, player)) {
            Minecraft.getInstance().hitResult = new ObbHitResult(hit, box);
        }

        BOXES.clear();
    }

    private static double distanceTo(Vector3d location, Entity entity) {
        double d = location.x - entity.getX();
        double e = location.y - entity.getY();
        double f = location.z - entity.getZ();
        return d * d + e * e + f * f;
    }
}
