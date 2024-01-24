package com.lowdragmc.lowdraglib.syncdata;

/**
 * @author KilaBash
 * @date 2023/6/23
 * @implNote IEnhancedManaged
 */
public interface IEnhancedManaged extends IManaged {

    /**
     * Called when a sync field is annotated as {@link com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender}
     */
    default void scheduleRender(String fieldName, Object newValue, Object oldValue) {
        scheduleRenderUpdate();
    }

    void scheduleRenderUpdate();

}
