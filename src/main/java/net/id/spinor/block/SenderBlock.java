package net.id.spinor.block;

import net.id.spinor.SpinorHostEntity;
import net.id.spinor.SpinorMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class SenderBlock extends HorizontalFacingBlock {
    public SenderBlock(Settings settings) {
        super(settings);
        Registry.register(Registry.BLOCK, new Identifier(SpinorMod.MOD_ID, "sender"), this);
        Registry.register(Registry.ITEM, new Identifier(SpinorMod.MOD_ID, "sender"), new BlockItem(this, new Item.Settings().group(SpinorMod.GROUP)));
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockPos spinorPos = pos.up();
            List<SpinorHostEntity> foundEntities = world.getEntitiesByType(TypeFilter.instanceOf(SpinorHostEntity.class), new Box(spinorPos), spinorEntity -> true);
            SpinorHostEntity spinorEntity;
            if (foundEntities.size() == 0) {
                spinorEntity = new SpinorHostEntity(world, spinorPos.getX() + .5d, spinorPos.getY(), spinorPos.getZ() + .5d);
                spinorEntity.createSpinorAt(spinorPos, pos);
                world.spawnEntity(spinorEntity);
            } else {
                spinorEntity = foundEntities.get(0);
                spinorEntity.setPosition(spinorPos.getX() + .5d, spinorPos.getY(), spinorPos.getZ() + .5d);
            }

            Vec3d facingVel = Vec3d.of(state.get(FACING).getVector()).multiply(.1d);

            spinorEntity.setVelocity(facingVel.getX(), facingVel.getY(), facingVel.getZ());


            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
