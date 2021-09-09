package net.id.spinor.mixin;

import net.id.spinor.SpinoredBlockPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Shadow public abstract BlockPos getPos();

    @Inject(method = "toUpdatePacket",at = @At("RETURN"))
    public void stealUpdatePacket(CallbackInfoReturnable<BlockEntityUpdateS2CPacket> cir){
        if(this.getPos() instanceof SpinoredBlockPos) {
            System.out.println("we out here");
            System.out.println(cir.getReturnValue().getNbt());
        }
    }
}
