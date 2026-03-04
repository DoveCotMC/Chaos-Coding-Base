package team.dovecotmc.ccb.client.mixin.accessor;

import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Deprecated
@Mixin(VertexBuffer.class)
public interface AccessorVertexBuffer {
    @Accessor()
    int getIndexCount();

    @Accessor()
    VertexFormat.IndexType getIndexType();

    @Accessor()
    int getVertexBufferId();

    @Accessor()
    int getIndexBufferId();

    @Accessor()
    int getArrayObjectId();

    @Accessor()
    VertexFormat getFormat();

    @Accessor()
    VertexFormat.Mode getMode();
}
