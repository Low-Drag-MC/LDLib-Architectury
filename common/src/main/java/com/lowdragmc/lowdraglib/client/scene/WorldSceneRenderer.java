package com.lowdragmc.lowdraglib.client.scene;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.client.shader.management.ShaderManager;
import com.lowdragmc.lowdraglib.client.utils.glu.Project;
import com.lowdragmc.lowdraglib.utils.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.PlatformOnly;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static net.minecraft.world.level.block.RenderShape.INVISIBLE;


/**
 * @author KilaBash
 * @date 2022/05/25
 * @implNote  render a scene, through VBO compilation scene, greatly optimize rendering performance.
 */
@SuppressWarnings("ALL")
@Environment(EnvType.CLIENT)
public abstract class WorldSceneRenderer {
    protected static final FloatBuffer MODELVIEW_MATRIX_BUFFER = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    protected static final FloatBuffer PROJECTION_MATRIX_BUFFER = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    protected static final IntBuffer VIEWPORT_BUFFER = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
    protected static final FloatBuffer PIXEL_DEPTH_BUFFER = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    protected static final FloatBuffer OBJECT_POS_BUFFER = ByteBuffer.allocateDirect(3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    enum CacheState {
        UNUSED,
        NEED,
        COMPILING,
        COMPILED
    }

    public final Level world;
    public final Map<Collection<BlockPos>, ISceneRenderHook> renderedBlocksMap;
    protected VertexBuffer[] vertexBuffers;
    protected Set<BlockPos> tileEntities;
    protected boolean useCache;
    protected boolean ortho;
    protected AtomicReference<CacheState> cacheState;
    protected int maxProgress;
    protected int progress;
    protected Thread thread;
    protected ParticleManager particleManager;
    protected Camera camera;
    protected CameraEntity cameraEntity;
    private Consumer<WorldSceneRenderer> beforeRender;
    private Consumer<WorldSceneRenderer> afterRender;
    private Consumer<BlockHitResult> onLookingAt;
    protected int clearColor;
    private BlockHitResult lastTraceResult;
    private Set<BlockPos> blocked;
    private Vector3f eyePos = new Vector3f(0, 0, 10f);
    private Vector3f lookAt = new Vector3f(0, 0, 0);
    private Vector3f worldUp = new Vector3f(0, 1, 0);
    private Matrix4f lastProject;
    private float fov = 60f;
    private float minX, maxX, minY, maxY, minZ, maxZ;

    public WorldSceneRenderer(Level world) {
        this.world = world;
        renderedBlocksMap = new LinkedHashMap<>();
        cacheState = new AtomicReference<>(CacheState.UNUSED);
    }

    public WorldSceneRenderer setParticleManager(ParticleManager particleManager) {
        if (particleManager == null) {
            this.particleManager = null;
            this.camera = null;
            this.cameraEntity = null;
            return this;
        }
        this.particleManager = particleManager;
        if (this.world instanceof DummyWorld dummyWorld) {
            dummyWorld.setParticleManager(particleManager);
        }
        this.particleManager.setLevel(world);
        this.camera = new Camera();
        this.cameraEntity = new CameraEntity(world);
        setCameraLookAt(eyePos, lookAt, worldUp);
        return this;
    }

    public ParticleManager getParticleManager() {
        return particleManager;
    }

    public WorldSceneRenderer useCacheBuffer(boolean useCache) {
        if (this.useCache || !Minecraft.getInstance().isSameThread() || LDLib.isModLoaded(LDLib.MODID_RUBIDIUM)) return this;
        deleteCacheBuffer();
        if (useCache) {
            List<RenderType> layers = RenderType.chunkBufferLayers();
            this.vertexBuffers = new VertexBuffer[layers.size()];
            for (int j = 0; j < layers.size(); ++j) {
                this.vertexBuffers[j] = new VertexBuffer();
            }
            if (cacheState.get() == CacheState.COMPILING && thread != null) {
                thread.interrupt();
                thread = null;
            }
            cacheState.set(CacheState.NEED);
        }
        this.useCache = useCache;
        return this;
    }

    public WorldSceneRenderer useOrtho(boolean ortho) {
        this.ortho = ortho;
        return this;
    }

    public WorldSceneRenderer deleteCacheBuffer() {
        if (useCache) {
            for (int i = 0; i < RenderType.chunkBufferLayers().size(); ++i) {
                if (this.vertexBuffers[i] != null) {
                    this.vertexBuffers[i].close();
                }
            }
            if (cacheState.get() == CacheState.COMPILING && thread != null) {
                thread.interrupt();
                thread = null;
            }
        }
        this.tileEntities = null;
        useCache = false;
        cacheState.set(CacheState.UNUSED);
        return this;
    }

    public WorldSceneRenderer needCompileCache() {
        if (cacheState.get() == CacheState.COMPILING && thread != null) {
            thread.interrupt();
            thread = null;
        }
        cacheState.set(CacheState.NEED);
        return this;
    }

    public WorldSceneRenderer setBeforeWorldRender(Consumer<WorldSceneRenderer> callback) {
        this.beforeRender = callback;
        return this;
    }

    public WorldSceneRenderer setAfterWorldRender(Consumer<WorldSceneRenderer> callback) {
        this.afterRender = callback;
        return this;
    }

    public WorldSceneRenderer addRenderedBlocks(Collection<BlockPos> blocks, ISceneRenderHook renderHook) {
        if (blocks != null) {
            this.renderedBlocksMap.put(blocks, renderHook);
        }
        return this;
    }

    public WorldSceneRenderer setBlocked(Set<BlockPos> blocked) {
        this.blocked = blocked;
        return this;
    }

    public WorldSceneRenderer setOnLookingAt(Consumer<BlockHitResult> onLookingAt) {
        this.onLookingAt = onLookingAt;
        return this;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setClearColor(int clearColor) {
        this.clearColor = clearColor;
    }

    public BlockHitResult getLastTraceResult() {
        return lastTraceResult;
    }

    public void render(PoseStack poseStack, float x, float y, float width, float height, int mouseX, int mouseY) {
        // setupCamera
        var pose = poseStack.last().pose();
        Vector4f pos = new Vector4f(x, y, 0, 1.0F);
        pos.transform(pose);
        Vector4f size = new Vector4f(x + width, y + height, 0, 1.0F);
        size.transform(pose);
        x = pos.x();
        y = pos.y();
        width = size.x() - x;
        height = size.y() - y;
        PositionedRect positionedRect = getPositionedRect((int)x, (int)y, (int)width, (int)height);
        PositionedRect mouse = getPositionedRect(mouseX, mouseY, 0, 0);
        mouseX = mouse.position.x;
        mouseY = mouse.position.y;
        setupCamera(positionedRect);
        // render TrackedDummyWorld
        drawWorld();
        // check lookingAt
        this.lastTraceResult = null;
        if (onLookingAt != null && mouseX > positionedRect.position.x && mouseX < positionedRect.position.x + positionedRect.size.width
                && mouseY > positionedRect.position.y && mouseY < positionedRect.position.y + positionedRect.size.height) {
            Vector3f hitPos = unProject(mouseX, mouseY);
            BlockHitResult result = rayTrace(hitPos);
            if (result != null) {
                this.lastTraceResult = null;
                this.lastTraceResult = result;
                onLookingAt.accept(result);
            }
        }
        // resetCamera
        resetCamera();
    }

    public Vector3f getEyePos() {
        return eyePos;
    }

    public Vector3f getLookAt() {
        return lookAt;
    }

    public Vector3f getWorldUp() {
        return worldUp;
    }

    public void setCameraLookAt(Vector3f eyePos, Vector3f lookAt, Vector3f worldUp) {
        this.eyePos = eyePos;
        this.lookAt = lookAt;
        this.worldUp = worldUp;
        if (camera != null) {
            Vector3 xzProduct = new Vector3(lookAt.x() - eyePos.x(), 0, lookAt.z() - eyePos.z());
            double angleYaw = Math.toDegrees(xzProduct.angle(Vector3.Z));
            if (xzProduct.angle(Vector3.X) < Math.PI / 2) {
                angleYaw = -angleYaw;
            }
            double anglePitch = Math.toDegrees(new Vector3(lookAt).subtract(new Vector3(eyePos)).angle(Vector3.Y)) - 90;
            cameraEntity.setPos(eyePos.x(), eyePos.y() - cameraEntity.getEyeHeight(), eyePos.z());
            cameraEntity.xo = cameraEntity.getX();
            cameraEntity.yo = cameraEntity.getY();
            cameraEntity.zo = cameraEntity.getZ();
            cameraEntity.setYRot((float) angleYaw);
            cameraEntity.setXRot((float) anglePitch);
            cameraEntity.yRotO = cameraEntity.getYRot();
            cameraEntity.xRotO = cameraEntity.getXRot();
        }
    }

    public void setCameraLookAt(Vector3f lookAt, double radius, double rotationPitch, double rotationYaw) {
        Vector3 vecX = new Vector3(Math.cos(rotationPitch), 0, Math.sin(rotationPitch));
        Vector3 vecY = new Vector3(0, Math.tan(rotationYaw) * vecX.mag(),0);
        Vector3 pos = vecX.copy().add(vecY).normalize().multiply(radius);
        setCameraLookAt(pos.add(lookAt.x(), lookAt.y(), lookAt.z()).vector3f(), lookAt, worldUp);
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public void setCameraOrtho(float x, float y, float z) {
        this.minX = -x;
        this.maxX = x;
        this.minY = -y;
        this.maxY = y;
        this.minZ = -z;
        this.maxZ = z;
    }

    public void setCameraOrtho(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    protected PositionedRect getPositionedRect(int x, int y, int width, int height) {
        return new PositionedRect(new Position(x, y), new Size(width, height));
    }

    protected void setupCamera(PositionedRect positionedRect) {
        int x = positionedRect.getPosition().x;
        int y = positionedRect.getPosition().y;
        int width = positionedRect.getSize().width;
        int height = positionedRect.getSize().height;

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();

        //setup viewport and clear GL buffers
        RenderSystem.viewport(x, y, width, height);

        RenderSystem.depthMask(true);
        clearView(x, y, width, height);

        //setup projection matrix to perspective
        lastProject = RenderSystem.getProjectionMatrix();

        float aspectRatio = width / (height * 1.0f);
        if (ortho) {
            RenderSystem.setProjectionMatrix(Matrix4f.orthographic(minX, maxX, maxY / aspectRatio, minY / aspectRatio, minZ, maxZ));
        } else {
            RenderSystem.setProjectionMatrix(Matrix4f.perspective(fov, aspectRatio, 0.1f, 10000.0f));
        }

        //setup modelview matrix
        PoseStack posesStack = RenderSystem.getModelViewStack();
        posesStack.pushPose();
        posesStack.setIdentity();
        Project.gluLookAt(posesStack, eyePos.x(), eyePos.y(), eyePos.z(), lookAt.x(), lookAt.y(), lookAt.z(), worldUp.x(), worldUp.y(), worldUp.z());
        RenderSystem.applyModelViewMatrix();

        RenderSystem.enableTexture();

        Minecraft mc = Minecraft.getInstance();
        RenderSystem.enableCull();

        if (camera != null) {
            camera.setup(world, cameraEntity, false, false, mc.getFrameTime());
        }
        ShaderManager.getInstance().setViewPort(positionedRect);

    }

    protected void clearView(int x, int y, int width, int height) {
        int i = (clearColor & 0xFF0000) >> 16;
        int j = (clearColor & 0xFF00) >> 8;
        int k = (clearColor & 0xFF);
        RenderSystem.clearColor(i / 255.0f, j / 255.0f, k / 255.0f, (clearColor >> 24) / 255.0f);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
    }

    protected void resetCamera() {
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);

        //reset viewport
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.viewport(0, 0, minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());

        //reset projection matrix
        RenderSystem.setProjectionMatrix(lastProject);
        
        //reset modelview matrix
        PoseStack posesStack = RenderSystem.getModelViewStack();
        posesStack.popPose();
        RenderSystem.applyModelViewMatrix();
        
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        ShaderManager.getInstance().clearViewPort();

    }

    protected void drawWorld() {
        if (beforeRender != null) {
            beforeRender.accept(this);
        }

        Minecraft mc = Minecraft.getInstance();

        float particleTicks = mc.getFrameTime();
        if (useCache) {
            renderCacheBuffer(mc, particleTicks);
        } else {
            BlockRenderDispatcher blockrendererdispatcher = mc.getBlockRenderer();
            try { // render com.lowdragmc.lowdraglib.test.block in each layer
                renderedBlocksMap.forEach((renderedBlocks, hook) -> {
                    for (RenderType layer : RenderType.chunkBufferLayers()) {
                        layer.setupRenderState();
                        Random random = new Random();
                        PoseStack matrixstack = new PoseStack();
                        if (hook != null) {
                            hook.apply(false, layer);
                        } else {
                            setDefaultRenderLayerState(layer);
                        }

                        if (layer == RenderType.translucent()) { // render tesr before translucent
                            if (hook != null) {
                                hook.apply(true, layer);
                            }
                            renderTESR(renderedBlocks, matrixstack, mc.renderBuffers().bufferSource(), hook, particleTicks);
                        }

                        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
                        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

                        renderBlocks(matrixstack, blockrendererdispatcher, layer, new VertexConsumerWrapper(buffer), renderedBlocks, hook, particleTicks);

                        Tesselator.getInstance().end();
                        layer.clearRenderState();
                    }
                });
            } finally {
            }
        }

        if (particleManager != null) {
            PoseStack poseStack = new PoseStack();
            poseStack.setIdentity();
            poseStack.translate(cameraEntity.getX(), cameraEntity.getY(), cameraEntity.getZ());
            particleManager.render(poseStack, camera, particleTicks);
        }

        if (afterRender != null) {
            afterRender.accept(this);
        }
    }

    public boolean isCompiling() {
        return cacheState.get() == CacheState.COMPILING;
    }

    public double getCompileProgress() {
        if (maxProgress > 1000) {
            return progress * 1. / maxProgress;
        }
        return 0;
    }

    private void renderCacheBuffer(Minecraft mc, float particleTicks) {
        List<RenderType> layers = RenderType.chunkBufferLayers();
        if (cacheState.get() == CacheState.NEED) {
            progress = 0;
            maxProgress = renderedBlocksMap.keySet().stream().map(Collection::size).reduce(0, Integer::sum) * (layers.size() + 1);
            thread = new Thread(()->{
                cacheState.set(CacheState.COMPILING);
                BlockRenderDispatcher blockrendererdispatcher = mc.getBlockRenderer();
                try { // render com.lowdragmc.lowdraglib.test.block in each layer
                    ModelBlockRenderer.enableCaching();
                    PoseStack matrixstack = new PoseStack();
                    for (int i = 0; i < layers.size(); i++) {
                        if (Thread.interrupted())
                            return;
                        RenderType layer = layers.get(i);
                        BufferBuilder buffer = new BufferBuilder(layer.bufferSize());
                        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
                        renderedBlocksMap.forEach((renderedBlocks, hook) -> {
                            renderBlocks(matrixstack, blockrendererdispatcher, layer, new VertexConsumerWrapper(buffer), renderedBlocks, hook, 0);
                        });
                        var builder = buffer.end();

                        var vertexBuffer = vertexBuffers[i];
                        Runnable toUpload = () -> {
                            if (!vertexBuffer.isInvalid()) {
                                vertexBuffer.bind();
                                vertexBuffer.upload(builder);
                                VertexBuffer.unbind();
                            }
                        };
                        CompletableFuture.runAsync(toUpload, runnable -> {
                            RenderSystem.recordRenderCall(runnable::run);
                        });

                    }
                    ModelBlockRenderer.clearCache();
                } finally {
                }
                Set<BlockPos> poses = new HashSet<>();
                renderedBlocksMap.forEach((renderedBlocks, hook) -> {
                    for (BlockPos pos : renderedBlocks) {
                        progress++;
                        if (Thread.interrupted())
                            return;
                        BlockEntity tile = world.getBlockEntity(pos);
                        if (tile != null) {
                            if (mc.getBlockEntityRenderDispatcher().getRenderer(tile) != null) {
                                poses.add(pos);
                            }
                        }
                    }
                });
                if (Thread.interrupted())
                    return;
                tileEntities = poses;
                cacheState.set(CacheState.COMPILED);
                thread = null;
                maxProgress = -1;
            });
            thread.start();
        } else {
            PoseStack matrixstack = new PoseStack();
            for (int i = 0; i < layers.size(); i++) {
                VertexBuffer vertexbuffer = vertexBuffers[i];
                if (vertexbuffer.isInvalid() || vertexbuffer.getFormat() == null) continue;

                RenderType layer = layers.get(i);
                if (layer == RenderType.translucent() && tileEntities != null) { // render tesr before translucent
                    renderTESR(tileEntities, matrixstack, mc.renderBuffers().bufferSource(), null, particleTicks);
                }

                layer.setupRenderState();

                matrixstack.pushPose();

                ShaderInstance shaderInstance = RenderSystem.getShader();

//                for(int j = 0; j < 12; ++j) {
//                    int k = RenderSystem.getShaderTexture(j);
//                    shaderInstance.setSampler("Sampler" + j, k);
//                }

                // setup shader uniform
                if (shaderInstance.MODEL_VIEW_MATRIX != null) {
                    shaderInstance.MODEL_VIEW_MATRIX.set(RenderSystem.getModelViewMatrix());
                }

                if (shaderInstance.PROJECTION_MATRIX != null) {
                    shaderInstance.PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());
                }

                if (shaderInstance.COLOR_MODULATOR != null) {
                    shaderInstance.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
                }

                if (shaderInstance.FOG_START != null) {
                    shaderInstance.FOG_START.set(RenderSystem.getShaderFogStart());
                }

                if (shaderInstance.FOG_END != null) {
                    shaderInstance.FOG_END.set(RenderSystem.getShaderFogEnd());
                }

                if (shaderInstance.FOG_COLOR != null) {
                    shaderInstance.FOG_COLOR.set(RenderSystem.getShaderFogColor());
                }

                if (shaderInstance.FOG_SHAPE != null) {
                    shaderInstance.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
                }

                if (shaderInstance.TEXTURE_MATRIX != null) {
                    shaderInstance.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
                }

                if (shaderInstance.GAME_TIME != null) {
                    shaderInstance.GAME_TIME.set(RenderSystem.getShaderGameTime());
                }

                RenderSystem.setupShaderLights(shaderInstance);
                shaderInstance.apply();

                setDefaultRenderLayerState(layer);

                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                vertexbuffer.bind();
                vertexbuffer.draw();

                matrixstack.popPose();

                shaderInstance.clear();
                VertexBuffer.unbind();
                layer.clearRenderState();
            }
        }
    }

    private void renderBlocks(PoseStack matrixStack, BlockRenderDispatcher blockrendererdispatcher, RenderType layer, VertexConsumerWrapper wrapperBuffer, Collection<BlockPos> renderedBlocks, @Nullable ISceneRenderHook hook, float partialTicks) {
        for (BlockPos pos : renderedBlocks) {
            if (blocked != null && blocked.contains(pos)) {
                continue;
            }
            BlockState state = world.getBlockState(pos);
            FluidState fluidState = state.getFluidState();
            Block block = state.getBlock();
            BlockEntity te = world.getBlockEntity(pos);

            if (hook != null) {
                hook.applyVertexConsumerWrapper(world, pos, state, wrapperBuffer, layer, partialTicks);
            }

            if (block == Blocks.AIR) continue;
            if (state.getRenderShape() != INVISIBLE && canRenderInLayer(state, layer)) {
                matrixStack.pushPose();
                matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
                if (Platform.isForge()) {
                    renderBlocksForge(blockrendererdispatcher, state, pos, world, matrixStack, wrapperBuffer, world.random, layer);
                } else {
                    blockrendererdispatcher.renderBatched(state, pos, world, matrixStack, wrapperBuffer, false, world.random);
                }
                matrixStack.popPose();
            }
            if (!fluidState.isEmpty() && ItemBlockRenderTypes.getRenderLayer(fluidState) == layer) { // I dont want to do this fxxk wrapper
                wrapperBuffer.addOffset((pos.getX() - (pos.getX() & 15)), (pos.getY() - (pos.getY() & 15)), (pos.getZ() - (pos.getZ() & 15)));
                blockrendererdispatcher.renderLiquid(pos, world, wrapperBuffer, state, fluidState);
            }
            wrapperBuffer.clerOffset();
            wrapperBuffer.clearColor();
            if (maxProgress > 0) {
                progress++;
            }
        }
    }

    @ExpectPlatform
    public static boolean canRenderInLayer(BlockState state, RenderType renderType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    @PlatformOnly(PlatformOnly.FORGE)
    public static void renderBlocksForge(BlockRenderDispatcher blockRenderDispatcher, BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, RandomSource random, RenderType renderType) {
        throw new AssertionError();
    }

    private void renderTESR(Collection<BlockPos> poses, PoseStack matrixStack, MultiBufferSource.BufferSource buffers, @Nullable ISceneRenderHook hook, float partialTicks) {
        if (buffers == null) return;
        for (BlockPos pos : poses) {
            BlockEntity tile = world.getBlockEntity(pos);
            if (tile != null) {
                matrixStack.pushPose();
                matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
                BlockEntityRenderer<BlockEntity> tileentityrenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(tile);
                if (tileentityrenderer != null) {
                    if (tile.hasLevel() && tile.getType().isValid(tile.getBlockState())) {
                        Level world = tile.getLevel();
                        if (hook != null) {
                            hook.applyBESR(world, pos, tile, matrixStack, partialTicks);
                        }
                        tileentityrenderer.render(tile, partialTicks, matrixStack, buffers, 0xF000F0, OverlayTexture.NO_OVERLAY);
                    }
                }
                matrixStack.popPose();
            }
        }
        buffers.endBatch();
    }

    public static void setDefaultRenderLayerState(RenderType layer) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        if (layer == RenderType.translucent()) { // SOLID
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(false);
        } else { // TRANSLUCENT
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
        }
    }

    public BlockHitResult rayTrace(Vector3f hitPos) {
        Vec3 startPos = new Vec3(this.eyePos.x(), this.eyePos.y(), this.eyePos.z());
        if (ortho) {
            startPos = startPos.add(new Vec3(startPos.x - lookAt.x(), startPos.y - lookAt.y(), startPos.z - lookAt.z()).multiply(500, 500, 500));
        }
        hitPos.mul(2); // Double view range to ensure pos can be seen.
        Vec3 endPos = new Vec3((hitPos.x() - startPos.x), (hitPos.y() - startPos.y), (hitPos.z() - startPos.z));
        try {
            return this.world.clip(new ClipContext(startPos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null));
        } catch (Exception e) {
            return null;
        }
    }

    public Vector3f project(Vector3f pos) {
        //read current rendering parameters
        RenderSystem.getModelViewMatrix().store(MODELVIEW_MATRIX_BUFFER);
        RenderSystem.getProjectionMatrix().store(PROJECTION_MATRIX_BUFFER);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, VIEWPORT_BUFFER);

        //rewind buffers after write by OpenGL glGet calls
        MODELVIEW_MATRIX_BUFFER.rewind();
        PROJECTION_MATRIX_BUFFER.rewind();
        VIEWPORT_BUFFER.rewind();

        //call gluProject with retrieved parameters
        Project.gluProject(pos.x(), pos.y(), pos.z(), MODELVIEW_MATRIX_BUFFER, PROJECTION_MATRIX_BUFFER, VIEWPORT_BUFFER, OBJECT_POS_BUFFER);

        //rewind buffers after read by gluProject
        VIEWPORT_BUFFER.rewind();
        PROJECTION_MATRIX_BUFFER.rewind();
        MODELVIEW_MATRIX_BUFFER.rewind();

        //rewind buffer after write by gluProject
        OBJECT_POS_BUFFER.rewind();

        //obtain position in Screen
        float winX = OBJECT_POS_BUFFER.get();
        float winY = OBJECT_POS_BUFFER.get();
        float winZ = OBJECT_POS_BUFFER.get();

        //rewind buffer after read
        OBJECT_POS_BUFFER.rewind();

        return new Vector3f(winX, winY, winZ);
    }

    public Vector3f unProject(int mouseX, int mouseY) {
        //read depth of pixel under mouse
        GL11.glReadPixels(mouseX, mouseY, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, PIXEL_DEPTH_BUFFER);

        //rewind buffer after write by glReadPixels
        PIXEL_DEPTH_BUFFER.rewind();

        //retrieve depth from buffer (0.0-1.0f)
        float pixelDepth = PIXEL_DEPTH_BUFFER.get();

        //rewind buffer after read
        PIXEL_DEPTH_BUFFER.rewind();

        //read current rendering parameters
        RenderSystem.getModelViewMatrix().store(MODELVIEW_MATRIX_BUFFER);
        RenderSystem.getProjectionMatrix().store(PROJECTION_MATRIX_BUFFER);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, VIEWPORT_BUFFER);

        //rewind buffers after write by OpenGL glGet calls
        MODELVIEW_MATRIX_BUFFER.rewind();
        PROJECTION_MATRIX_BUFFER.rewind();
        VIEWPORT_BUFFER.rewind();

        //call gluUnProject with retrieved parameters
        Project.gluUnProject(mouseX, mouseY, pixelDepth, MODELVIEW_MATRIX_BUFFER, PROJECTION_MATRIX_BUFFER, VIEWPORT_BUFFER, OBJECT_POS_BUFFER);

        //rewind buffers after read by gluUnProject
        VIEWPORT_BUFFER.rewind();
        PROJECTION_MATRIX_BUFFER.rewind();
        MODELVIEW_MATRIX_BUFFER.rewind();

        //rewind buffer after write by gluUnProject
        OBJECT_POS_BUFFER.rewind();

        //obtain absolute position in world
        float posX = OBJECT_POS_BUFFER.get();
        float posY = OBJECT_POS_BUFFER.get();
        float posZ = OBJECT_POS_BUFFER.get();

        //rewind buffer after read
        OBJECT_POS_BUFFER.rewind();

        return new Vector3f(posX, posY, posZ);
    }

