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
                    String[] tokens = line.split(" ");
                    switch (tokens[0]) {
                        case "usemtl":
                            System.out.println(line);
                            break;
                        case "g":
                        case "o":
                            System.out.println(line);
                            break;
                        case "v": {
                            float x = Float.parseFloat(tokens[1]);
                            float y = Float.parseFloat(tokens[2]);
                            float z = Float.parseFloat(tokens[2]);
                            System.out.println("Vertex: " + tokens[1] + " " + tokens[2] + " " + tokens[3]);
                            break;
                        }
                        case "vt": {
                            float u = Float.parseFloat(tokens[1]);
                            float v = Float.parseFloat(tokens[2]);
                            System.out.println("UV: " + tokens[1] + " " + tokens[2]);
                            break;
                        }
                        case "vn": {
                            float x = Float.parseFloat(tokens[1]);
                            float y = Float.parseFloat(tokens[2]);
                            float z = Float.parseFloat(tokens[2]);
                            System.out.println("Normal: " + tokens[1] + " " + tokens[2] + " " + tokens[3]);
                            break;
                        }
                        case "f":
                            break;
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
