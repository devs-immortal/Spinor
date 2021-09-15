package net.id.spinor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpinorEntityBase extends InanimateEntity {
    SpinoredBlockStorage spinoredBlockStorage;

    public SpinorEntityBase(EntityType<? extends SpinorEntityBase> spinorEntityEntityType, World world) {
        super(spinorEntityEntityType, world);
    }

    public SpinorEntityBase(EntityType<? extends SpinorEntityBase> spinorEntityEntityType, World world, double x, double y, double z) {
        super(spinorEntityEntityType, world);
        this.setPosition(x, y + (double) ((1.0F - this.getHeight()) / 2.0F), z);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.setRotation(180, this.getPitch());
    }

    @Override
    public boolean collidesWith(Entity other) {
        return !(other instanceof SpinorEntityBase);
    }

    public SpinoredBlockStorage createSpinoredBlockStorage(BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (!blockState.isAir()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            SpinoredBlockStorage spinoredBlock = new SpinoredBlockStorage(blockState, blockEntity);
            spinoredBlock.createFakeBlockPos(this);
            this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
            return spinoredBlock;
        }
        return null;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (world.isClient) return ActionResult.PASS;

        BlockHitResult hit = new BlockHitResult(this.getPos(), Direction.UP, this.getBlockPos(), false);
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        } else if (this.spinoredBlockStorage != null) {
            spinoredBlockStorage.interact(world, player, hand, hit);
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public void tick() {
        super.tick();
        // setRotation(getYaw() +1,getPitch());
        spinoredBlockStorage.createFakeBlockPos(this);
        spinoredBlockStorage.tickBlockEntity(world);
        if (world.isClient)
            spinoredBlockStorage.clientDisplayTick(world);

        this.move(MovementType.SELF, getVelocity());
        world.getOtherEntities(this, Box.from(this.getPos().add(-.5, .5, -.5)), entity -> !(entity instanceof SpinorEntityBase)).forEach(entity -> {
            //entity.move(MovementType.SELF,this.getVelocity());
            ((SpinorPassenger) entity).addSpinorVelocity(this.getVelocity());
            //entity.setVelocity(this.getVelocity());
            //entity.addVelocity(getVelocity().x, getVelocity().y, getVelocity().z);
        });
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        NbtCompound spinoredBlock = nbt.getCompound("spinoredBlock");
        if (spinoredBlock != null) {
            spinoredBlockStorage = SpinoredBlockStorage.fromNBT(spinoredBlock, this);
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        NbtCompound nbtCompound = new NbtCompound();
        spinoredBlockStorage.writeCustomDataToNbt(nbtCompound);
        nbt.put("spinoredBlock", nbtCompound);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        BlockState blockState = Blocks.AIR.getDefaultState();
        if (spinoredBlockStorage != null)
            blockState = spinoredBlockStorage.getBlockState();
        return new EntitySpawnS2CPacket(this, Block.getRawIdFromState(blockState));
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        BlockState state = Block.getStateFromRawId(packet.getEntityData());
        spinoredBlockStorage = new SpinoredBlockStorage(state);
        this.setPosition(d, e + (double) ((1.0F - this.getHeight()) / 2.0F), f);
    }
}
