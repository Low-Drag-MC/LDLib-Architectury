package com.lowdragmc.lowdraglib.syncdata;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Objects;

public class SyncUtils {
    public static boolean isChanged(@NotNull Object oldValue, @NotNull Object newValue) {
        if (oldValue instanceof ItemStack itemStack) {
            if (!(newValue instanceof ItemStack)) {
                return true;
            }
            return !ItemStack.matches(itemStack, (ItemStack) newValue);
        }
        if (oldValue instanceof FluidStack fluidStack) {
            if (!(newValue instanceof FluidStack newFluid)) {
                return true;
            }
            return !fluidStack.isFluidStackIdentical(newFluid);
        }

        return !oldValue.equals(newValue);
    }

    public static Object copyWhenNecessary(Object value) {
        if (value instanceof ItemStack itemStack) {
            return itemStack.copy();
        }
        if (value instanceof FluidStack fluidStack) {
            return fluidStack.copy();
        }
        if (value instanceof BlockPos blockPos) {
            return blockPos.immutable();
        }


        return value;
    }


    public static boolean isArrayLikeChanged(@NotNull Object oldValue, @NotNull Object newValue, int oldSize, boolean isArray) {
        if(isArray) {
            if(Array.getLength(newValue) != oldSize) {
                return true;
            }
            return !Objects.deepEquals(oldValue, newValue);
        }
        if (newValue instanceof Collection<?> collection) {
            if(collection.size() != oldSize) {
                return true;
            }
            var array = (Object[]) oldValue;
            int i = 0;
            for (var item : collection) {
                var oldItem = array[i++];
                 if ((oldItem == null && item != null) || (oldItem != null && item == null) || (oldItem != null && isChanged(oldItem, item))) {
                     return true;
                 }
            }
            return false;
        }
        throw new IllegalArgumentException("Value %s is not an array or collection".formatted(newValue));
    }

    public static Object copyArrayLike(Object value, boolean isArray) {
        if (isArray) {
            var componentType = value.getClass().getComponentType();
            if (componentType.isPrimitive()) {
                Object result = Array.newInstance(componentType, Array.getLength(value));
                System.arraycopy(value, 0, result, 0, Array.getLength(value));
                return result;
            }

            Object[] array = (Object[]) value;
            Object[] result = new Object[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = copyWhenNecessary(array[i]);
            }
            return result;

        }

        if (value instanceof Collection<?> collection) {
            Object[] result = new Object[collection.size()];
            int i = 0;
            for (Object o : collection) {
                result[i++] = copyWhenNecessary(o);
            }
            return result;
        }

        throw new IllegalArgumentException("Value %s is not an array or collection".formatted(value));
    }
}
