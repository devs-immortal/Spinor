package net.id.spinor;


import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SpinoredBlockPos extends BlockPos {
    private final SpinorEntityBase spinorEntity;

    public SpinoredBlockPos(SpinorEntityBase spinorEntity) {
        super(spinorEntity.getBlockPos());
        this.spinorEntity = spinorEntity;
    }

    public BlockState getBlockState() {
        return spinorEntity.spinoredBlockStorage.getBlockState();
    }

    public void setBlockState(BlockState newState) {
        spinorEntity.spinoredBlockStorage.updateBlockState(newState);
    }

    public BlockEntity getBlockEntity() {
        return spinorEntity.spinoredBlockStorage.getBlockEntity();
    }
}
