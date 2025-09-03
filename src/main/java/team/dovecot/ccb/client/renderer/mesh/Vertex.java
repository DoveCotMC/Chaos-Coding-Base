package team.dovecot.ccb.client.renderer.mesh;

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
