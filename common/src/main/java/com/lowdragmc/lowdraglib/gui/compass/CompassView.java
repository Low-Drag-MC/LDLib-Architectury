package com.lowdragmc.lowdraglib.gui.compass;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.utils.FileUtility;
import com.lowdragmc.lowdraglib.utils.Size;
import com.lowdragmc.lowdraglib.utils.XmlUtils;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.input.ReaderInputStream;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

import static com.lowdragmc.lowdraglib.gui.compass.CompassManager.*;

/**
 * @author KilaBash
 * @date 2023/7/26
 * @implNote CompassView
 */
public class CompassView extends WidgetGroup {
    public static final int LIST_WIDTH = 150;

    public final String modID;
    public final CompassConfig config;

    @Getter
    protected final Map<ResourceLocation, CompassSection> sections = new LinkedHashMap<>();

    protected WidgetGroup listView;
    protected WidgetGroup mainView;
    //runtime
    @Nullable
    protected ResourceLocation openedSection, openedNode;

    public CompassView(String modID) {
        super(0, 0, 10, 10);
        setClientSideWidget();
        this.modID = modID;
        if (LDLib.isClient()) {
            this.config = INSTANCE.getConfig(modID);
        } else {
            this.config = null;
        }
    }

    public CompassView(CompassNode compassNode) {
        this(compassNode.section.sectionName.getNamespace());
        if (LDLib.isClient()) {
            this.openedSection = compassNode.section.getSectionName();
            this.openedNode = compassNode.getNodeName();
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onScreenSizeUpdate(int screenWidth, int screenHeight) {
        setSize(new Size(screenWidth, screenHeight));
        this.clearAllWidgets();
        super.onScreenSizeUpdate(screenWidth, screenHeight);
        addWidget(listView = new WidgetGroup(0, 0, 150, screenHeight));
        addWidget(mainView = new WidgetGroup(LIST_WIDTH, 0, screenWidth - 150, screenHeight));
        this.listView.setBackground(config.getListViewBackground());
        initCompass();
        var sectionList = new DraggableScrollableWidgetGroup(4, 4, 142, screenHeight - 8);
        sectionList.setYScrollBarWidth(2).setYBarStyle(null, ColorPattern.T_WHITE.rectTexture().setRadius(1));
        this.addWidget(sectionList);
        SelectableWidgetGroup selected = null;
        for (var section : this.sections.values()) {
            var selectable = new SelectableWidgetGroup(0, sectionList.getAllWidgetSize() * 20, 140, 20);
            selectable.setBackground(config.getListItemBackground());
            selectable.setOnSelected(s -> {
                s.setBackground(config.getListItemSelectedBackground());
                if (this.openedSection == null || !mainView.widgets.isEmpty() || !this.openedSection.equals(section.sectionName)) {
                    this.openSection(section);
                }
            });
            selectable.setOnUnSelected(s -> {
                s.setBackground(config.getListItemBackground());
            });
            selectable.addWidget(new ImageWidget(2, 2, 16, 16, section.getButtonTexture()));
            selectable.addWidget(new ImageWidget(22, 0, 115, 20, new TextTexture(section.sectionName.toLanguageKey("compass.section")).setWidth(115).setType(TextTexture.TextType.LEFT_HIDE)));
            sectionList.addWidget(selectable);
            if (section.sectionName.equals(openedSection)) {
                selected = selectable;
            }
        }
        if (selected != null) {
            sectionList.setSelected(selected);
        }
        this.listView.addWidget(sectionList);

        if (openedSection != null) {
            var section = sections.get(openedSection);
            if (section != null) {
                var node = section.getNode(openedNode);
                if (node != null) {
                    openNodeContent(node);
                } else {
                    openSection(section);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public void openSection(CompassSection section) {
        openedSection = section.sectionName;
        openedNode = null;
        mainView.clearAllWidgets();
        var sectionWidget = new CompassSectionWidget(this, section);
        mainView.addWidget(sectionWidget);
    }

    @Environment(EnvType.CLIENT)
    public void openNodeContent(CompassNode node) {
        openedNode = node.getNodeName();
        openedSection = node.getSection().getSectionName();
        mainView.clearAllWidgets();
        // page
        var pageWidget = new LayoutPageWidget(mainView.getSize().width, mainView.getSize().height);
        if (INSTANCE.devMode) { // always reload page in dev mode
            INSTANCE.nodePages.clear();
        }
        var map = INSTANCE.nodePages.computeIfAbsent(node.getPage(), x -> new HashMap<>());
        var lang = Minecraft.getInstance().getLanguageManager().getSelected();
        var document = map.computeIfAbsent(lang, langKey -> {
            var pageLocation = node.getPage();
            var resourceManager = Minecraft.getInstance().getResourceManager();
            var path = "compass/pages/%s/%s.xml".formatted(langKey,pageLocation.getPath());
            var option = resourceManager.getResource(new ResourceLocation(pageLocation.getNamespace(), path));
            if (option.isEmpty()) {
                path = "compass/pages/en_us/%s.xml".formatted(pageLocation.getPath());
                option = resourceManager.getResource(new ResourceLocation(pageLocation.getNamespace(), path));
            }
            var resource = option.orElseGet(() -> resourceManager.getResource(LDLib.location("compass/pages/en_us/missing.xml")).orElseThrow());
            String content;
            try (var inputStream = resource.open()) {
                content = FileUtility.readInputStream(inputStream);
            } catch (Exception e) {
                LDLib.LOGGER.error("loading compass page {} failed", node.getPage(), e);
                content = """
                    <page>
                        <text>
                            loading page error
                        </text>
                    </page>
                    """;
            }
            try (var stream = new ReaderInputStream(new StringReader(content), StandardCharsets.UTF_8)) {
                return XmlUtils.loadXml(stream);
            } catch (Exception e) {
                LDLib.LOGGER.error("loading compass page {} failed", pageLocation, e);
            }
            return null;
        });

        if (document != null) {
            NodeList nodeList = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                var xmlNode = nodeList.item(i);
                if (xmlNode instanceof Element element) {
                    ILayoutComponent component = CompassManager.INSTANCE.createComponent(element.getTagName(), element);
                    if (component != null) {
                        component.createWidgets(pageWidget);
                    }
                }
            }
        }
        mainView.addWidget(pageWidget);

        // nodes
        List<CompassNode> relatedNodes = new ArrayList<>();
        relatedNodes.addAll(node.getPreNodes());
        relatedNodes.addAll(node.getChildNodes());
        var height = relatedNodes.size() * 20;
        var listHeight = Math.min(140, height);
        if (height > listHeight) {
            listHeight += 10;
        }
        var nodeList = new DraggableScrollableWidgetGroup(mainView.getSize().width - 21, (mainView.getSize().height - listHeight) / 2, 20, listHeight);
        nodeList.setBackground(new GuiTextureGroup(ColorPattern.BLACK.rectTexture(), ColorPattern.WHITE.borderTexture(1)));
        for (var compassNode : relatedNodes) {
            nodeList.addWidget(new ButtonWidget(2, 2 + nodeList.getAllWidgetSize() * 20, 16, 16,
                    compassNode.getButtonTexture(), cd -> openNodeContent(compassNode))
                    .setHoverTexture(new GuiTextureGroup(compassNode.getButtonTexture(), ColorPattern.T_GRAY.rectTexture()))
                    .setHoverTooltips(compassNode.getChatComponent()));
        }
        mainView.addWidget(nodeList);

        // buttons
        if (INSTANCE.devMode) {
            // Reset View
            mainView.addWidget(new ButtonWidget(10, 10, 20, 20,
                    new GuiTextureGroup(ColorPattern.T_GRAY.rectTexture(), Icons.ROTATION),
                    cd -> openNodeContent(node)).setHoverTooltips(Component.translatable("ldlib.gui.compass.refresh")));
        }
    }

    @Environment(EnvType.CLIENT)
    private void initCompass() {
        if (INSTANCE.devMode) {
            INSTANCE.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
        }
        sections.clear();
        var sectionList = INSTANCE.sections.getOrDefault(modID, Collections.emptyMap());
        sectionList.values().stream().sorted(Comparator.comparingInt(a -> a.priority)).forEach(section -> {
            sections.put(section.sectionName, section);
        });
    }

}