    /***
     * For better performance, You'd better handle the event {@link #setOnLookingAt(Consumer)} or {@link #getLastTraceResult()}
     * @param mouseX xPos in Texture
     * @param mouseY yPos in Texture
     * @return RayTraceResult Hit
     */
    protected BlockHitResult screenPos2BlockPosFace(int mouseX, int mouseY, int x, int y, int width, int height) {
        // render a frame
        RenderSystem.enableDepthTest();
        setupCamera(getPositionedRect(x, y, width, height));

        drawWorld();

        Vector3f hitPos = unProject(mouseX, mouseY);
        BlockHitResult result = rayTrace(hitPos);

        resetCamera();

        return result;
    }

    /***
     * For better performance, You'd better do project in {@link #setAfterWorldRender(Consumer)}
     * @param pos BlockPos
     * @param depth should pass Depth Test
     * @return x, y, z
     */
    protected Vector3f blockPos2ScreenPos(BlockPos pos, boolean depth, int x, int y, int width, int height){
        // render a frame
        RenderSystem.enableDepthTest();
        setupCamera(getPositionedRect(x, y, width, height));

        drawWorld();
        Vector3f winPos = project(new Vector3f(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));

        resetCamera();

        return winPos;
    }

    public static class VertexConsumerWrapper implements VertexConsumer {
        final VertexConsumer builder;
        @Setter
        double offsetX, offsetY, offsetZ;
        float r = 1, g = 1, b = 1, a = 1;

