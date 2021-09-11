package net.id.spinor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.id.spinor.block.SenderBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;


public class SpinorMod implements ModInitializer {
    public static final String MOD_ID = "spinor";

    public static Identifier SPINOR_ID = new Identifier(MOD_ID, "spinor");
    public static EntityType<SpinorEntity> SPINOR_ENTITY_TYPE;

    public static final ItemGroup GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "spinor"), () -> new ItemStack(Blocks.PISTON_HEAD));

    @Override
    public void onInitialize() {
        SPINOR_ENTITY_TYPE = FabricEntityTypeBuilder.<SpinorEntity>create(SpawnGroup.MISC, SpinorEntity::new).dimensions(EntityDimensions.changing(1, 1)).trackRangeBlocks(8).build();
        Registry.register(Registry.ENTITY_TYPE, SPINOR_ID, SPINOR_ENTITY_TYPE);

        UseItemCallback.EVENT.register(((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (!world.isClient) {
                Item item = stack.getItem();
                if (item == Items.STICK) {
                    HitResult hit = player.raycast(6, 1, false);
                    if (hit.getType() == HitResult.Type.BLOCK) {
                        BlockHitResult blockHit = (BlockHitResult) hit;
                        BlockPos usedBlockPos = blockHit.getBlockPos();
                        SpinorEntity spinorEntity = new SpinorEntity(world, usedBlockPos.getX() + .5d, usedBlockPos.getY(), usedBlockPos.getZ() + .5d);
                        spinorEntity.grabAllBlocks(usedBlockPos);
                        world.spawnEntity(spinorEntity);
                    }
                }
            }
            return TypedActionResult.pass(stack);
        }));

        new SenderBlock(AbstractBlock.Settings.of(Material.METAL));
    }
}