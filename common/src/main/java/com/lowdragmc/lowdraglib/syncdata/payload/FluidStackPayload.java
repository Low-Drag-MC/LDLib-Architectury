package com.lowdragmc.lowdraglib.syncdata.payload;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;

public class FluidStackPayload extends ObjectTypedPayload<FluidStack> {

    @Override
    public void writePayload(FriendlyByteBuf buf) {
        buf.writeFluidStack(payload);
    }

    @Override
    public void readPayload(FriendlyByteBuf buf) {
        payload = buf.readFluidStack();
    }

    @Override
    public Tag serializeNBT() {
        return payload.writeToNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(Tag tag) {
        payload = FluidStack.loadFluidStackFromNBT((CompoundTag) tag);
    }
}

