package team.dovecot.ccb.client.renderer.mesh;

import team.dovecot.genericrailwayplatform.renderer.EnumRenderType;
import team.dovecot.genericrailwayplatform.renderer.RenderBuffer;

import java.util.List;

public class Mesh {
    public final EnumRenderType renderType;
    public List<Face> faces;

    public Mesh(EnumRenderType renderType, List<Face> faces) {
        this.renderType = renderType;
        this.faces = faces;
    }

    public RenderBuffer asBuffer() {
        return new RenderBuffer(0, 0);
    }
}
