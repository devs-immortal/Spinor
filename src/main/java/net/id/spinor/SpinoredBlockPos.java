package net.id.spinor;


import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SpinoredBlockPos extends BlockPos {
    private final SpinorEntity spinorEntity;
    private final BlockPos relativePos;

    public SpinoredBlockPos(SpinorEntity spinorEntity, BlockPos relativePos) {
        super(spinorEntity.getBlockPos().add(relativePos));
        this.spinorEntity = spinorEntity;
        this.relativePos = relativePos;
    }

    public SpinoredBlockPos updatePosition() {
        return new SpinoredBlockPos(spinorEntity, relativePos);
    }

    public BlockState getBlockState() {
        return spinorEntity.getSpinoredBlock(relativePos).getBlockState();
    }

    public void setBlockState(BlockState newState) {
        spinorEntity.getSpinoredBlock(relativePos).updateBlockState(newState);
    }

    public BlockEntity getBlockEntity() {
        return spinorEntity.getSpinoredBlock(relativePos).getBlockEntity();
    }
}
