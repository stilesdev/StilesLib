package com.mstiles92.plugins.stileslib.menu.menus;

import com.mstiles92.plugins.stileslib.menu.events.MenuClickEvent;
import com.mstiles92.plugins.stileslib.menu.items.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * ListMenu is a more advanced Menu, with support for showing a group of items in a List. This class also provides
 * automatic pagination of items if there are too many to display in one InventoryView.
 */
public class ListMenu extends Menu {
    private List<? extends MenuItem> list;
    private static final int NUM_ROWS = 6;
    private static final int NUM_COLLECTION_ROWS = 4;
    private int page = 1;

    public ListMenu(Plugin plugin, String title, final List<? extends MenuItem> list) {
        super(plugin, title, NUM_ROWS);
        this.list = list;

        setItem(45, new MenuItem(new ItemStack(Material.DIODE), ChatColor.RESET + "Previous Page") {
            @Override
            public void onClick(MenuClickEvent event) {
                page -= 1;

                applyPage(event.getPlayer());
                event.setResult(MenuClickEvent.Result.REFRESH);
            }

            @Override
            public boolean visibleTo(Player player) {
                return page > 1;
            }
        });

        setItem(53, new MenuItem(new ItemStack(Material.REDSTONE_COMPARATOR), ChatColor.RESET + "Next Page") {
            @Override
            public void onClick(MenuClickEvent event) {
                page += 1;

                applyPage(event.getPlayer());
                event.setResult(MenuClickEvent.Result.REFRESH);
            }

            @Override
            public boolean visibleTo(Player player) {
                return list.size() > page * 9 * NUM_COLLECTION_ROWS;
            }
        });
    }

    @Override
    public void open(Player player) {
        applyPage(player);
        super.open(player);
    }

    private void applyPage(Player player) {
        for (int i = 0; i < 9 * NUM_COLLECTION_ROWS; i++) {
            int index = i + ((page - 1) * 9 * NUM_COLLECTION_ROWS);
            setItem(i, index < list.size() ? list.get(index) : null);
        }
    }
}
