package net.id.spinor;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;

public class SpinorEntity extends InanimateEntity {

    //BlockPos is a pos relative to the center of the entity. Ie 0,0,0 is the center, and a block above that would be 0,1,0
    public HashMap<BlockPos, SpinoredBlockStorage> spinoredBlockStorages = new HashMap<>();

    public SpinorEntity(EntityType<SpinorEntity> spinorEntityEntityType, World world) {
        super(spinorEntityEntityType, world);
    }

    public SpinorEntity(World world, double x, double y, double z) {
        super(SpinorMod.SPINOR_ENTITY_TYPE, world);
        this.setPosition(x, y + (double) ((1.0F - this.getHeight()) / 2.0F), z);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.setRotation(180, this.getPitch());
    }

    public void grabAllBlocks(BlockPos pos) {
        addBlocks(Sets.newHashSet(), pos);
    }

    public void addBlocks(HashSet<BlockPos> touched, BlockPos pos) {
        if (touched.size() > 40 || touched.contains(pos)) return;

        touched.add(pos);

        if (addBlock(pos)) {
            addBlocks(touched, pos.up());
            addBlocks(touched, pos.down());
            addBlocks(touched, pos.west());
            addBlocks(touched, pos.east());
            addBlocks(touched, pos.north());
            addBlocks(touched, pos.south());
        }
    }

    public boolean addBlock(BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (!blockState.isAir()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            BlockPos relativePos = pos.subtract(this.getBlockPos());
            SpinoredBlockStorage spinoredBlockStorage = new SpinoredBlockStorage(blockState, blockEntity);
            spinoredBlockStorage.createFakeBlockPos(this, relativePos);
            this.spinoredBlockStorages.put(relativePos, spinoredBlockStorage);
            this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
            return true;
        }
        return false;
    }

    public SpinoredBlockStorage getSpinoredBlock(BlockPos relativePos) {
        return spinoredBlockStorages.get(relativePos);
    }

    //todo this does not work. Bounding boxes are limited to rectangles and are unable to rotate.
    @Override
    protected Box calculateBoundingBox() {
        Box box = new Box(this.getX() - .5d, this.getY(), this.getZ() - .5d, this.getX() + .5d, this.getY() + 1, this.getZ() + .5d);
        /*
        if (blocks != null && blocks.size() > 0)
            for (BlockPos blockPos : blocks.keySet()) {
                VoxelShape shape = blocks.get(blockPos).getBlockState().getCollisionShape(world, this.getBlockPos().add(blockPos)).offset(this.getX(), this.getY(), this.getZ());
                box = box.union(shape.getBoundingBox());
            }

         */
        return box;
    }
    @Override
    public boolean collidesWith(Entity other) {
        //other.move(MovementType.SELF, this.getVelocity());
        return true;
    }
    public void tick() {
        super.tick();
        // setRotation(getYaw() +1,getPitch());
        spinoredBlockStorages.forEach((relativePos, spinoredBlockStorage) -> {
            spinoredBlockStorage.createFakeBlockPos(this, relativePos);
            spinoredBlockStorage.tickBlockEntity(world);
            if (world.isClient) {
                spinoredBlockStorage.clientDisplayTick(world);
            }
        });
        this.move(MovementType.SELF, getVelocity());

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        NbtList links = (NbtList) nbt.get("spinoredBlocks");
        if (links != null)
            for (int i = 0; i < links.size(); i++) {
                NbtCompound link = links.getCompound(i);
                BlockPos relativePos = BlockPos.fromLong(link.getLong("relativePos"));
                this.spinoredBlockStorages.put(relativePos, SpinoredBlockStorage.fromNBT(link, this, relativePos));
            }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        NbtList links = new NbtList();
        this.spinoredBlockStorages.forEach((relativePos, spinoredBlockStorage) -> {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putLong("relativePos", relativePos.asLong());
            spinoredBlockStorage.writeCustomDataToNbt(nbtCompound);
            links.add(nbtCompound);
        });
        nbt.put("spinoredBlocks", links);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new SpinorSpawnPacket(this);
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        this.spinoredBlockStorages = ((SpinorSpawnPacket) packet).getBlocks();
        this.setPosition(d, e + (double) ((1.0F - this.getHeight()) / 2.0F), f);

    }
}