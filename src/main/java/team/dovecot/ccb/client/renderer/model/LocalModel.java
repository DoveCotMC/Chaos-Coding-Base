package team.dovecot.ccb.client.renderer.model;

import team.dovecot.ccb.client.renderer.model.record.Face;

import java.nio.ByteBuffer;
import java.util.List;

public class LocalModel {
    private final List<Face> faces;
    private final ByteBuffer texture;

    public LocalModel(List<Face> faces, ByteBuffer texture) {
        this.faces = faces;
        this.texture = texture;
    }

    public int getNumFaces() {
        return faces.size();
    }
}
