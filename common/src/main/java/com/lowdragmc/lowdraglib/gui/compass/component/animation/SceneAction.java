package com.lowdragmc.lowdraglib.gui.compass.component.animation;

import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.lowdragmc.lowdraglib.utils.BlockPosFace;
import com.lowdragmc.lowdraglib.utils.XmlUtils;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.w3c.dom.Element;

import java.util.*;

/**
 * @author KilaBash
 * @date 2023/7/28
 * @implNote SceneAction
 */
public class SceneAction extends Action {
    private final List<Tuple<BlockAnima, BlockInfo>> addedBlocks = new ArrayList<>();
    private final List<BlockAnima> removedBlocks = new ArrayList<>();
    private final Map<BlockPos, BlockInfo> modifiedTags = new HashMap<>();
    private final Map<BlockPosFace, Integer> highlightedBlocks = new HashMap<>();
    private final Map<Vec3, MutableTriple<Tuple<XmlUtils.SizedIngredient, List<Component>>, Vec2, Integer>> tooltipBlocks = new HashMap<>();
    private Float rotation;
    //runtime
    private int duration = -1;

    public SceneAction() {
    }

    public SceneAction(Element element) {
        super(element);
        final var nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            final var node = nodes.item(i);
            if (node instanceof Element blockElement) {
                val nodeName = blockElement.getNodeName();
                val blockPos = XmlUtils.getAsBlockPos(blockElement, "pos", BlockPos.ZERO);
                switch (nodeName) {
                    case "add" -> addedBlocks.add(new Tuple<>(new BlockAnima(blockPos, XmlUtils.getAsVec3(blockElement, "offset", new Vec3(0, 0.7, 0)), XmlUtils.getAsInt(blockElement, "duration", 15)), XmlUtils.getBlockInfo(blockElement)));
                    case "remove" -> removedBlocks.add(new BlockAnima(blockPos, XmlUtils.getAsVec3(blockElement, "offset", new Vec3(0, 0.7, 0)), XmlUtils.getAsInt(blockElement, "duration", 15)));
                    case "modify" -> modifiedTags.put(blockPos, XmlUtils.getBlockInfo(blockElement));
                    case "rotation" -> rotation = XmlUtils.getAsFloat(blockElement, "degree", 0f);
                    case "highlight" -> highlightedBlocks.put(new BlockPosFace(blockPos, XmlUtils.getAsEnum(blockElement, "face", Direction.class, null)), XmlUtils.getAsInt(blockElement, "duration", 40));
                    case "tooltip" -> tooltipBlocks.put(XmlUtils.getAsVec3(blockElement, "pos", new Vec3(0, 0, 0)), MutableTriple.of(new Tuple<>(XmlUtils.getIngredient(blockElement), new ArrayList<>(XmlUtils.getComponents(blockElement, Style.EMPTY))), XmlUtils.getAsVec2(blockElement, "screen-offset", new Vec2(0.3f, 0.3f)), XmlUtils.getAsInt(blockElement, "duration", 40)));
                }
            }
        }
    }

    public SceneAction rotation(Float rotation) {
        this.rotation = rotation;
        return this;
    }

    public SceneAction addedBlock(BlockPos pos, BlockInfo blockInfo, Vec3 offset, int duration) {
        addedBlocks.add(new Tuple<>(new BlockAnima(pos, offset, duration), blockInfo));
        return this;
    }

    public SceneAction removedBlock(BlockPos pos, Vec3 offset, int duration) {
        removedBlocks.add(new BlockAnima(pos, offset, duration));
        return this;
    }

    public SceneAction modifiedTag(BlockPos pos, BlockInfo blockInfo) {
        modifiedTags.put(pos, blockInfo);
        return this;
    }

    public SceneAction highlightedBlock(BlockPos pos, Direction face, int duration) {
        highlightedBlocks.put(new BlockPosFace(pos, face), duration);
        return this;
    }

    public SceneAction tooltip(Vec3 pos, Tuple<XmlUtils.SizedIngredient, List<Component>> tooltip, Vec2 screenOffset, int duration) {
        tooltipBlocks.put(pos, MutableTriple.of(tooltip, screenOffset, duration));
        return this;
    }

    @Override
    public int getDuration() {
        if (duration == -1) {
            duration = 0;
            for (var tuple : addedBlocks) {
                duration = Math.max(duration, tuple.getA().duration());
            }
            for (var block : removedBlocks) {
                duration = Math.max(duration, block.duration());
            }
            for (var entry : highlightedBlocks.entrySet()) {
                duration = Math.max(duration, entry.getValue());
            }
            for (var entry : tooltipBlocks.entrySet()) {
                duration = Math.max(duration, entry.getValue().getRight());
            }
        }
        return duration + 5;
    }

    @Override
    public void performAction(AnimationFrame frame, CompassScene scene, boolean anima) {
        for (Tuple<BlockAnima, BlockInfo> tuple : addedBlocks) {
            var blockInfo = tuple.getB();
            blockInfo.clearBlockEntityCache();
            scene.addBlock(tuple.getA().pos(), blockInfo, anima ? tuple.getA() : null);
        }

        for (var block : removedBlocks) {
            scene.removeBlock(block.pos(), anima ? block : null);
        }
        for (var entry : modifiedTags.entrySet()) {
            scene.addBlock(entry.getKey(), entry.getValue(), null);
        }
        if (anima) {
            for (var entry : highlightedBlocks.entrySet()) {
                scene.highlightBlock(entry.getKey(), entry.getValue());
            }
        }
        if (anima) {
            for (var entry : tooltipBlocks.entrySet()) {
                scene.addTooltip(entry.getKey(), entry.getValue().getLeft(), entry.getValue().getMiddle(), entry.getValue().getRight());
            }
        }
        if (rotation != null) {
            scene.rotate(rotation, anima);
        }
    }
}
