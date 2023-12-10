package com.lowdragmc.lowdraglib.emi;

import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIEmiHandlers;
import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import com.lowdragmc.lowdraglib.test.TestEMIPlugin;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

/**
 * @author KilaBash
 * @date 2023/4/3
 * @implNote EMIPlugin
 */
@EmiEntrypoint
public class EMIPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        registry.addDragDropHandler(ModularUIGuiContainer.class, ModularUIEmiHandlers.DRAG_DROP_HANDLER);
        registry.addExclusionArea(ModularUIGuiContainer.class, ModularUIEmiHandlers.EXCLUSION_AREA);
        registry.addStackProvider(ModularUIGuiContainer.class, ModularUIEmiHandlers.STACK_PROVIDER);

        if (Platform.isDevEnv()) {
            TestEMIPlugin.register(registry);
        }
    }
}
