package team.dovecotmc.ccb.client.renderer.model;

import static team.dovecotmc.ccb.entries.ChaosBase.LOGGER;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import team.dovecotmc.ccb.common.file.IFileProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BlockBenchLoader {
    public static void load(ResourceIdentifier modelId, String path, IFileProvider fileProvider) {
        Optional<InputStream> rawStream = fileProvider.openFile(path);

        if (rawStream.isEmpty()) {
            LOGGER.error("Could not find model: {}", path);
            return;
        }

        JsonObject json;
        try {
            json = JsonParser.parseString(new String(rawStream.get().readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            LOGGER.error("Could not resolve model: {}", path);
            return;
        }
        System.out.println(json.keySet());
        for (JsonElement element : json.getAsJsonArray("elements")) {
            System.out.println(element.getAsJsonObject().keySet());
            System.out.println(element.getAsJsonObject().get("vertices"));
            JsonObject vertices = element.getAsJsonObject().get("vertices").getAsJsonObject();
            JsonObject faces = element.getAsJsonObject().get("faces").getAsJsonObject();
            for (String faceKey : faces.keySet()) {
                JsonObject face = faces.get(faceKey).getAsJsonObject();
                for (JsonElement vertexId : face.get("vertices").getAsJsonArray()) {
                    System.out.println(vertices.get(vertexId.getAsString()));
                }
            }
        }
    }
}
