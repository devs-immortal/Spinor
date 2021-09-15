package net.id.spinor;


import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class SpinorChildEntity extends SpinorEntityBase {

    public SpinorChildEntity(EntityType<SpinorChildEntity> spinorChildEntityEntityType, World world) {
        super(spinorChildEntityEntityType, world);
    }

    public SpinorChildEntity(World world, SpinoredBlockStorage spinoredBlockStorage, double x, double y, double z) {
        super(SpinorMod.SPINOR_ENTITY_TYPE, world, x, y, z);
        this.spinoredBlockStorage = spinoredBlockStorage;
    }
}
