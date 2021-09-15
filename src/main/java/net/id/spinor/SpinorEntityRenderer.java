package net.id.spinor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class SpinorEntityRenderer extends EntityRenderer<SpinorEntityBase> {
    public SpinorEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public void render(SpinorEntityBase spinorEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        // matrixStack.translate(-0.5D, 0.0D, -0.5D);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - f));
        matrixStack.translate(-0.5D, 0.0D, -0.5D);
        //matrixStack.translate(-1D, 0.0D, -1D);
        if (spinorEntity.spinoredBlockStorage.getBlockEntity() != null)
            MinecraftClient.getInstance().getBlockEntityRenderDispatcher().render(spinorEntity.spinoredBlockStorage.getBlockEntity(), f, matrixStack, vertexConsumerProvider);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(spinorEntity.spinoredBlockStorage.getBlockState(), matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);

        matrixStack.pop();
        super.render(spinorEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public Identifier getTexture(SpinorEntityBase fallingBlockEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
