package team.dovecot.ccb.client.renderer;

import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public interface IRenderContext {
    Matrix4f getPoseMatrix();

    @Deprecated
    default Matrix3f getNormalMatrix() {
        return new Matrix3f();
    }

    default int getLight() {
        return Renderer.LIGHT_FULL_BRIGHT_BLOCK;
    }

    default int getOverlay() {
        return OverlayTexture.NO_OVERLAY;
    }
}
