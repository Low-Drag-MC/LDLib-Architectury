package com.lowdragmc.lowdraglib.fabric.core.mixin.accessor;

import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

/**
 * @author KilaBash
 * @date 2022/05/02
 * @implNote PackRepositoryMixin, inject resource packs
 */
@Mixin(PackRepository.class)
public interface PackRepositoryMixin {
    @Accessor Set<RepositorySource> getSources();
}
