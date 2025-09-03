package team.dovecot.ccb.client;

import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.GameRenderer;
import org.lwjgl.glfw.GLFW;

import java.util.OptionalInt;

public class TestRun {
    public static void main(String[] args) {
//        RenderSystem.setShader(GameRenderer::);
        System.out.println("Test Run 114514!");
        GLFW.glfwInit();
        Window window = new Window(new WindowEventHandler() {
            @Override
            public void setWindowActive(boolean bl) {
            }

            @Override
            public void resizeDisplay() {

            }

            @Override
            public void cursorEntered() {

            }
        }, new ScreenManager(new MonitorCreator() {
            @Override
            public Monitor createMonitor(long l) {
                return new Monitor(l);
            }
        }), new DisplayData(800, 600, OptionalInt.empty(), OptionalInt.empty(), false), "114514", "1919810");
        while (!window.shouldClose()) {
            window.updateDisplay();
        }
    }
}
