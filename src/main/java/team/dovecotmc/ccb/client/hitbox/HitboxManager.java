package team.dovecotmc.ccb.client.hitbox;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.HitResult;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import team.dovecotmc.ccb.entries.ChaosBase;
import team.dovecotmc.nextrain.util.JOMLUtil;

import java.util.*;

public class HitboxManager {
    private static final Map<ResourceLocation, IHitTest> HIT_TESTS = new HashMap<>();

    private static Vector3d eyePos = null;
    private static Vector3d sight = null;

    public static void pick(Player player, float f) {
        eyePos = new Vector3d(player.getEyePosition(f).toVector3f());
        sight = new Vector3d(player.getViewVector(f).toVector3f());

        for (IHitTest hitTest : HIT_TESTS.values()) {
            hitTest.test(eyePos, sight);
        }
    }

    public static void hittest(List<InteractObject> objects) {
        if (eyePos == null || sight == null)
            return;

        Vector3d hit = null;
        double distance = Double.MAX_VALUE;
        InteractObject interactObject = null;

        for (InteractObject object : objects) {
            Obb.HitResult hitResult = object.getBoundingBox().inflate(0.0, 0.0, 0.0).rayIntersect(eyePos, sight);
            if (hitResult != null) {
                if (distance > hitResult.distance()) {
                    hit = hitResult.pos();
                    distance = hitResult.distance();
                    interactObject = object;
                }
            }
        }

        if (Minecraft.getInstance().hitResult != null && hit != null) {
            if (Minecraft.getInstance().hitResult.getType().equals(HitResult.Type.MISS))
                Minecraft.getInstance().hitResult = new CCBHitResult(hit, interactObject);
            else if (Minecraft.getInstance().hitResult.getLocation().toVector3f().distance(JOMLUtil.asVector3f(eyePos)) >= distance) {
                Minecraft.getInstance().hitResult = new CCBHitResult(hit, interactObject);
            }
        }
    }

    public static void addHitTest(ResourceLocation resourceLocation, IHitTest hitTest) {
        HIT_TESTS.put(resourceLocation, hitTest);
    }

    private static double distanceTo(Vector3d location, Entity entity) {
        double d = location.x - entity.getX();
        double e = location.y - entity.getY();
        double f = location.z - entity.getZ();
        return d * d + e * e + f * f;
    }

    private static double distanceTo(Vector3d to, Vector3d from) {
        double d = to.x - from.x();
        double e = to.y - from.y();
        double f = to.z - from.z();
        return d * d + e * e + f * f;
    }

    public interface IHitTest {
        void test(Vector3d eyePos, Vector3d sight);
    }
}
