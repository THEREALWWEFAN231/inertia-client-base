package com.inertiaclient.base.gui.components.module;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.components.helpers.VerticalListContainer;
import com.inertiaclient.base.gui.components.tabbedpage.Tab;
import com.inertiaclient.base.gui.components.tabbedpage.TabbedPage;
import com.inertiaclient.base.module.Category;
import com.inertiaclient.base.module.Module;

import java.util.ArrayList;
import java.util.Comparator;

//https://yogalayout.com/playground/?eyJ3aWR0aCI6NTAwLCJoZWlnaHQiOjUwMCwibWluV2lkdGgiOm51bGwsIm1pbkhlaWdodCI6bnVsbCwibWF4V2lkdGgiOm51bGwsIm1heEhlaWdodCI6bnVsbCwicG9zaXRpb24iOnsidG9wIjpudWxsLCJyaWdodCI6bnVsbCwiYm90dG9tIjpudWxsLCJsZWZ0IjpudWxsfSwiY2hpbGRyZW4iOlt7IndpZHRoIjoiNDAwIiwiaGVpZ2h0IjoiNDAwIiwibWluV2lkdGgiOm51bGwsIm1pbkhlaWdodCI6bnVsbCwibWF4V2lkdGgiOm51bGwsIm1heEhlaWdodCI6bnVsbCwiZmxleERpcmVjdGlvbiI6MCwicG9zaXRpb24iOnsidG9wIjpudWxsLCJyaWdodCI6bnVsbCwiYm90dG9tIjpudWxsLCJsZWZ0IjpudWxsfSwiY2hpbGRyZW4iOlt7IndpZHRoIjoiMTAwJSIsImhlaWdodCI6MTAwLCJtaW5XaWR0aCI6bnVsbCwibWluSGVpZ2h0IjpudWxsLCJtYXhXaWR0aCI6bnVsbCwibWF4SGVpZ2h0IjpudWxsLCJwb3NpdGlvbiI6eyJ0b3AiOm51bGwsInJpZ2h0IjpudWxsLCJib3R0b20iOm51bGwsImxlZnQiOm51bGx9fSx7IndpZHRoIjoiNzUlIiwiaGVpZ2h0IjoxMDAsIm1pbldpZHRoIjpudWxsLCJtaW5IZWlnaHQiOm51bGwsIm1heFdpZHRoIjpudWxsLCJtYXhIZWlnaHQiOm51bGwsInBvc2l0aW9uIjp7InRvcCI6bnVsbCwicmlnaHQiOm51bGwsImJvdHRvbSI6bnVsbCwibGVmdCI6bnVsbH0sImZsZXhHcm93IjoiMSIsImNoaWxkcmVuIjpbeyJ3aWR0aCI6MTAwLCJoZWlnaHQiOiIxMDAwIiwibWluV2lkdGgiOm51bGwsIm1pbkhlaWdodCI6bnVsbCwibWF4V2lkdGgiOm51bGwsIm1heEhlaWdodCI6bnVsbCwicG9zaXRpb24iOnsidG9wIjpudWxsLCJyaWdodCI6bnVsbCwiYm90dG9tIjpudWxsLCJsZWZ0IjpudWxsfX1dfV19XX0=
public class ModulesPage extends TabbedPage {

    @Override
    public int defaultSelectedIndex() {
        return 2;
    }

    public ArrayList<Tab> createTabs() {
        var tabs = new ArrayList<Tab>();

        Tab<FavoritesList> favorites = new Tab("Favorites", new FavoritesList());
        tabs.add(favorites);

        favorites.getYogaNode().refresh();

        Tab<VerticalListContainer> all = new Tab("All", new VerticalListContainer());
        tabs.add(all);
        InertiaBase.instance.getModuleManager().getModules().stream().sorted(Comparator.comparing(Module::getNameString)).forEach(module -> {
            all.getYogaNode().addToList(new ModuleComponent(module, favorites.getYogaNode()));
        });

        for (Category category : Category.values()) {
            Tab<VerticalListContainer> categoryTab = new Tab(category.name(), new VerticalListContainer());
            tabs.add(categoryTab);

            var modulesInCategory = InertiaBase.instance.getModuleManager().getModulesByCategory().get(category);
            if (modulesInCategory != null) {//if the category was no modules...
                modulesInCategory.stream().sorted(Comparator.comparing(Module::getNameString)).forEach(module -> {
                    categoryTab.getYogaNode().addToList(new ModuleComponent(module, favorites.getYogaNode()));
                });
            }
        }


        return tabs;
    }

    public static class FavoritesList extends VerticalListContainer {

        public void refresh() {
            this.getListNode().removeAllChildren();
            InertiaBase.instance.getModuleManager().getModules().stream().filter(Module::isFavorite).sorted(Comparator.comparing(Module::getNameString)).forEach(module -> {
                this.addToList(new ModuleComponent(module, this));
            });
        }
    }

}
