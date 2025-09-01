package team.dovecot.ccb.client.renderer.mesh;

import net.minecraft.world.phys.Vec3;
import team.dovecot.ccb.common.math.Vec2d;
import team.dovecot.ccb.common.math.Vec3d;

public class Vertex {
    public final Vec3d position;
    public final Vec3d color;
    public final Vec2d uv;

    public Vertex(Vec3d position, Vec3d color, Vec2d uv) {
        this.position = position;
        this.color = color;
        this.uv = uv;
    }
}
