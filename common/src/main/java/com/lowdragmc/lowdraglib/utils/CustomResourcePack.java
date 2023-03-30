package com.lowdragmc.lowdraglib.utils;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.*;

/**
 * @author KilaBash
 * @date 2022/05/13
 * @implNote CustomResourcePack
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CustomResourcePack extends FolderPackResources {
    private final PackType type;
    private final String namespace;

    public CustomResourcePack(File location, String namespace, PackType type) {
        super(location);
        new File(location, "assets/" + namespace).mkdirs();
        this.namespace = namespace;
        this.type = type;
    }

    @Override
    protected InputStream getResource(String resourcePath) throws IOException {
        if ("pack.mcmeta".equals(resourcePath)) {
            return new ByteArrayInputStream("""
                    {
                        "pack": {
                            "description": "Generated resources for %s",
                            "pack_format": %d
                        }
                    }
                    """.formatted(namespace, type.getVersion(SharedConstants.getCurrentVersion())).getBytes());
        } else {
            return super.getResource(resourcePath);
        }
    }
}
