package com.lowdragmc.lowdraglib.utils;

import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/05/13
 * @implNote CustomResourcePack
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CustomResourcePack implements RepositorySource {
    private final File location;
    private final PackSource packSource;

    public CustomResourcePack(File location, PackSource packSource, String namespace, String description, int format) {
        this.location = location;
        this.packSource = packSource;

        new File(location, "assets/" + namespace).mkdirs();
        File mcmeta = new File(location, "pack.mcmeta");
        if (!mcmeta.exists()) {
            JsonObject meta = new JsonObject();
            JsonObject pack = new JsonObject();
            meta.add("pack", pack);
            pack.addProperty("description", description);
            pack.addProperty("pack_format", format);
            FileUtility.saveJson(mcmeta, meta);
        }

    }

    public CustomResourcePack(File location, PackSource packSource, String namespace, JsonObject meta) {
        this.location = location;
        this.packSource = packSource;

        new File(location, "assets/" + namespace).mkdirs();
        File mcmeta = new File(location, "pack.mcmeta");
        if (!mcmeta.exists()) {
            FileUtility.saveJson(mcmeta, meta);
        }
    }

    private static final FileFilter RESOURCEPACK_FILTER = (file) -> {
        boolean flag = file.isFile() && file.getName().endsWith(".zip");
        boolean flag1 = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();
        return flag || flag1;
    };

    private Supplier<PackResources> createSupplier(File pFile) {
        return pFile.isDirectory() ? () -> new FolderPackResources(pFile) : () -> new FilePackResources(pFile);
    }

    @Override
    public void loadPacks(Consumer<Pack> pInfoConsumer, Pack.PackConstructor pInfoFactory) {
        if (!this.location.isDirectory()) {
            this.location.mkdirs();
        }

        if (RESOURCEPACK_FILTER.accept(location)) {
            String s = "file/" + location.getName();
            Pack resourcepackinfo = Pack.create(s, true, this.createSupplier(location), pInfoFactory, Pack.Position.TOP, this.packSource);
            if (resourcepackinfo != null) {
                pInfoConsumer.accept(resourcepackinfo);
            }
        }
    }
}
