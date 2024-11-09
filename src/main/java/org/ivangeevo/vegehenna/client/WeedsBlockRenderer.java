package org.ivangeevo.vegehenna.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import org.ivangeevo.vegehenna.block.blocks.WeedsBlock;
import org.ivangeevo.vegehenna.entity.block.WeedsBlockEntity;
import org.ivangeevo.vegehenna.model.WeedsBlockModel;

@Environment(EnvType.CLIENT)
public class WeedsBlockRenderer implements BlockEntityRenderer<WeedsBlockEntity>
{
    @Override
    public void render(WeedsBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

    }

    /**
    @Override
    public void render(WeedsBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        // Get the growth level from the block state
        int growthLevel = state.get(WeedsBlock.WEEDS_LEVEL);

        // Choose the model based on growth level
        WeedsBlockModel model = getModelForGrowthLevel(growthLevel);

        // Apply necessary transformations and render the model
        matrices.push();
        renderWeedsModel(matrices, vertexConsumers, light, overlay, model);
        matrices.pop();

    }

    private WeedsBlockModel getModelForGrowthLevel(int growthLevel) {
        // Choose the correct model based on the growth level
        switch (growthLevel) {
            case 0: return WeedsBlockModel.getSmallModel();  // Stage 0 (initial)
            case 1: return WeedsBlockModel.getMediumModel(); // Stage 1
            case 2: return WeedsBlockModel.getLargeModel();  // Stage 2
            case 3: return WeedsBlockModel.getFullyGrownModel(); // Stage 3
            default: return WeedsBlockModel.getSmallModel();
        }
    }

    private void renderWeedsModel(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, WeedsBlockModel model) {
        // Render the model
        matrices.push();
        model.(matrices, vertexConsumers, light, overlay);
        RenderSystem.popMatrix();
    }
    **/


}
