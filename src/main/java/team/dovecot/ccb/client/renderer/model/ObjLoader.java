package team.dovecot.ccb.client.renderer.model;

import static team.dovecot.ccb.common.ChaosBase.*;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
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
     * @param modelLocation Model Identifier, required to locate a model in database.
     * @param path Path of the model, must include suffix (like .obj)
     * @param fileProvider To access file
     * @return The model
     */
    public static @Nullable LocalModel load(ResourceLocation modelLocation, String path, IFileProvider fileProvider) {
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
                Map<String, ResourceLocation> mtls = new HashMap<>();

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

                                            // Upload to vanilla texture manager
                                            try (InputStream imageStream = imageOptional.get()) {
                                                ResourceLocation mapLocation = modelLocation.withSuffix("/").withSuffix(mapName);

                                                Minecraft.getInstance().getTextureManager().register(
                                                        mapLocation,
                                                        new DynamicTexture(NativeImage.read(imageStream))
                                                );
                                                mtls.put(currentMaterial, mapLocation);
                                            }
                                        }
                                        default -> {
                                            LOGGER.warn("Unknown Token when parsing mtl file: " + mtlTokens[0]);
                                        }
                                    }
                                }
                            } else {
                                LOGGER.error("Mtl not found: {}", path + ", loading process interrupted!");
                                return null;
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
                            break;
                        }
                        case "usemtl": {
                            thisMtl = tokens[1];
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

                // Second iteration, build up faces and groups
                LocalModel.Builder builder = new LocalModel.Builder();
                for (String group : faceIndices.keySet()) {
                    Map<String, List<Face>> materialFaces = LocalModel.parseFromRawData(positions, uvs, normals, faceIndices.get(group));
                    builder = builder.pushGroup(group);
                    for (String material : materialFaces.keySet()) {
                        System.out.println(material);
                        System.out.println(mtls.get(material));
                        builder.addMaterial(material, mtls.get(material));
                        builder.addFaces(materialFaces.get(material));
                    }
                    builder = builder.popGroup();
                }
                return builder.build();
            } catch (IOException e) {
                LOGGER.error("Unable to read model: {}", path);
                e.printStackTrace();
                return null;
            }
        } else {
            LOGGER.error("Model not found: {}", path);
            return null;
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
