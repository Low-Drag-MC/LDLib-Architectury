package com.lowdragmc.creategreg.api.data;

import com.simibubi.create.foundation.data.CreateRegistrate;
import lombok.experimental.Accessors;
import net.minecraft.MethodsReturnNonnullByDefault;


import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2023/1/30
 * @implNote CreateGregRegistrate
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Accessors(fluent = true)
public class CreateGregRegistrate extends CreateRegistrate {


    protected CreateGregRegistrate(String modID) {
        super(modID);
    }

    public static CreateGregRegistrate create(String modID) {
        return new CreateGregRegistrate(modID);
    }

}
