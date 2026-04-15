package org.btwr.vegehenna.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.shared_library.util.utils.IdUtils;
import org.btwr.vegehenna.block.ModBlocks;
import org.btwr.vegehenna.block.blocks.WeedsBlock;
import org.btwr.vegehenna.entity.block.WeedsBlockEntity;

public class WeedsBlockEntityRenderer implements BlockEntityRenderer<WeedsBlockEntity> {

    private final BlockRenderManager blockRenderManager;

    public WeedsBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.blockRenderManager = ctx.getRenderManager();
    }

    @Override
    public void render(WeedsBlockEntity entity,
                       float tickDelta,
                       MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers,
                       int light,
                       int overlay
    )
    {

        World world = entity.getWorld();
        if (world == null) return;

        int growth = entity.getLevel();
        int level = Math.min(growth / 2, 3);

        if (level <= 0) return;

        BlockPos farmlandPos = entity.getPos();
        BlockPos cropPos = farmlandPos.up();

        matrices.push();

        // Move render space to the crop position
        matrices.translate(
                cropPos.getX() - farmlandPos.getX(),
                cropPos.getY() - farmlandPos.getY(),
                cropPos.getZ() - farmlandPos.getZ()
        );

        BlockState renderState = ModBlocks.WEEDS.getDefaultState()
                .with(WeedsBlock.LEVEL, level);

        Identifier modelId = IdUtils.ofVG("block/weeds_" + level);
        BakedModel model = MinecraftClient.getInstance()
                .getBakedModelManager()
                .getModel(modelId);

        blockRenderManager.getModelRenderer().render(
                world,
                model,
                renderState,
                cropPos,
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getCutout()),
                false,
                world.random,
                42,
                overlay
        );

        matrices.pop();
    }
}