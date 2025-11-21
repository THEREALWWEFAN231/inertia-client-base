package com.inertiaclient.base.gui.components.tabbedpage;

import com.inertiaclient.base.render.yoga.YogaNode;
import lombok.*;
import lombok.experimental.Accessors;
import net.minecraft.text.Text;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class Tab<T extends YogaNode> {

    @NonNull
    @Getter
    private Text label;
    @NonNull
    @Getter
    private T yogaNode;


    @Getter
    @Setter
    @Accessors(fluent = true)
    private Consumer<T> onSelected;

}
