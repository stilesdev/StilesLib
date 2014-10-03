/*
 * This document is a part of the source code and related artifacts for StilesLib, an open source library that
 * provides a set of commonly-used functions for Bukkit plugins.
 *
 * http://github.com/mstiles92/StilesLib
 *
 * Copyright (c) 2014 Matthew Stiles (mstiles92)
 *
 * Licensed under the Common Development and Distribution License Version 1.0
 * You may not use this file except in compliance with this License.
 *
 * You may obtain a copy of the CDDL-1.0 License at http://opensource.org/licenses/CDDL-1.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the license.
 */

package com.mstiles92.plugins.stileslib.menu.items;

import com.mstiles92.plugins.stileslib.menu.events.MenuClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public abstract class MenuItem {
    private ItemStack icon;
    private String displayName;
    private List<String> lore;

    public MenuItem(ItemStack icon, String displayName, String... lore) {
        this.icon = icon;
        this.displayName = displayName;
        this.lore = Arrays.asList(lore);
    }

    public ItemStack getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public ItemStack getDisplayItem(Player player) {
        ItemStack icon = this.icon.clone();
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    public abstract void onClick(MenuClickEvent event);
}
