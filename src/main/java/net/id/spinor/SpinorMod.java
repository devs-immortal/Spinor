package net.id.spinor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.id.spinor.block.SenderBlock;
import net.id.spinor.block.SpinningBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class SpinorMod implements ModInitializer {
    public static final String MOD_ID = "spinor";

    public static Identifier SPINORHOST_ID = new Identifier(MOD_ID, "spinorhost");
    public static Identifier SPINORCHILD_ID = new Identifier(MOD_ID, "spinorchild");
    public static EntityType<SpinorHostEntity> SPINOR_ENTITY_TYPE;
    public static EntityType<SpinorChildEntity> SPINOR_CHILD_TYPE;
    public static final ItemGroup GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "spinor"), () -> new ItemStack(Blocks.PISTON_HEAD));

    @Override
    public void onInitialize() {
        SPINOR_ENTITY_TYPE = FabricEntityTypeBuilder.<SpinorHostEntity>create(SpawnGroup.MISC, SpinorHostEntity::new).dimensions(EntityDimensions.changing(1, 1)).trackRangeBlocks(8).build();
        SPINOR_CHILD_TYPE = FabricEntityTypeBuilder.<SpinorChildEntity>create(SpawnGroup.MISC, SpinorChildEntity::new).dimensions(EntityDimensions.changing(1, 1)).trackRangeBlocks(8).build();
        Registry.register(Registry.ENTITY_TYPE, SPINORHOST_ID, SPINOR_ENTITY_TYPE);
        Registry.register(Registry.ENTITY_TYPE, SPINORCHILD_ID, SPINOR_CHILD_TYPE);

        new SenderBlock(AbstractBlock.Settings.of(Material.METAL));
        new SpinningBlock(AbstractBlock.Settings.of(Material.METAL));
    }
}