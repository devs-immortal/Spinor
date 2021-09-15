package net.id.spinor;

import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;

public class SpinorHostEntity extends SpinorEntityBase {

    //BlockPos is a pos relative to the center of the entity. Ie 0,0,0 is the center, and a block above that would be 0,1,0
    public HashMap<BlockPos, SpinorChildEntity> childSpinors = new HashMap<>();

    public SpinorHostEntity(EntityType<SpinorHostEntity> spinorEntityEntityType, World world) {
        super(spinorEntityEntityType, world);
    }

    public SpinorHostEntity(World world, double x, double y, double z) {
        super(SpinorMod.SPINOR_ENTITY_TYPE, world, x, y, z);
    }

    public void createSpinorAt(BlockPos pos, BlockPos excludePos) {
        pos = pos.toImmutable();
        excludePos = excludePos.toImmutable();
        SpinoredBlockStorage spinoredBlock = createSpinoredBlockStorage(pos);
        if (spinoredBlock != null) {
            this.spinoredBlockStorage = spinoredBlock;
            HashSet<BlockPos> touched = Sets.newHashSet(excludePos, pos);
            addChildSpinors(touched, pos.up());
            addChildSpinors(touched, pos.down());
            addChildSpinors(touched, pos.west());
            addChildSpinors(touched, pos.east());
            addChildSpinors(touched, pos.north());
            addChildSpinors(touched, pos.south());
        }
    }

    public void addChildSpinors(HashSet<BlockPos> touched, BlockPos pos) {
        if (childSpinors.size() > 40 || touched.contains(pos) || !pos.isWithinDistance(this.getBlockPos(), 15)) return;

        touched.add(pos);
        SpinoredBlockStorage spinoredBlock = createSpinoredBlockStorage(pos);
        if (spinoredBlock != null) {
            SpinorChildEntity childSpinor = new SpinorChildEntity(world, spinoredBlock, pos.getX() + .5d, pos.getY(), pos.getZ() + .5d);
            BlockPos relativePos = pos.subtract(this.getBlockPos());
            this.childSpinors.put(relativePos, childSpinor);
            this.world.spawnEntity(childSpinor);

            addChildSpinors(touched, pos.up());
            addChildSpinors(touched, pos.down());
            addChildSpinors(touched, pos.west());
            addChildSpinors(touched, pos.east());
            addChildSpinors(touched, pos.north());
            addChildSpinors(touched, pos.south());
        }
    }

    @Override
    public boolean collidesWith(Entity other) {
        return !(other instanceof SpinorEntityBase);
        //other.move(MovementType.SELF, this.getVelocity());
    }

    @Override
    public void setVelocity(Vec3d velocity) {
        super.setVelocity(velocity);
        if (childSpinors != null)
            childSpinors.forEach((relativePos, spinorChildEntity) -> {
                spinorChildEntity.setVelocity(velocity);
            });
    }

    @Override
    public void setYaw(float yaw) {
        yaw = yaw % 360.0F;
        float yawDelta = yaw - getYaw();

        super.setYaw(yaw);

        if (childSpinors != null) {
            childSpinors.forEach((relativePos, spinorChildEntity) -> {
                Pair<Double, Double> xz = getRotatedXZ(relativePos.getX(), relativePos.getZ(), (int) this.getYaw());
                spinorChildEntity.refreshPositionAndAngles(this.getX() + xz.getLeft(), this.getY() + relativePos.getY(), this.getZ() + xz.getRight(), this.getYaw(), this.getPitch());

                world.getOtherEntities(this, Box.from(spinorChildEntity.getPos().add(-.5, .5, -.5)), entity -> !(entity instanceof SpinorEntityBase)).forEach(entity -> {
                    Vec3d relativeEntityPos = this.getPos().subtract(entity.getPos());
                    Pair<Double, Double> entityxz = getRotatedXZ(relativeEntityPos.x, relativeEntityPos.z, (int) yawDelta);
                    entity.refreshPositionAndAngles(this.getX() + entityxz.getLeft(), entity.getPos().y, this.getZ() + entityxz.getRight(), yawDelta, entity.getPitch());
                    //entity.teleport(this.getX() + entityxz.getLeft(), entity.getPos().y, this.getZ() + entityxz.getRight());

                });
            });
        }
        world.getOtherEntities(this, Box.from(this.getPos().add(-.5, .5, -.5)), entity -> !(entity instanceof SpinorEntityBase)).forEach(entity -> {
            entity.setYaw(entity.getYaw() + yawDelta);
        });
    }

    //trig function to calculate new position after being rotated. x,z being start position relative to pivot point, yaw being degrees to rotate.
    public static Pair<Double, Double> getRotatedXZ(double x, double z, int yaw) {
        if (yaw == 0) return new Pair<>(x, z);
        if (yaw == 90) return new Pair<>(z, x);
        if (yaw == 180) return new Pair<>(-x, -z);
        if (yaw == 270) return new Pair<>(-z, -x);
        double f = Math.sin(Math.toRadians(yaw));
        double g = Math.cos(Math.toRadians(yaw));
        return new Pair<>(g * -x - f * -z, f * -x + g * -z);
    }

    public void tick() {
        super.tick();
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

    }
}