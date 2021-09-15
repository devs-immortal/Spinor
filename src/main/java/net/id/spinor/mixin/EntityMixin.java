package net.id.spinor.mixin;

import net.id.spinor.SpinorPassenger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements SpinorPassenger {

    @Shadow
    public abstract void move(MovementType movementType, Vec3d movement);

    Vec3d spinorVelocity = Vec3d.ZERO;

    @Inject(method = "move", at = @At(value = "HEAD"), cancellable = true)
    public void injectVelocity(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        if (!spinorVelocity.equals(Vec3d.ZERO)) {
            Vec3d newVel = movement.add(spinorVelocity);
            spinorVelocity = Vec3d.ZERO;
            this.move(movementType, newVel);
            ci.cancel();
        }
    }

    @Override
    public void addSpinorVelocity(Vec3d spinorVec) {
        this.spinorVelocity = spinorVec;
    }
}
