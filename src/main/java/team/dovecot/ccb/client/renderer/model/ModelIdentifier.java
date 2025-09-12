package team.dovecot.ccb.client.renderer.model;

public class ModelIdentifier {
    private final String namespace;
    private final String path;

    public ModelIdentifier(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPath() {
        return path;
    }
}
