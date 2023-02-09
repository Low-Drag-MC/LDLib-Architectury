package com.lowdragmc.lowdraglib.client.particle;

import com.lowdragmc.lowdraglib.client.scene.ParticleManager;
import com.lowdragmc.lowdraglib.utils.DummyWorld;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/05/30
 * @implNote LParticle
 */
@Environment(EnvType.CLIENT)
public abstract class LParticle extends Particle {
    protected float quadSize = 1;
    @Setter @Getter
    protected boolean moveless;
    @Setter @Getter
    protected int delay;
    @Setter @Getter
    protected int light = -1;
    @Setter @Getter
    protected boolean cull = true;
    @Getter
    protected float randomX, randomY, randomZ, windX, windZ, t;
    @Setter @Getter
    protected float yaw, pitch;
    private Level realLevel;
    protected float oA = 1, oR = 1, oG = 1, oB = 1, oQuadSize = 1;
    @Setter
    protected Consumer<LParticle> onUpdate;
    @Setter
    protected Function<LParticle, Float> alphaUpdate, redUpdate, greenUpdate, blueUpdate, sizeUpdate, rollUpdate;

    protected LParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.realLevel = level;
        if (level == null) {
            hasPhysics = false;
        }
    }

    protected LParticle(ClientLevel level, double x, double y, double z, double sX, double sY, double sZ) {
        super(level, x, y, z, sX, sY, sZ);
        this.realLevel = level;
        if (level == null) {
            hasPhysics = false;
        }
    }

    public Level getLevel() {
        return realLevel == null ? super.level : realLevel;
    }

    public void setLevel(Level level) {
        this.realLevel = level;
    }

    public void setPhysics(boolean hasPhysics) {
        this.hasPhysics = hasPhysics;
    }

    public void setFullLight() {
        setLight(0xf000f0);
    }

    public void setFadeIn(int fadeIn) {
        this.alpha = 0;
        setAlphaUpdate(p -> {
            if (fadeIn > 0 && p.age <= fadeIn) {
                return p.age * 1f / fadeIn;
            }
            return p.alpha;
        });
    }

    public void setFade(int fade) {
        setAlphaUpdate(p -> {
            if (fade > 0 && p.age <= fade) {
                return p.age * 1f / fade;
            } else if (fade > 0 && p.lifetime > 0 && (p.lifetime - p.age) <= fade) {
                return (p.lifetime - p.age) * 1f / fade;
            }
            return p.alpha;
        });
    }

    public void setFadeOut(int fadeOut) {
        setAlphaUpdate(p -> {
            if (fadeOut > 0 && p.lifetime > 0 && (p.lifetime - p.age) <= fadeOut) {
                return (p.lifetime - p.age) * 1f / fadeOut;
            }
            return p.alpha;
        });
    }

    @Nonnull
    public LParticle scale(float pScale) {
        this.quadSize = pScale;
        super.scale(pScale / 0.2f);
        return this;
    }

    public void setPos(double pX, double pY, double pZ, boolean setOrigin) {
        this.x = pX;
        this.y = pY;
        this.z = pZ;
        if (setOrigin) {
            this.xo = x;
            this.yo = y;
            this.zo = z;
        }
        float f = this.bbWidth / 2.0F;
        float f1 = this.bbHeight;
        this.setBoundingBox(new AABB(pX - (double)f, pY, pZ - (double)f, pX + (double)f, pY + (double)f1, pZ + (double)f));
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public void setImmortal() {
        setLifetime(-1);
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public void setRandomMovementScale(float x, float y, float z) {
        this.randomX = x;
        this.randomY = y;
        this.randomZ = z;
    }

    public void setWind(float d) {
        int m = level.getMoonPhase();
        Vec3 source = new Vec3(0.0, 0.0, 0.0);
        Vec3 tar = new Vec3(0.1, 0.0, 0.0);
        float angle = (float)(m * (40 + random.nextInt(10))) / 180.0F * Mth.PI;
        float x = Mth.cos(angle);
        float y = Mth.sin(angle);
        tar = new Vec3(tar.x * x + tar.z * y, tar.y, tar.z * x - tar.x * y);
        Vec3 result = source.add(tar.x, tar.y, tar.z);
        this.windX = (float) (result.x * d);
        this.windZ = (float) (result.z * d);
    }

    public void setColor(int color) {
        this.setColor((float) FastColor.ARGB32.red(color) / 255, (float)FastColor.ARGB32.green(color) / 255, (float)FastColor.ARGB32.blue(color) / 255);
    }

    public void setAlpha(float... alphaAnima) {
        setAnima(this::setAlphaUpdate, v -> this.alpha = v, () -> this.alpha, alphaAnima);
    }

    public void setRed(float... redAnima) {
        setAnima(this::setRedUpdate, v -> this.rCol = v, () -> this.rCol, redAnima);
    }

    public void setGreen(float... greenAnima) {
        setAnima(this::setGreenUpdate, v -> this.gCol = v, () -> this.gCol, greenAnima);
    }

    public void setBlue(float... blueAnima) {
        setAnima(this::setBlueUpdate, v -> this.bCol = v, () -> this.bCol, blueAnima);
    }

    public void setSize(float... sizeAnima) {
        setAnima(this::setSizeUpdate, this::scale, () -> this.quadSize, sizeAnima);
    }

    public void setRoll(float... rollAnima) {
        setAnima(this::setRollUpdate, v -> this.roll = v, () -> this.roll, rollAnima);
    }

    protected void setAnima(Consumer<Function<LParticle, Float>> update, FloatConsumer setter, Supplier<Float> getter, float... anima) {
        if (anima.length > 0) {
            setter.accept(anima[0]);
            if (anima.length == 1) {
                return;
            }
            update.accept(p -> {
                if (p.lifetime > 0) { // 0 - 2 - 10 - 23   0 - 0.33 0.33 - 0.66 0.66 - 1
                    float piece = p.t * (anima.length - 1);
                    int from = (int) Math.min(piece, anima.length - 2);
                    return anima[from] + (anima[from + 1] - anima[from]) * (piece - from);
                }
                return getter.get();
            });
        }
    }

    @Override
    public void tick() {
        if (delay > 0) {
            delay--;
            return;
        }

        updateOrigin();

        if (this.age++ >= this.lifetime && lifetime > 0) {
            this.remove();
        } else if (onUpdate == null) {
            update();
        } else {
            onUpdate.accept(this);
        }

        if (lifetime > 0) {
            t = 1.0f * age / this.lifetime;
        }
    }

    protected void updateOrigin() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.oA = this.alpha;
        this.oR = this.rCol;
        this.oG = this.gCol;
        this.oB = this.bCol;
        this.oQuadSize = this.quadSize;
        this.oRoll = this.roll;
    }

    protected void update() {
        updateChanges();
    }

    protected void updateChanges() {

        if (alphaUpdate != null) {
            this.alpha = alphaUpdate.apply(this);
        }
        if (redUpdate != null) {
            this.rCol = redUpdate.apply(this);
        }
        if (greenUpdate != null) {
            this.gCol = greenUpdate.apply(this);
        }
        if (blueUpdate != null) {
            this.bCol = blueUpdate.apply(this);
        }
        if (sizeUpdate != null) {
            scale(sizeUpdate.apply(this));
        }
        if (rollUpdate != null) {
            this.roll = rollUpdate.apply(this);
        }

        if (!moveless) {
            this.yd -= 0.04D * this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
                this.xd *= 1.1D;
                this.zd *= 1.1D;
            }
            this.xd *= this.friction;
            this.yd *= this.friction;
            this.zd *= this.friction;
            this.xd += this.random.nextGaussian() * (double)this.randomX;
            this.yd += this.random.nextGaussian() * (double)this.randomY;
            this.zd += this.random.nextGaussian() * (double)this.randomZ;
            this.xd *= this.windX;
            this.zd *= this.windZ;
            if (this.onGround && this.friction != 1.0) {
                this.xd *= 0.7F;
                this.zd *= 0.7F;
            }
        }
    }

    public void render(@Nonnull VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        if (delay <= 0) {
            renderInternal(pBuffer, pRenderInfo, pPartialTicks);
        }
    }

    public void renderInternal(@Nonnull VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 vec3 = camera.getPosition();
        float f = (float)(Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());

        float a = getAlpha(partialTicks);
        float r = getRed(partialTicks);
        float g = getGreen(partialTicks);
        float b = getBlue(partialTicks);

        Quaternion quaternion;
        if (this.roll == 0.0F) {
            quaternion = camera.rotation();
        } else {
            quaternion = new Quaternion(camera.rotation());
            if (pitch != 0) {
                quaternion.mul(Vector3f.XP.rotation(pitch));
            }
            if (yaw != 0) {
                quaternion.mul(Vector3f.YP.rotation(yaw));
            }
            quaternion.mul(Vector3f.ZP.rotation(getRoll(partialTicks)));
        }

        Vector3f[] rawVertexes = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = this.getQuadSize(partialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vertex = rawVertexes[i];
            vertex.transform(quaternion);
            vertex.mul(f4);
            vertex.add(f, f1, f2);
        }

        float u0 = this.getU0(partialTicks);
        float u1 = this.getU1(partialTicks);
        float v0 = this.getV0(partialTicks);
        float v1 = this.getV1(partialTicks);
        int light = this.getLight(partialTicks);

        buffer.vertex(rawVertexes[0].x(), rawVertexes[0].y(), rawVertexes[0].z()).uv(u1, v1).color(r, g, b, a).uv2(light).endVertex();
        buffer.vertex(rawVertexes[1].x(), rawVertexes[1].y(), rawVertexes[1].z()).uv(u1, v0).color(r, g, b, a).uv2(light).endVertex();
        buffer.vertex(rawVertexes[2].x(), rawVertexes[2].y(), rawVertexes[2].z()).uv(u0, v0).color(r, g, b, a).uv2(light).endVertex();
        buffer.vertex(rawVertexes[3].x(), rawVertexes[3].y(), rawVertexes[3].z()).uv(u0, v1).color(r, g, b, a).uv2(light).endVertex();
    }

    protected float getAlpha(float partialTicks) {
        return Mth.lerp(partialTicks, this.oA, this.alpha);
    }

    protected float getRed(float partialTicks) {
        return Mth.lerp(partialTicks, this.oR, this.rCol);
    }

    protected float getGreen(float partialTicks) {
        return Mth.lerp(partialTicks, this.oG, this.gCol);
    }

    protected float getBlue(float partialTicks) {
        return Mth.lerp(partialTicks, this.oB, this.bCol);
    }

    protected float getQuadSize(float partialTicks) {
        return Mth.lerp(partialTicks, this.oQuadSize, this.quadSize);
    }

    protected float getRoll(float partialTicks) {
        return Mth.lerp(partialTicks, this.oRoll, this.roll);
    }

    public float getGravity() {
        return gravity;
    }

    public float getRoll() {
        return roll;
    }

    protected int getLight(float pPartialTick) {
        if (light >= 0) return light;
        if (level == null) return 0xf000f0;
        return super.getLightColor(pPartialTick);
    }

    public boolean shouldCull() {
        return cull;
    }

    protected float getU0(float pPartialTicks) {
        return 0;
    }

    protected float getU1(float pPartialTicks) {
        return 1;
    }

    protected float getV0(float pPartialTicks) {
        return 0;
    }

    protected float getV1(float pPartialTicks) {
        return 1;
    }

    public int getAge() {
        return age;
    }

    public void addParticle() {
        updateOrigin();
        if (getLevel() instanceof DummyWorld dummyWorld) {
            ParticleManager particleManager = dummyWorld.getParticleManager();
            if (particleManager != null) {
                particleManager.addParticle(this);
            }
        } else {
            Minecraft.getInstance().particleEngine.add(this);
        }
    }

    public void resetAge() {
        this.age = 0;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
