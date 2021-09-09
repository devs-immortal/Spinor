package net.id.spinor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class SpinorSpawnPacket extends EntitySpawnS2CPacket {

    HashMap<BlockPos, SpinoredBlockStorage> blocks;

    public SpinorSpawnPacket(SpinorEntity entity) {
        super(entity);
        /*
        buf.writeVarInt(entity.getId());
        buf.writeUuid(entity.getUuid());
        buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.entityTypeId));
        buf.writeDouble(entity.getX());
        buf.writeDouble(entity.getY());
        buf.writeDouble(entity.getZ());
        buf.writeByte(entity.getPitch());
        buf.writeByte(entity.getYaw());
        buf.writeInt(this.entityData);
        Vec3d vel = ventity.getVelocity()
        buf.writeShort(entity.getVelocity());
        buf.writeShort(this.velocityY);
        buf.writeShort(this.velocityZ);
        this.blocks = entity.blocks;
         */
        this.blocks = entity.spinoredBlockStorages;
    }

    public SpinorSpawnPacket(PacketByteBuf buf) {
        super(buf);

        int numBlocks = buf.readInt();
        for (int i = 0; i < numBlocks; i++) {
            BlockPos relativePos = buf.readBlockPos();
            BlockState state = Block.getStateFromRawId(buf.readInt());
            blocks.put(relativePos, new SpinoredBlockStorage(state));
        }

    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeInt(blocks.size());
        blocks.forEach((relativePos, spinoredBlockStorage) -> {
            buf.writeBlockPos(relativePos);
            buf.writeInt(Block.getRawIdFromState(spinoredBlockStorage.getBlockState()));
        });
    }

    public HashMap<BlockPos, SpinoredBlockStorage> getBlocks() {
        return blocks;
    }
}