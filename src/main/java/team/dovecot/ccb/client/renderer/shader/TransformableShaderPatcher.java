package team.dovecot.ccb.client.renderer.shader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import team.dovecot.ccb.client.renderer.Renderer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TransformableShaderPatcher {
    public static String patchJson(String originalString) {
        JsonObject json = JsonParser.parseString(originalString).getAsJsonObject();
        JsonArray array = json.getAsJsonArray("uniforms");

        json.addProperty("vertex", json.get("vertex").getAsString() + Renderer.patchedShaderSuffix);
        json.addProperty("fragment", json.get("fragment").getAsString() + Renderer.patchedShaderSuffix);

        JsonObject uniform = new JsonObject();
        uniform.addProperty("name", "TransformMat");
        uniform.addProperty("type", "matrix4x4");
        uniform.addProperty("count", 16);
        uniform.add("values", JsonParser.parseString("[ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0 ]"));
        array.add(uniform);

        json.add("uniforms", array);

        return json.toString();
    }

    public static String patchVsh(String originalString) {
        String[] split = originalString.split("void main");
        split[0] = split[0]
                .replace(
                        "uniform mat4 ModelViewMat;",
                        "uniform mat4 ModelViewMat;\n" +
                        "uniform mat4 TransformMat;"
                );
        split[1] = split[1]
                .replaceAll(
                        "\\bPosition\\b",
                        "(TransformMat * vec4(Position, 1.0)).xyz"
                ).replaceAll(
                        "\\bNormal\\b",
                        "normalize(mat3(TransformMat) * Normal)"
                );

        return split[0] + "void main" + split[1];
    }

    public static String patchFsh(String originalString) {
        // Nothing to do ;)
        return originalString;
    }

    public static class ResourceProvider implements net.minecraft.server.packs.resources.ResourceProvider {
        public final net.minecraft.server.packs.resources.ResourceProvider parent;

        public ResourceProvider(net.minecraft.server.packs.resources.ResourceProvider parent) {
            this.parent = parent;
        }

        @Override
        public Optional<Resource> getResource(ResourceLocation resourceLocation) {
            if (resourceLocation.getPath().contains(Renderer.patchedShaderSuffix))
                resourceLocation = new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath().replace(Renderer.patchedShaderSuffix, ""));

            Optional<Resource> parentResource = parent.getResource(resourceLocation);

            if (parentResource.isEmpty())
                return parentResource;

            try (InputStream stream = parentResource.get().open()) {
                String stringValue = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                if (resourceLocation.getPath().endsWith(".json")) {
                    stringValue = patchJson(stringValue);
                } else if (resourceLocation.getPath().endsWith(".vsh")) {
                    stringValue = patchVsh(stringValue);
                } else if (resourceLocation.getPath().endsWith(".fsh")) {
                    stringValue = patchFsh(stringValue);
                }
                final String finalStringValue = stringValue;
                return Optional.of(new Resource(parentResource.get().source(), () -> new ByteArrayInputStream(finalStringValue.getBytes(StandardCharsets.UTF_8))));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
