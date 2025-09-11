package team.dovecot.ccb.client.renderer.model;

import static team.dovecot.ccb.common.ChaosBase.*;

import net.minecraft.client.model.Model;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3i;
import team.dovecot.ccb.client.renderer.model.record.Face;
import team.dovecot.ccb.client.renderer.model.record.Vertex;
import team.dovecot.ccb.common.file.IFileProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ObjLoader {
    public static void load(String path, IFileProvider fileProvider) {
        LOGGER.info("Loading Obj model: \"{}\"", path);
//        LOGGER.debug("File Provider: " + fileProvider.getClass());

        Optional<InputStream> inputStreamOptional = fileProvider.openFile(path);
        if (inputStreamOptional.isPresent()) {
            InputStream inputStream = inputStreamOptional.get();
            try {
                byte[] bytes = inputStream.readAllBytes();
                final String modelString = new String(bytes, StandardCharsets.UTF_8);
//                LOGGER.debug("Resource: " + modelString);
                String thisGroup = "";
                String thisMtl = "";
                // Group name, Raw Model
//                Map<String, LocalModel> models = new HashMap<>();
//                Map<String, List<Face>> facesCache = new HashMap<>();
//                Map<String, List<Vertex>> verticesCache = new HashMap<>();

                Set<String> groups = new HashSet<>();
                Map<String, List<Vector3d>> positionCache = new HashMap<>();
                Map<String, List<Vector3d>> uv = new HashMap<>();
                Map<String, List<Vector2d>> uvCache = new HashMap<>();
                Map<String, List<Vector3i[]>> faceCache = new HashMap<>();

                for (String line : modelString.lines().toList()) {
                    String[] tokens = line.split(" ");
                    switch (tokens[0]) {
                        case "usemtl": {
                            if (!Objects.equals(thisMtl, tokens[1])) {
                                System.out.println("Material changed: " + tokens[1]);
                            }
                            thisMtl = tokens[1];
                            break;
                        }
                        case "g":
                        case "o": {
                            thisGroup = tokens[1];
                            groups.add(thisGroup);
                            break;
                        }
                        case "v": {
                            double x = Double.parseDouble(tokens[1]);
                            double y = Double.parseDouble(tokens[2]);
                            double z = Double.parseDouble(tokens[2]);
                            List<Vector3d> positions = positionCache.getOrDefault(thisGroup, new ArrayList<>());
                            positions.add(new Vector3d(x, y, z));
                            positionCache.put(thisGroup, positions);
                            break;
                        }
                        case "vt": {
                            double u = Double.parseDouble(tokens[1]);
                            double v = Double.parseDouble(tokens[2]);
                            List<Vector2d> uvs = uvCache.getOrDefault(thisGroup, new ArrayList<>());
                            uvs.add(new Vector2d(u, v));
                            uvCache.put(thisGroup, uvs);
                            break;
                        }
                        case "vn": {
                            double x = Double.parseDouble(tokens[1]);
                            double y = Double.parseDouble(tokens[2]);
                            double z = Double.parseDouble(tokens[2]);
                            List<Vector3d> normals = uv.getOrDefault(thisGroup, new ArrayList<>());
                            normals.add(new Vector3d(x, y, z));
                            uv.put(thisGroup, normals);
                            break;
                        }
                        case "f": {
                            int count = tokens.length - 1;
                            Vector3i[] indices = new Vector3i[count];

                            for (int i = 0; i < count; i++) {
                                String[] indexTokens = tokens[i + 1].split("/");
                                int x = Integer.parseInt(indexTokens[0]);
                                int y = Integer.parseInt(indexTokens[1]);
                                int z = Integer.parseInt(indexTokens[2]);
                                indices[i] = new Vector3i(x, y, z);
                                System.out.println(indices[i].x + " / " + indices[i].y + " / " + indices[i].z);
                            }

                            List<Vector3i[]> faces = faceCache.getOrDefault(thisGroup, new ArrayList<>());
                            faces.add(indices);
                            faceCache.put(thisGroup, faces);
                            break;
                        }
                    }
                }
                for (String group : groups) {
                    System.out.println(positionCache.get(group));
                }
            } catch (IOException e) {
                LOGGER.error("Unable to read model: {}", path);
                e.printStackTrace();
            }
        } else {
            LOGGER.error("Model not found: {}", path);
        }
    }

    private static List<LocalModel> parseObjParts(String[] objLines, int startIndex) {
        List<LocalModel> models = new ArrayList<>();

        return models;
    }
}
