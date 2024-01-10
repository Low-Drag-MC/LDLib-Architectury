package com.lowdragmc.lowdraglib.client.shader.management;

import com.lowdragmc.lowdraglib.LDLib;
import com.mojang.blaze3d.platform.GlStateManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class Shader {
    public final ShaderType shaderType;
    public final String source;
    private int shaderId;
    private boolean isCompiled;

    public Shader(ShaderType type, String source) {
        this.shaderType = type;
        this.source = source;
        this.shaderId = GL20.glCreateShader(shaderType.shaderMode);
        if (this.shaderId == 0) {
            LDLib.LOGGER.error("GL Shader Allocation Fail!");
            throw new RuntimeException("GL Shader Allocation Fail!");
        }
    }

    public void attachShader(ShaderProgram program) {
        if (!isCompiled) compileShader();
        GlStateManager.glAttachShader(program.programId, this.shaderId);
    }

    public void deleteShader() {
        if (shaderId == 0) return;
        GlStateManager.glDeleteShader(this.shaderId);
        shaderId = 0;
    }

    public Shader compileShader() {
        if (!this.isCompiled && shaderId != 0) {
            GL20.glShaderSource(this.shaderId, source);
            GL20.glCompileShader(this.shaderId);
            if (GL20.glGetShaderi(this.shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                int maxLength = GL20.glGetShaderi(this.shaderId, GL20.GL_INFO_LOG_LENGTH);
                String error = String.format("Unable to compile %s shader object:\n%s", this.shaderType.name(), GL20.glGetShaderInfoLog(this.shaderId, maxLength));
                LDLib.LOGGER.error(error);
            }
            this.isCompiled = true;
        }
        return this;
    }

    public static Shader loadShader(ShaderType type, String rawShader) {
        return new Shader(type, rawShader).compileShader();
    }

    public static Shader loadShader(ShaderType type, ResourceLocation resourceLocation) throws IOException {
        var resource = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
        if (resource.isPresent()) {
            var iresource = resource.get();
            InputStream stream = iresource.open();
            StringBuilder sb = new StringBuilder();
            BufferedReader bin = new BufferedReader(new InputStreamReader(stream));
            String line;
            while((line = bin.readLine()) != null) {
                sb.append(line).append('\n');
            }
            stream.close();
            IOUtils.closeQuietly(stream);
            return loadShader(type, sb.toString());
        }
        throw new IOException("find no resource " + resourceLocation);
    }

    public enum ShaderType {
        VERTEX("vertex", ".vsh", 35633),
        FRAGMENT("fragment", ".fsh", 35632);

        public final String shaderName;
        public final String shaderExtension;
        public final int shaderMode;

        ShaderType(String shaderNameIn, String shaderExtensionIn, int shaderModeIn) {
            this.shaderName = shaderNameIn;
            this.shaderExtension = shaderExtensionIn;
            this.shaderMode = shaderModeIn;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) return true;
        if (obj instanceof Shader shader) {
            return Objects.equals(shader.source, this.source) && shader.shaderType == this.shaderType;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shaderType, source);
    }
}
