package team.dovecot.ccb.client.renderer.model;

import static team.dovecot.ccb.common.ChaosBase.*;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3i;
import team.dovecot.ccb.client.renderer.TextureManager;
import team.dovecot.ccb.client.renderer.model.record.Face;
import team.dovecot.ccb.common.file.IFileProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ObjLoader {
    /**
     * Parse obj model and load to memory
     * @param modelId Model Identifier, required to locate a model in database.
     * @param path Path of the model, must include suffix (like .obj)
     * @param fileProvider To access file
     */
    public static void load(ResourceIdentifier modelId, String path, IFileProvider fileProvider) {
        // TODO: Re-construct required due to new model format
        LOGGER.info("Loading Obj model: \"{}\"", path);
//        LOGGER.debug("File Provider: " + fileProvider.getClass());

        Optional<InputStream> objStreamOptional = fileProvider.openFile(path);
        if (objStreamOptional.isPresent()) {
            InputStream objStream = objStreamOptional.get();
            try {
                String rootDir = fileProvider.getParent(path);
                final String objString = new String(objStream.readAllBytes(), StandardCharsets.UTF_8);

                List<Vector3d> positions = new ArrayList<>();
                List<Vector3d> normals = new ArrayList<>();
                List<Vector2d> uvs = new ArrayList<>();
                // Group name, Material, Index
                Map<String, Map<String, List<Vector3i[]>>> faceCache = new HashMap<>();

                // Materials
                Map<String, ResourceIdentifier> mtls = new HashMap<>();

                // Pre-processing, load vertices data...
                List<String> lines = objString.lines().toList();
                for (String line : lines) {
                    String[] tokens = line.split(" ");
                    switch (tokens[0]) {
                        case "mtllib": {
                            Optional<InputStream> mtlOptional = fileProvider.openFile(rootDir + "/" + tokens[1]);
                            if (mtlOptional.isPresent()) {
                                InputStream mtlStream = mtlOptional.get();
                                final String mtlString = new String(mtlStream.readAllBytes(), StandardCharsets.UTF_8);
                                // Parse mtl and load texture
                                String currentMaterial = "";
                                for (String mtlLine : mtlString.lines().toList()) {
                                    String[] mtlTokens = mtlLine.split(" ");
                                    switch (mtlTokens[0]) {
                                        case "newmtl" -> {
                                            currentMaterial = mtlTokens[1];
                                        }
                                        case "map_Kd" -> {
                                            String mapName = mtlTokens[1];
                                            if (!mapName.endsWith(".png")) {
                                                LOGGER.error("Unknown texture format: " + mapName.substring(0, mapName.lastIndexOf(".")));
                                                break;
                                            }
//                                            new SimpleTexture();
                                            // TODO: Load texture
                                            Optional<InputStream> imageOptional = fileProvider.openFile(rootDir + "/" + mapName);

                                            if (imageOptional.isEmpty()) {
                                                LOGGER.error("Texture not found: " + rootDir + "/" + mapName);
                                                break;
                                            }

                                            InputStream imageStream = imageOptional.get();
                                            TextureManager.getInstance().uploadModel(modelId.join(currentMaterial), imageStream.readAllBytes());
                                            mtls.put(mapName, modelId.join(currentMaterial));
                                        }
                                        default -> {
                                            LOGGER.warn("Unknown Token when parsing mtl file: " + mtlTokens[0]);
                                        }
                                    }
                                }
                            } else {
                                LOGGER.error("Mtl not found: {}", path + ", loading process interrupted!");
                                return;
                            }
                            System.out.println(rootDir + "/" + tokens[1]);
                            break;
                        }
                        case "v": {
                            double x = Double.parseDouble(tokens[1]);
                            double y = Double.parseDouble(tokens[2]);
                            double z = Double.parseDouble(tokens[2]);
                            positions.add(new Vector3d(x, y, z));
                            break;
                        }
                        case "vt": {
                            double u = Double.parseDouble(tokens[1]);
                            double v = Double.parseDouble(tokens[2]);
                            uvs.add(new Vector2d(u, v));
                            break;
                        }
                        case "vn": {
                            double x = Double.parseDouble(tokens[1]);
                            double y = Double.parseDouble(tokens[2]);
                            double z = Double.parseDouble(tokens[2]);
                            normals.add(new Vector3d(x, y, z));
                            break;
                        }
//                        case "f": {
//                            int count = tokens.length - 1;
//                            Vector3i[] indices = new Vector3i[count];
//
//                            for (int i = 0; i < count; i++) {
//                                String[] indexTokens = tokens[i + 1].split("/");
//                                int x = Integer.parseInt(indexTokens[0]);
//                                int y = Integer.parseInt(indexTokens[1]);
//                                int z = Integer.parseInt(indexTokens[2]);
//                                indices[i] = new Vector3i(x, y, z);
//                            }
//
//                            Map<String, List<Vector3i[]>> materials = faceCache.getOrDefault(thisGroup, new HashMap<>());
//                            List<Vector3i[]> faces = materials.getOrDefault(thisMtl, new ArrayList<>());
//                            faces.add(indices);
//                            materials.put(thisMtl, faces);
//                            faceCache.put(thisGroup, materials);
//                            break;
//                        }
                    }
                }

                String thisGroup = "";
                String thisMtl = "";
                Set<String> groupNames = new HashSet<>();
                Map<String, Map<String, List<List<List<Integer>>>>> faceIndices = new HashMap<>();

                // Second iteration, resolve faces
                for (String line : lines) {
                    String[] tokens = line.split(" ");
                    switch (tokens[0]) {
                        case "g":
                        case "o": {
                            thisGroup = tokens[1];
                            groupNames.add(thisGroup);
                            System.out.println("Group: " + thisGroup);
                            break;
                        }
                        case "usemtl": {
//                            if (!Objects.equals(thisMtl, tokens[1])) {
//                                System.out.println("Material changed: " + tokens[1]);
//                            }
                            thisMtl = tokens[1];
                            System.out.println("Mtl: " + thisMtl);
                            break;
                        }
                        case "f": {
                            Map<String, List<List<List<Integer>>>> idkMap = faceIndices.getOrDefault(thisGroup, new HashMap<>());
                            List<List<List<Integer>>> indices = idkMap.getOrDefault(thisMtl, new ArrayList<>());
                            List<List<Integer>> face = new ArrayList<>();
                            for (int i = 1; i < tokens.length; i++) {
                                int[] indicesArray = splitVertexIndexString(tokens[i]);
                                List<Integer> index = List.of(indicesArray[0], indicesArray[1], indicesArray[2]);
                                face.add(index);
                            }
                            indices.add(face);
                            idkMap.put(thisMtl, indices);
                            faceIndices.put(thisGroup, idkMap);
                            break;
                        }
                    }
                }

                LocalModel.Builder builder = new LocalModel.Builder();
                for (String group : faceIndices.keySet()) {
                    Map<String, List<Face>> materialFaces = LocalModel.parseFromRawData(positions, uvs, normals, faceIndices.get(group));
                    System.out.println(materialFaces);
                    builder.pushGroup(group);
                    for (String material : materialFaces.keySet()) {
                        builder.setMaterial(material);
                        builder.addFaces(materialFaces.get(material));
                    }
                    builder.popGroup();
                }

                // Converting to Local model
                // Group name, Raw Model
//                Map<String, LocalModel> groups = new HashMap<>();
//                for (String groupName : groupNames) {
//                    for (Map.Entry<String, List<Vector3i[]>> entry : faceCache.get(groupName).entrySet()) {
//                        String mtl = entry.getKey();
//                        List<Vector3i[]> faceIndices = entry.getValue();
//                        List<Face> faces = new ArrayList<>();
//                        for (Vector3i[] vertices : faceIndices) {
//                            List<Vertex> verticesInFace = new ArrayList<>();
//                            for (Vector3i vertexIndex : vertices) {
//                                int faceIndex = vertexIndex.x - 1;
//                                int uvIndex = vertexIndex.y - 1;
//                                int normalIndex = vertexIndex.z - 1;
//                                verticesInFace.add(new Vertex(
//                                        new Vector3d(positions.get(faceIndex)),
//                                        new Vector3d(normals.get(normalIndex)),
//                                        new Vector2d(uvs.get(uvIndex))
//                                ));
//                            }
//                            faces.add(new Face(verticesInFace));
//                        }
//                        // TODO: Load model to memory
//                        uploadModel(modelId, groupName, faces, mtl, fileProvider);
//                    }
//                }


                // Second iteration, build up faces and groups
            } catch (IOException e) {
                LOGGER.error("Unable to read model: {}", path);
                e.printStackTrace();
            }
        } else {
            LOGGER.error("Model not found: {}", path);
        }
    }

    private static void uploadModel(ResourceIdentifier modelId, String groupName, List<Face> faces, String materialName, IFileProvider fileProvider) {
        LOGGER.info("Save to Memory: {}${}&{}", modelId, groupName, materialName);
    }

    private static int[] splitVertexIndexString(String string) {
        String[] tokens = string.split("/");
        return new int[] {
            Integer.parseInt(tokens[0]) - 1,
            Integer.parseInt(tokens[1]) - 1,
            Integer.parseInt(tokens[2]) - 1
        };
    }
}
