package team.dovecot.ccb.client.renderer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

public interface IRenderContext {
    Matrix4f getPoseMatrix();

    Matrix3f getNormalMatrix();

    int getLight();

    int getOverlay();
}
