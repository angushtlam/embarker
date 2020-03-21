package com.raeic.embarker.cities.commands;

import com.raeic.embarker.cities.models.StakedChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class StakeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1 || (args.length == 1 && !args[0].equalsIgnoreCase("confirm"))) {
            sender.sendMessage("Usage: /stake (confirm)");
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran by a player.");
            return false;
        }

        Player p = (Player) sender;
        Chunk chunk = p.getLocation().getChunk();

        StakedChunk stakedChunk = StakedChunk.findOne(chunk.getX(), chunk.getZ());

        if (stakedChunk != null) {
            UUID ownerUniqueId = UUID.fromString(stakedChunk.getOwnerUniqueId());
            OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUniqueId);

            if (p.getUniqueId().equals(ownerUniqueId)) {
                p.sendMessage("You have already staked this chunk of land!");
            } else if (owner.getName() != null) {
                p.sendMessage("Sorry, this chunk of land is already staked by " + owner.getName() + ".");
            } else {
                // Should not happen, but just in case there's missing data.
                p.sendMessage("Sorry, this chunk of land is already staked by an unknown player.");
            }
        } else {
            int totalOwned = StakedChunk.countOwnedBy(p.getUniqueId().toString());
            int cost = (int) Math.round(Math.pow(totalOwned + 1, 2));
            String emeraldPlurality = cost == 1 ? "Emerald" : "Emeralds";

            if (args.length == 0) {
                p.sendMessage("This chunk of land is unowned. You can stake it by entering /stake confirm.");

                String chunkPlurality = totalOwned == 1 ? "chunk" : "chunks";
                p.sendMessage("You own " + totalOwned + " " + chunkPlurality + " of land. To claim another, you need " + cost + " " + emeraldPlurality + " .");

            } else if (args[0].equalsIgnoreCase("confirm")) {
                // Make sure player is not broke
                if (!p.getInventory().containsAtLeast(new ItemStack(Material.EMERALD), cost)) {
                    p.sendMessage("You need " + cost + " " + emeraldPlurality + " to stake this chunk of land.");
                    return true;
                }

                // Look for all the Emeralds in the player's inventory.
                ItemStack[] contents = p.getInventory().getContents();
                int removedEmeralds = 0;

                ArrayList<ItemStack> itemsToRemove = new ArrayList<>();
                for (ItemStack inventoryItem : contents) {
                    if (inventoryItem == null || !inventoryItem.getType().equals(Material.EMERALD)) {
                        continue;
                    }

                    int amount = inventoryItem.getAmount();
                    int needToPay = cost - removedEmeralds;

                    if (needToPay == 0) {
                        break;
                    } else if (amount <= needToPay) {
                        removedEmeralds += amount;
                        itemsToRemove.add(inventoryItem);
                    } else /* if (amount > needToPay) */ {
                        // If the user has more Emeralds than what they needed to pay, just set the item.
                        inventoryItem.setAmount(amount - needToPay);
                        break;
                    }
                }

                // Remove all Emeralds from inventory.
                for (ItemStack itemToRemove : itemsToRemove) {
                    p.getInventory().remove(itemToRemove);
                }

                StakedChunk newStakedChunk = new StakedChunk(chunk.getX(), chunk.getZ(), p.getUniqueId().toString());
                newStakedChunk.save();
                p.sendMessage("Confirmed! You paid " + cost + " " + emeraldPlurality + " to stake this chunk of land.");
            }
        }

        return true;
    }
}
