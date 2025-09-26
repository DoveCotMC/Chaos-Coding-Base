package team.dovecot.ccb;

import net.minecraft.resources.ResourceLocation;
import team.dovecot.ccb.client.renderer.model.ObjLoader;
import team.dovecot.ccb.common.file.SystemFileProvider;

import java.io.File;

public class Test {
    public static void main(String[] args) {
        SystemFileProvider fileProvider = new SystemFileProvider(new File("./src/main/resources/assets/ccb/"));
        ObjLoader.load(new ResourceLocation("ccb", "utah_teapot"), "model/teapot/teapot.obj", fileProvider);
//        BlockBenchLoader.load(new ResourceIdentifier("ccb", "utah_teapot"), "model/teapot/teapot.bbmodel", fileProvider);
    }
}
