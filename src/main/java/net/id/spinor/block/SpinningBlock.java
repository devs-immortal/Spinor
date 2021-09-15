package net.id.spinor.block;

import net.id.spinor.SpinorHostEntity;
import net.id.spinor.SpinorMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class SpinningBlock extends Block {
    public SpinningBlock(AbstractBlock.Settings settings) {
        super(settings);
        Registry.register(Registry.BLOCK, new Identifier(SpinorMod.MOD_ID, "spinningblock"), this);
        Registry.register(Registry.ITEM, new Identifier(SpinorMod.MOD_ID, "spinningblock"), new BlockItem(this, new Item.Settings().group(SpinorMod.GROUP)));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockPos spinorPos = pos.up();
            List<SpinorHostEntity> foundEntities = world.getEntitiesByType(TypeFilter.instanceOf(SpinorHostEntity.class), new Box(spinorPos), spinorEntity -> true);
            SpinorHostEntity spinorHostEntity;
            if (foundEntities.size() == 0) {
                spinorHostEntity = new SpinorHostEntity(world, spinorPos.getX() + .5d, spinorPos.getY(), spinorPos.getZ() + .5d);
                spinorHostEntity.createSpinorAt(spinorPos, pos);
                world.spawnEntity(spinorHostEntity);
            } else {
                spinorHostEntity = foundEntities.get(0);
                spinorHostEntity.setPosition(spinorPos.getX() + .5d, spinorPos.getY(), spinorPos.getZ() + .5d);
            }
            spinorHostEntity.setYaw(spinorHostEntity.getYaw() + 1);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
