package net.id.spinor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

public class SpinorClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(SpinorMod.SPINOR_ENTITY_TYPE, SpinorEntityRenderer::new);
    }
}
