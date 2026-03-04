package team.dovecotmc.ccb.common.file;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VanillaAssetsFileProvider implements IFileProvider {
    private final String namespace;
    private final ResourceManager resourceManager;

    public VanillaAssetsFileProvider(String namespace, ResourceManager resourceManager) {
        this.namespace = namespace;
        this.resourceManager = resourceManager;
    }

    private Optional<Resource> getResource(String relativePath) {
        return resourceManager.getResource(new ResourceLocation(namespace, relativePath));
    }

    @Override
    public boolean exists(String relativePath) {
        return getResource(relativePath).isPresent();
    }

    @Override
    public Optional<InputStream> openFile(String relativePath) {
        Optional<Resource> optionalResource = getResource(relativePath);

        if (optionalResource.isPresent()) {
            try {
                return Optional.of(optionalResource.get().open());
            } catch (IOException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<String> getChildren(String relativePath) {
        List<Resource> resources = resourceManager.getResourceStack(new ResourceLocation(namespace, relativePath));
        List<String> names = new ArrayList<>();

//        for (Resource resource : resources) {
//            names.add(resource.);
//        }
//        return names;
        throw new RuntimeException("Sorry this function is still under construction =3=");
    }

    @Override
    public String getParent(String path) {
        return path.substring(0, path.replace("\\", "/").lastIndexOf("/"));
    }
}
