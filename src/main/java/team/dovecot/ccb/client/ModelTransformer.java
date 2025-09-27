package team.dovecot.ccb.client;

import static org.lwjgl.opengl.GL32.*;

public class ModelTransformer {
    public static void test() {
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
            throw new RuntimeException("[Model Transformer] Vertex Shader Compile Error:\n" + glGetShaderInfoLog(vertexShader));

        int program = glCreateProgram();
        glAttachShader(program, vertexShader);

        glBindAttribLocation(program, 0, "Position");
        glBindAttribLocation(program, 1, "Normal");

        String[] varyings = {"OutPosition", "OutColor", "OutUV0", "OutUV1", "OutUV2", "OutNormal"};
        glTransformFeedbackVaryings(program, varyings, GL_INTERLEAVED_ATTRIBS);

        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
            throw new RuntimeException("[Model Transformer] Program Link Error:\n" + glGetProgramInfoLog(program));

        glDeleteShader(vertexShader);

        System.out.println("✔ Shader compiled and linked successfully!");
        System.out.println("Shader: " + program);
    }
}
