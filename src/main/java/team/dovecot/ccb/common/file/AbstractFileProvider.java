package team.dovecot.ccb.common.file;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.List;

public abstract class AbstractFileProvider {
    protected AbstractFileProvider() {
    }

    public abstract boolean hasChild(String relativePath);

    public abstract boolean isFile(String relativePath);

    @Nullable
    public abstract InputStream openFile(String relativePath);

    public abstract List<String> getChildren(String relativePath);
}
