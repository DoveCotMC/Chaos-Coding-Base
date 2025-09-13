package team.dovecot.ccb;

import team.dovecot.ccb.client.renderer.model.ResourceIdentifier;
import team.dovecot.ccb.client.renderer.model.ObjLoader;
import team.dovecot.ccb.common.file.SystemFileProvider;

public class Test {
    public static void main(String[] args) {
        ObjLoader.load(new ResourceIdentifier("ccb", "utah_teapot"), "model/teapot/teapot.obj", new SystemFileProvider("C:\\Users\\admin\\Documents\\ChaosCodingBase\\1.20.1-fabric\\ChaosCodingBase\\src\\main\\resources\\assets\\ccb"));
    }
}
