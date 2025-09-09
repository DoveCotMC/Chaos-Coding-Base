package team.dovecot.ccb.client.renderer.model;

import static team.dovecot.ccb.common.ChaosBase.*;

import team.dovecot.ccb.common.file.IFileProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ObjLoader {
    public static void load(String path, IFileProvider fileProvider) {
        LOGGER.info("Loading Obj model: \"{}\"", path);
//        LOGGER.debug("File Provider: " + fileProvider.getClass());

        Optional<InputStream> inputStreamOptional = fileProvider.openFile(path);
        if (inputStreamOptional.isPresent()) {
            InputStream inputStream = inputStreamOptional.get();
            try {
                byte[] bytes = inputStream.readAllBytes();
                String modelString = new String(bytes, StandardCharsets.UTF_8);
//                LOGGER.debug("Resource: " + modelString);

                for (String line : modelString.lines().toList()) {
                    if (line.startsWith("o") || line.startsWith("g")) {
                        System.out.println(line);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Unable to read model: {}", path);
                e.printStackTrace();
            }
        } else {
            LOGGER.error("Model not found: {}", path);
        }
    }
}
