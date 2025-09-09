package team.dovecot.ccb.common.file;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.List;

public class VanillaResourceFileProvider extends AbstractFileProvider {
    private final String rootPath;

    public VanillaResourceFileProvider(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public boolean hasChild(String relativePath) {
        return false;
    }

    @Override
    public boolean isFile(String relativePath) {
        return false;
    }

    @Nullable
    @Override
    public InputStream openFile(String relativePath) {
        return null;
    }

    @Override
    public List<String> getChildren(String relativePath) {
        return null;
    }
}
