package team.dovecot.ccb.common.file;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface IFileProvider {
    boolean exists(String relativePath);

    Optional<InputStream> openFile(String relativePath);

    List<String> getChildren(String relativePath);

    String getParent(String path);
}
