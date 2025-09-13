package team.dovecot.ccb.client.renderer.model;

import java.util.Objects;

public class ResourceIdentifier {
    private final String namespace;
    private final String path;

    public ResourceIdentifier(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPath() {
        return path;
    }

    public ResourceIdentifier join(String name) {
        return new ResourceIdentifier(namespace, this.path + "&" + name);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ResourceIdentifier that = (ResourceIdentifier) o;
        return Objects.equals(namespace, that.namespace) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, path);
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }
}
