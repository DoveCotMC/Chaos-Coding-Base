package team.dovecot.ccb.client;

import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import team.dovecot.ccb.common.ChaosBase;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL32.*;

public class ModelTransformer {
    private static int transformProgram = -1;

    public static void reload() {
//        close();

        // Transform shader
        String vertexShaderSource = """
            #version 150

            in vec3 Position;
            in vec4 Color;
            in vec2 UV0;
            in ivec2 UV1;
            in ivec2 UV2;
            in vec3 Normal;

            uniform mat4 TransformMatrix;

            out vec3 OutPosition;
            out vec4 OutColor;
            out vec2 OutUV0;
            out ivec2 OutUV1;
            out ivec2 OutUV2;
            out vec3 OutNormal;

            void main() {
//                vec4 worldPos = TransformMatrix * vec4(Position, 1.0);
                vec4 worldPos = vec4(Position, 1.0);
                OutPosition = worldPos.xyz;
//                OutNormal = mat3(TransformMatrix) * Normal;
                OutNormal = Normal;
                OutColor = Color;
                
                OutUV0 = UV0;
                OutUV1 = UV1;
                OutUV2 = UV2;
                
                gl_Position = worldPos;
            }
            """;


        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException("[Model Transformer]: Vertex Shader Compile Error:\n" + glGetShaderInfoLog(vertexShader));

        transformProgram = glCreateProgram();
        glAttachShader(transformProgram, vertexShader);

        glBindAttribLocation(transformProgram, 0, "Position");
        glBindAttribLocation(transformProgram, 1, "Color");
        glBindAttribLocation(transformProgram, 2, "UV0");
        glBindAttribLocation(transformProgram, 3, "UV1");
        glBindAttribLocation(transformProgram, 4, "UV2");
        glBindAttribLocation(transformProgram, 5, "Normal");

        String[] varyings = {"OutPosition", "OutColor", "OutUV0", "OutUV1", "OutUV2", "OutNormal"};
        glTransformFeedbackVaryings(transformProgram, varyings, GL_INTERLEAVED_ATTRIBS);

        glLinkProgram(transformProgram);
        if (glGetProgrami(transformProgram, GL_LINK_STATUS) == GL_FALSE)
            throw new RuntimeException("[Model Transformer]: Program Link Error:\n" + glGetProgramInfoLog(transformProgram));

        glDeleteShader(vertexShader);

        ChaosBase.LOGGER.info("[Model Transformer]: Transform shader successfully loaded.");
    }

    private int transformedVertexArray;
    private int transformedVertexBuffer;
    private int transformedIndexBuffer;
    private int vertexCount;

    public ModelTransformer(int arrayObjectId, int vertexBufferId, int indexBufferId, int indexCount, int indexType) {
        this.transformedVertexArray = glGenVertexArrays();
        this.transformedVertexBuffer = glGenBuffers();
        this.transformedIndexBuffer = glGenBuffers();

        glBindVertexArray(arrayObjectId);
        long vertexSize = glGetBufferParameteri64(GL_COPY_READ_BUFFER, GL_BUFFER_SIZE);
        long indexSize = glGetBufferParameteri64(GL_COPY_READ_BUFFER, GL_BUFFER_SIZE);
        this.vertexCount = Math.toIntExact(vertexSize / 16);

        glBindVertexArray(transformedVertexArray);

        glBindBuffer(GL_ARRAY_BUFFER, transformedVertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, vertexSize, GL_DYNAMIC_DRAW);

        int stride = (3 + 4 + 2 + 2 + 2 + 3) * Float.BYTES;
        int offset = 0;

        // Position
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, offset);
        offset += 3 * Float.BYTES;

        // Color
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, stride, offset);
        offset += 4 * Float.BYTES;

        // UV0
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, offset);
        offset += 2 * Float.BYTES;

        // UV1
        glEnableVertexAttribArray(3);
        glVertexAttribIPointer(3, 2, GL_INT, stride, offset);
        offset += 2 * Integer.BYTES;

        // UV2
        glEnableVertexAttribArray(4);
        glVertexAttribIPointer(4, 2, GL_INT, stride, offset);
        offset += 2 * Integer.BYTES;

        // Normal
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 3, GL_FLOAT, false, stride, offset);
//        offset += 3 * Float.BYTES;

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, transformedIndexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexSize, GL_DYNAMIC_DRAW);

//        glBindBuffer(GL_COPY_READ_BUFFER, vertexBufferId);
////        glBindBuffer(GL_COPY_WRITE_BUFFER, transformedVertexBuffer);
//        glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_ARRAY_BUFFER, 0, 0, vertexSize);
//        glBindBuffer(GL_COPY_READ_BUFFER, 0);
////        glBindBuffer(GL_COPY_WRITE_BUFFER, 0);
    }

    public void transform(int arrayObjectId, int vertexBufferId, int indexBufferId, int indexCount, int indexType, Matrix4f transformMatrix) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this._transform(arrayObjectId, vertexBufferId, indexBufferId, indexCount, indexType, new Matrix4f(transformMatrix)));
        } else {
            this._transform(arrayObjectId, vertexBufferId, indexBufferId, indexCount, indexType, transformMatrix);
        }
    }

    private void _transform(int arrayObjectId, int vertexBufferId, int indexBufferId, int indexCount, int indexType, Matrix4f transformMatrix) {
        if (transformProgram <= 0)
            throw new RuntimeException("Model transformer is not initialized!");

//        if (true)
//            return;

        glUseProgram(transformProgram);
        glBindVertexArray(arrayObjectId);

//        int uniformLocation = glGetUniformLocation(transformProgram, "TransformMatrix");
//        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
//        transformMatrix.get(buffer);
//        glUniformMatrix4fv(uniformLocation, false, buffer);

        glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, 0, transformedVertexBuffer);

        glEnable(GL_RASTERIZER_DISCARD);
        glBeginTransformFeedback(GL_TRIANGLES);

//        glDrawElements(GL_TRIANGLES, indexCount, indexType, 0);
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);

        glEndTransformFeedback();
        glDisable(GL_RASTERIZER_DISCARD);
        glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, 0, 0);
    }

    public int getTransformedVertexArray() {
        return transformedVertexArray;
    }

    public static void close() {
        if (isShaderLoaded()) {
            glDeleteProgram(transformProgram);
            transformProgram = -1;
        }
    }

    public static boolean isShaderLoaded() {
        return transformProgram > 0;
    }

    public void release() {
        if (transformedVertexArray >= 0) {
            glDeleteVertexArrays(transformedVertexArray);
            transformedVertexArray = -1;
        }
        if (transformedVertexBuffer >= 0) {
            glDeleteBuffers(transformedVertexBuffer);
            transformedVertexBuffer = -1;
        }
        if (transformedIndexBuffer >= 0) {
            glDeleteBuffers(transformedIndexBuffer);
            transformedIndexBuffer = -1;
        }
    }
}
