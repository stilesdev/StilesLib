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

import com.google.common.base.Preconditions;
import com.mstiles92.plugins.stileslib.menu.events.MenuClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract representation of an item which will be used as an icon in a Menu.
 */
public abstract class MenuItem {
    private ItemStack icon;
    private String displayName;
    private List<String> lore;

    /**
     * Create a new MenuItem to be displayed with the provided ItemStack as an icon, with the provided display name and
     * lore.
     *
     * @param icon the ItemStack that will be shown as a selectable option in a Menu
     * @param displayName the name that will be applied to the provided ItemStack icon
     * @param lore the lore that will be applied to the provided ItemStack icon
     */
    public MenuItem(ItemStack icon, String displayName, String... lore) {
        Preconditions.checkNotNull(icon, "The icon for a MenuItem can not be null!");
        Preconditions.checkNotNull(displayName, "The display name for a MenuItem can not be null!");

        this.icon = icon;
        this.displayName = displayName;
        this.lore = Arrays.asList(lore);
    }

    /**
     * Get the ItemStack that was supplied as the icon for this MenuItem, with no changes made to it by this class.
     *
     * @return the original ItemStack supplied as the icon for this MenuItem
     */
    public ItemStack getIcon() {
        return icon;
    }

    /**
     * Get the display name that will be applied to the icon for this MenuItem.
     *
     * @return the display name of this MenuItem
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the lore that will be applied to the icon for this MenuItem.
     *
     * @return the lore for this MenuItem
     */
    public List<String> getLore() {
        return lore;
    }

    /**
     * Get the actual ItemStack that will be used to display to the specified Player in a Menu.
     * <br>
     * This method should be overridden when the MenuItem needs to be customized depending on the Player seeing it.
     *
     * @param player the Player the MenuItem will be displayed to
     * @return the ItemStack representation of the MenuItem, ready to be displayed to the specified Player
     */
    public ItemStack getDisplayIcon(Player player) {
        ItemStack icon = this.icon.clone();
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    /**
     * Check whether this MenuItem is visible to the specified Player.
     * <br>
     * This method should be overridden when the MenuItem should be visible only to certain players, such as an option
     * that needs to be unlocked via other means first or an admin-only option that needs a certain permission.
     *
     * @param player which Player for which the visibility of this MenuItem will be checked
     * @return true if the Player should be able to see and use the MenuItem, false if they should not be able to
     */
    public boolean visibleTo(Player player) {
        return true;
    }

    /**
     * Handle the MenuItem being clicked in a Menu.
     * <br>
     * This method should be implemented to define the behavior of the MenuItem when it is clicked. Setting the Result
     * of the MenuClickEvent will define what happens to the Menu this item is in after the onClick method is finished.
     *
     * @param event the MenuClickEvent fired by a Player clicking on a MenuItem
     */
    public abstract void onClick(MenuClickEvent event);
}
