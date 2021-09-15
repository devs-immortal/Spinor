package net.id.spinor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class SpinorClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(SpinorMod.SPINOR_ENTITY_TYPE, SpinorEntityRenderer::new);
        EntityRendererRegistry.register(SpinorMod.SPINOR_CHILD_TYPE, SpinorEntityRenderer::new);
    }
}
