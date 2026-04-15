package org.btwr.vegehenna.entity.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class WeedsBlockEntity extends BlockEntity {

    private int level = 0;

    public WeedsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WEEDED_FARMLAND, pos, state);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        int clamped = Math.clamp(level, 0, 7);

        if (this.level == clamped) return; // avoid useless updates

        this.level = clamped;
        markDirty();

        if (world != null && !world.isClient) {
            // Update the renderer client-side
            world.updateListeners(pos, getCachedState(), getCachedState(), 0);
        }
    }

    public void removeWeeds() {
        setLevel(0);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("Level", level);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.level = nbt.getInt("Level");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

}