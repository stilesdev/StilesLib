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

package com.mstiles92.plugins.stileslib.menu.menus;

import com.mstiles92.plugins.stileslib.menu.MenuInventoryHolder;
import com.mstiles92.plugins.stileslib.menu.events.MenuClickEvent;
import com.mstiles92.plugins.stileslib.menu.items.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class Menu {
    private String title;
    private int numRows;
    private MenuItem[] contents;

    public Menu(String title, int numRows) {
        this.title = title;
        this.numRows = numRows;
        contents = new MenuItem[numRows * 9];
    }

    public String getTitle() {
        return title;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setItem(int position, MenuItem item) {
        contents[position] = item;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(this, Bukkit.createInventory(player, numRows * 9)), numRows * 9, title);

        applyMenuToInventory(inventory, player);

        player.openInventory(inventory);
    }

    public void handleClick(InventoryClickEvent event) {
        if (!event.getClick().equals(ClickType.LEFT)) {
            return;
        }

        int clickedSlot = event.getRawSlot();

        if (clickedSlot >= 0 && clickedSlot < numRows * 9 && contents[clickedSlot] != null) {
            Player player = (Player) event.getWhoClicked();

            if (contents[clickedSlot].visibleTo(player)) {
                MenuClickEvent menuClickEvent = new MenuClickEvent(player);
                contents[clickedSlot].onClick(menuClickEvent);

                switch (menuClickEvent.getResult()) {
                    case REFRESH:
                        refreshMenu(player);
                        break;
                    case CLOSE:
                        player.closeInventory();
                        break;
                }
            }
        }
    }

    private void refreshMenu(Player player) {
        if (player.getOpenInventory() != null) {
            Inventory inventory = player.getOpenInventory().getTopInventory();

            if (inventory.getHolder() instanceof MenuInventoryHolder && ((MenuInventoryHolder) inventory.getHolder()).getMenu().equals(this)) {
                applyMenuToInventory(inventory, player);
            }
        }
    }

    private void applyMenuToInventory(Inventory inventory, Player player) {
        inventory.clear();
        
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && contents[i].visibleTo(player)) {
                inventory.setItem(i, contents[i].getDisplayIcon(player));
            }
        }
    }
}
