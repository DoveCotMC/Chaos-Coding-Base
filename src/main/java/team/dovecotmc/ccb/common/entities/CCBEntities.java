package team.dovecotmc.ccb.common.entities;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import team.dovecotmc.ccb.client.boundingbox.EntityInteraction;

public class CCBEntities {
    public static EntityType<EntityInteraction> INTERACTION;

    public static void initialize(String modId) {
        INTERACTION = Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(modId, "interaction"), FabricEntityTypeBuilder.create(MobCategory.MISC, EntityInteraction::new).disableSaving().disableSummon().fireImmune().build());
    }
}
