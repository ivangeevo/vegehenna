package org.ivangeevo.vegehenna.entity.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.ivangeevo.vegehenna.block.ModBlocks;

public class WeedsBlockEntity extends BlockEntity
{

    public WeedsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WEEDS, pos, state);
    }

    private int weedsLevel = 0;

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("WeedsLevel", weedsLevel);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        this.weedsLevel = nbt.getInt("WeedsLevel");
    }

}
