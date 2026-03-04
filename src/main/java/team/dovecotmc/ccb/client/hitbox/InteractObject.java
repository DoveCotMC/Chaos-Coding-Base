package team.dovecotmc.ccb.client.hitbox;

public class InteractObject {
    private final IInteractHandler handler;
    private Obb boundingBox;
    private boolean alive;

    public InteractObject(Obb boundingBox, IInteractHandler handler) {
        this.handler = handler;
        this.boundingBox = boundingBox;
        this.alive = true;
    }

    public void setBoundingBox(Obb boundingBox) {
        this.boundingBox = boundingBox;
    }

    public Obb getBoundingBox() {
        return boundingBox;
    }

    public IInteractHandler getHandler() {
        return handler;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        this.alive = false;
    }
}