        public VertexConsumerWrapper(VertexConsumer builder) {
            this.builder = builder;
        }

        public void addOffset(double offsetX, double offsetY, double offsetZ) {
            this.offsetX += offsetX;
            this.offsetY += offsetY;
            this.offsetZ += offsetZ;
        }

        public void setColor(float r, float g, float b, float a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        public void clerOffset() {
            this.offsetX = 0;
            this.offsetY = 0;
            this.offsetZ = 0;
        }

        public void clearColor() {
            this.r = 1;
            this.g = 1;
            this.b = 1;
            this.a = 1;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            return builder.vertex(x + offsetX, y + offsetY, z + offsetZ);
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            return builder.color((int)(red * r), (int)(green * g), (int)(blue * b), (int)(alpha * a));
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            return builder.uv(u, v);
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            return builder.overlayCoords(u, v);
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            return builder.uv2(u, v);
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return builder.normal(x, y, z);
        }

        @Override
        public void endVertex() {
            builder.endVertex();
        }

        @Override
        public void defaultColor(int defaultR, int defaultG, int defaultB, int defaultA) {
            builder.defaultColor(defaultR, defaultG, defaultB, defaultA);
        }

        @Override
        public void unsetDefaultColor() {
            builder.unsetDefaultColor();
        }
    }
}
