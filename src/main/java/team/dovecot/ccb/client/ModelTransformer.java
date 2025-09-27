package team.dovecot.ccb.client;

import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;
import team.dovecot.ccb.common.ChaosBase;

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
                vec4 worldPos = TransformMatrix * vec4(Position, 1.0);
                OutPosition = worldPos.xyz;
                OutNormal = mat3(TransformMatrix) * Normal;
                OutColor = Color;
                
                OutUV0 = UV0;
                OutUV1 = UV1;
                OutUV2 = UV2;
                
                gl_Position = vec4(0.0);
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

    private final int transformedVertexArray;

    public ModelTransformer(int arrayObjectId, int vertexBufferId, int indexBufferId, int indexCount, int indexType) {
        this.transformedVertexArray = glGenVertexArrays();
        System.out.println("New transformer");

        glBindVertexArray(transformedVertexArray);
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);

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
        offset += 3 * Float.BYTES;

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
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

//        glBindVertexArray(vertexArray);
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer);

//        glUseProgram(transformProgram);
//
//        int uniformLocation = glGetUniformLocation(transformProgram, "TransformMatrix");
//
//        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
//        buffer.position(0);
//        transformMatrix.get(buffer);
//        buffer.flip();
//        glUniformMatrix4fv(uniformLocation, false, buffer);

//        glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, 0, transformedVertexBuffer);
//        glEnable(GL_RASTERIZER_DISCARD);
//        glBeginTransformFeedback(GL_TRIANGLES);
//        glDrawElements(GL_TRIANGLES, indexCount, indexType, 0);
//        glEndTransformFeedback();
//        glDisable(GL_RASTERIZER_DISCARD);
    }

    public int getTransformedVertexArray() {
        return transformedVertexArray;
    }

    private void bindVertexAttrib(int index, int vbo, int size, int type, boolean normalize) {
        glEnableVertexAttribArray(index);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(index, size, type, normalize, 0, 0L);
    }

    private void bindVertexAttribInt(int index, int vbo, int size) {
        glEnableVertexAttribArray(index);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribIPointer(index, size, GL_INT, 0, 0L);
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
        glDeleteVertexArrays(this.transformedVertexArray);
    }
}
