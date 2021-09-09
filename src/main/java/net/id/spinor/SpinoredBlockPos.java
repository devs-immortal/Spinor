package net.id.spinor;


import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SpinoredBlockPos extends BlockPos {
    private final SpinorEntity spinorEntity;
    private final BlockPos spoofedPos;

    public SpinoredBlockPos(SpinorEntity spinorEntity, BlockPos spoofedPos) {
        super(spinorEntity.getBlockPos().add(spoofedPos));
        this.spinorEntity = spinorEntity;
        this.spoofedPos = spoofedPos;
    }

    public SpinoredBlockPos updatePosition() {
        return new SpinoredBlockPos(spinorEntity, spoofedPos);
    }

    public BlockState getBlockState() {
        return spinorEntity.blocks.get(spoofedPos).getBlockState();
    }

    public void setBlockState(BlockState newState) {
        spinorEntity.blocks.get(spoofedPos).updateBlockState(newState);
    }

    public BlockEntity getBlockEntity() {
        return spinorEntity.blocks.get(spoofedPos).getBlockEntity();
    }
}
