package com.lowdragmc.lowdraglib.forge;

import com.lowdragmc.lowdraglib.CommonProxy;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.ServerCommands;
import com.lowdragmc.lowdraglib.client.renderer.block.RendererBlock;
import com.lowdragmc.lowdraglib.client.renderer.block.RendererBlockEntity;
import com.lowdragmc.lowdraglib.client.renderer.block.forge.RendererBlockEntityImpl;
import com.lowdragmc.lowdraglib.plugin.ILDLibPlugin;
import com.lowdragmc.lowdraglib.plugin.LDLibPlugin;
import com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries;
import com.lowdragmc.lowdraglib.test.NoRendererTestBlock;
import com.lowdragmc.lowdraglib.test.TestBlock;
import com.lowdragmc.lowdraglib.test.TestBlockEntity;
import com.lowdragmc.lowdraglib.test.TestItem;
import com.lowdragmc.lowdraglib.test.forge.TestBlockEntityImpl;
import com.lowdragmc.lowdraglib.utils.ReflectionUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CommonProxyImpl {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LDLib.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LDLib.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, LDLib.MOD_ID);

    public CommonProxyImpl() {
        BLOCKS.register("renderer", () -> RendererBlock.BLOCK);
        RendererBlockEntityImpl.TYPE = BLOCK_ENTITY_TYPES.register("renderer", () -> BlockEntityType.Builder.of(RendererBlockEntity::new, RendererBlock.BLOCK).build(null));

        if (Platform.isDevEnv()) {
            BLOCKS.register("test", () -> TestBlock.BLOCK);
            ITEMS.register("test", () -> TestItem.ITEM);
            BLOCKS.register("test_2", () -> NoRendererTestBlock.BLOCK);
            ITEMS.register("test_2", () -> new BlockItem(NoRendererTestBlock.BLOCK, new Item.Properties()));
            TestBlockEntityImpl.TYPE = BLOCK_ENTITY_TYPES.register("test", () -> BlockEntityType.Builder.of(TestBlockEntity::new, TestBlock.BLOCK).build(null));
        }

        // used for forge events (ClientProxy + CommonProxy)
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.register(this);
        // register server commands
        MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
        // init common features
        CommonProxy.init();
        // load ldlib plugin
        ReflectionUtils.findAnnotationClasses(LDLibPlugin.class, clazz -> {
            try {
                if (clazz.getConstructor().newInstance() instanceof ILDLibPlugin plugin) {
                    plugin.onLoad();
                }
            } catch (Throwable ignored) {}
        }, () -> {});
        TypedPayloadRegistries.postInit();
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    public void registerCommand(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        ServerCommands.createServerCommands().forEach(dispatcher::register);
    }
}
