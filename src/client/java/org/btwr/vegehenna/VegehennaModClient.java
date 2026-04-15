package org.btwr.vegehenna;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import org.btwr.shared_library.util.utils.IdUtils;
import org.btwr.vegehenna.block.ModBlocks;
import org.btwr.vegehenna.render.WeedsBlockEntityRenderer;
import org.btwr.vegehenna.entity.block.ModBlockEntities;
import org.slf4j.Logger;

public class VegehennaModClient implements ClientModInitializer {

    public static final Logger LOGGER = VegehennaMod.LOGGER;

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FARMLAND_FERTILIZED, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CARROT_FLOWERING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SUGAR_CANE_ROOTS, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BREAD_DOUGH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WEEDS, RenderLayer.getCutout());

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) ->
                world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : -1, ModBlocks.SUGAR_CANE_ROOTS);

        BlockEntityRendererFactories.register(ModBlockEntities.WEEDED_FARMLAND, WeedsBlockEntityRenderer::new);

        ModelLoadingPlugin.register(pluginContext -> pluginContext.addModels(
                IdUtils.ofVG("block/weeds_1"),
                IdUtils.ofVG("block/weeds_2"),
                IdUtils.ofVG("block/weeds_3")
        ));

    }

}