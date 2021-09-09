package net.id.spinor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class InanimateEntity extends Entity {
    public InanimateEntity(EntityType<?> type, World world) {
        super(type, world);
        this.inanimate = true;
    }

    public InanimateEntity(World world, double x, double y, double z) {
        this(SpinorMod.SPINOR_ENTITY_TYPE, world);
        this.setPosition(x, y + (double) ((1.0F - this.getHeight()) / 2.0F), z);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return null;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean collidesWith(Entity other) {
        return true;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
