package team.dovecot.ccb.common.file;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SystemFileProvider extends AbstractFileProvider {
    private final File root;

    public SystemFileProvider(File root) {
        this.root = root;
    }

    @Override
    public boolean hasChild(String relativePath) {
        return new File(root.toPath().resolve(relativePath).toUri()).exists();
    }

    @Override
    public boolean isFile(String relativePath) {
        return new File(root.toPath().resolve(relativePath).toUri()).isFile();
    }

    @Nullable
    @Override
    public InputStream openFile(String relativePath) {
        try {
            return new FileInputStream(new File(root.toPath().resolve(relativePath).toUri()));
        } catch (FileNotFoundException e) {
            return null;
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
