package team.dovecot.ccb.common.file;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SystemFileProvider implements IFileProvider {
    private final File root;

    public SystemFileProvider(String root) {
        this(new File(root));
    }

    public SystemFileProvider(File root) {
        this.root = root;
    }

    private File getFile(String relativePath) {
        return new File(root.toPath().resolve(relativePath).toUri());
    }

    @Override
    public boolean exists(String relativePath) {
        return getFile(relativePath).exists();
    }

    public boolean isFile(String relativePath) {
        return getFile(relativePath).isFile();
    }

    @Override
    public Optional<InputStream> openFile(String relativePath) {
        try {
            return Optional.of(new FileInputStream(new File(root.toPath().resolve(relativePath).toUri())));
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<String> getChildren(String relativePath) {
        List<File> files = List.of(Objects.requireNonNull(this.root.listFiles()));
        List<String> names = new ArrayList<>();

        for (File file : files) {
            names.add(file.getName());
        }

        return names;
    }
}
