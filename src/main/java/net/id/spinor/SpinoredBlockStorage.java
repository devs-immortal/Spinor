package net.id.spinor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
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

    public void createFakeBlockPos(SpinorEntity spinorEntity, BlockPos blockPos) {
        this.spinoredBlockPos = new SpinoredBlockPos(spinorEntity, blockPos);
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
        if(blockEntity != null) {
            BlockEntityTicker<BlockEntity> ticker = blockState.getBlockEntityTicker(world, (BlockEntityType<BlockEntity>) getBlockEntity().getType());
            if (ticker != null)
                ticker.tick(world, this.spinoredBlockPos, blockState, getBlockEntity());
        }
    }

    public void clientDisplayTick(World world) {
        blockState.getBlock().randomDisplayTick(getBlockState(), world, this.spinoredBlockPos, new Random());
    }

    public void createBEfromNBT(NbtCompound nbtCompound) {
        blockEntity = BlockEntity.createFromNbt(this.spinoredBlockPos, blockState, nbtCompound);
    }

    public static SpinoredBlockStorage fromNBT(NbtCompound nbt, SpinorEntity spinorEntity, BlockPos pos) {
        SpinoredBlockStorage spinoredBlockStorage = new SpinoredBlockStorage(Block.getStateFromRawId(nbt.getInt("blockstate")));
        if (nbt.contains("benbt")) {
            spinoredBlockStorage.createFakeBlockPos(spinorEntity, pos);
            spinoredBlockStorage.createBEfromNBT(nbt.getCompound("benbt"));
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
