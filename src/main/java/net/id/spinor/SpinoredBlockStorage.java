package net.id.spinor;

import net.id.spinor.mixin.BlockEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import java.util.Random;

public class SpinoredBlockStorage {
    private BlockState blockState;
    private BlockEntity blockEntity;
    private SpinoredBlockPos spinoredBlockPos;

    public SpinoredBlockStorage(BlockState blockState) {
        this.blockState = blockState;
    }

    public SpinoredBlockStorage(BlockState blockState, BlockEntity blockEntity) {
        this(blockState);
        this.blockEntity = blockEntity;
    }

    public void createFakeBlockPos(SpinorEntityBase spinorEntity) {
        this.spinoredBlockPos = new SpinoredBlockPos(spinorEntity);
    }

    public void updateBlockState(BlockState newState) {
        this.blockState = newState;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    public void tickBlockEntity(World world) {
        if (blockEntity != null) {
            ((BlockEntityAccessor) blockEntity).setPos(spinoredBlockPos);
            BlockEntityTicker<BlockEntity> ticker = blockState.getBlockEntityTicker(world, (BlockEntityType<BlockEntity>) getBlockEntity().getType());
            if (ticker != null)
                ticker.tick(world, this.spinoredBlockPos, blockState, getBlockEntity());
        }
    }

    public void interact(World world, PlayerEntity player, Hand hand, BlockHitResult hit) {
        blockState.getBlock().onUse(blockState, world, spinoredBlockPos, player, hand, hit);
    }

    public void clientDisplayTick(World world) {
        blockState.getBlock().randomDisplayTick(getBlockState(), world, this.spinoredBlockPos, new Random());
    }

    public void createBEfromNBT(NbtCompound nbtCompound) {
        blockEntity = BlockEntity.createFromNbt(this.spinoredBlockPos, blockState, nbtCompound);
    }

    public static SpinoredBlockStorage fromNBT(NbtCompound nbt, SpinorEntityBase spinorEntity) {
        SpinoredBlockStorage spinoredBlockStorage = new SpinoredBlockStorage(Block.getStateFromRawId(nbt.getInt("blockstate")));
        if (nbt.contains("benbt")) {
            spinoredBlockStorage.createFakeBlockPos(spinorEntity);
            spinoredBlockStorage.createBEfromNBT(nbt.getCompound("benbt"));
            spinoredBlockStorage.blockEntity.setWorld(spinorEntity.world);
            spinoredBlockStorage.createFakeBlockPos(spinorEntity);
        }
        return spinoredBlockStorage;
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("blockstate", Block.getRawIdFromState(blockState));
        if (getBlockEntity() != null) {
            NbtCompound beNBT = new NbtCompound();
            blockEntity.writeNbt(beNBT);
            nbt.put("benbt", beNBT);
        }
    }
}
