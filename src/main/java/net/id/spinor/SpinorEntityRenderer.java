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
public class SpinorEntityRenderer extends EntityRenderer<SpinorEntity> {
    public SpinorEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public void render(SpinorEntity fallingBlockEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        // matrixStack.translate(-0.5D, 0.0D, -0.5D);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - f));
        matrixStack.translate(-0.5D, 0.0D, -0.5D);
        //matrixStack.translate(-1D, 0.0D, -1D);
        fallingBlockEntity.spinoredBlockStorages.forEach((relativePos, spinoredBlockStorage) -> {
            matrixStack.translate(relativePos.getX(), relativePos.getY(), relativePos.getZ());
            if (spinoredBlockStorage.getBlockEntity() != null)
                MinecraftClient.getInstance().getBlockEntityRenderDispatcher().render(spinoredBlockStorage.getBlockEntity(), f, matrixStack, vertexConsumerProvider);
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(spinoredBlockStorage.getBlockState(), matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
            matrixStack.translate(-relativePos.getX(), -relativePos.getY(), -relativePos.getZ());
        });
        matrixStack.pop();
        super.render(fallingBlockEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public Identifier getTexture(SpinorEntity fallingBlockEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
