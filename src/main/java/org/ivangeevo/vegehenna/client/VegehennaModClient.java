package org.ivangeevo.vegehenna.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import org.ivangeevo.vegehenna.VegehennaMod;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.slf4j.Logger;

public class VegehennaModClient implements ClientModInitializer
{
    public static final Logger LOGGER = VegehennaMod.LOGGER;

    @Override
    public void onInitializeClient()
    {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FARMLAND_FERTILIZED, RenderLayer.getCutout());

    }
}
