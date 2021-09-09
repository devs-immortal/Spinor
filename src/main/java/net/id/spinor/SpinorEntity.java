package net.id.spinor;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;

public class SpinorEntity extends InanimateEntity {

    public HashMap<BlockPos, SpinoredBlockStorage> blocks = new HashMap<>();

    public SpinorEntity(EntityType<SpinorEntity> spinorEntityEntityType, World world) {
        super(spinorEntityEntityType,world);
    }

    public SpinorEntity(World world, double x, double y, double z) {
        super(SpinorMod.SPINOR_ENTITY_TYPE, world);
        this.setPosition(x, y + (double) ((1.0F - this.getHeight()) / 2.0F), z);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.setRotation(180,this.getPitch());
    }

    public void grabAllBlocks(BlockPos pos){
        addBlocks(Sets.newHashSet(),pos);
    }
    private void addBlocks(HashSet<BlockPos> touched, BlockPos pos) {
        if (touched.size() > 40 || touched.contains(pos)) return;

        touched.add(pos);

        if(addBlock(pos)) {
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
            this.blocks.put(relativePos, spinoredBlockStorage);
            this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
            return true;
        }
        return false;
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

    public void tick() {
        super.tick();
        // setRotation(getYaw() +1,getPitch());
        blocks.forEach((blockPos, spinoredBlockStorage) -> {
            spinoredBlockStorage.createFakeBlockPos(this, blockPos);
            spinoredBlockStorage.tickBlockEntity(world);
            if (world.isClient) {
                spinoredBlockStorage.clientDisplayTick(world);
            }
        });
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        NbtList links = (NbtList) nbt.get("blocks");
        if (links != null)
            for (int i = 0; i < links.size(); i++) {
                NbtCompound link = links.getCompound(i);
                BlockPos pos = BlockPos.fromLong(link.getLong("blockpos"));
                this.blocks.put(pos, SpinoredBlockStorage.fromNBT(link, this, pos));
            }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        NbtList links = new NbtList();
        this.blocks.forEach((blockPos, spinoredBlockStorage) ->{
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putLong("blockpos",blockPos.asLong());
            spinoredBlockStorage.writeCustomDataToNbt(nbtCompound);
            links.add(nbtCompound);
        });
        nbt.put("blocks",links);
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
        this.blocks = ((SpinorSpawnPacket) packet).getBlocks();
        this.setPosition(d, e + (double) ((1.0F - this.getHeight()) / 2.0F), f);

    }
}