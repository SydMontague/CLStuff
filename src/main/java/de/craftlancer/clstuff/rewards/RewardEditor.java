package de.craftlancer.clstuff.rewards;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.gui.GUIInventory;
import de.craftlancer.core.util.ItemBuilder;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RewardEditor {
    private Reward reward;
    private GUIInventory inventory;
    private RewardsManager manager;
    
    public RewardEditor(RewardsManager manager, Reward reward) {
        this.manager = manager;
        this.reward = reward;
    }
    
    public void display(Player player) {
        //if (inventory == null)
        createInventory();
        
        player.openInventory(inventory.getInventory());
    }
    
    private void createInventory() {
        this.inventory = new GUIInventory(CLStuff.getInstance(), ChatColor.DARK_PURPLE + "Reward: " + reward.getKey(), 1);
        
        inventory.fill(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(null).build());
        
        setItemRewardsItem();
        setCommandRewardsItem();
        setTitleRewardsItem();
        setInformationItem();
        setTitleMessageItem();
        setSubtitleMessageItem();
        setChatMessageItem();
        setMilestoneInformation();
    }
    
    private void contact(Player player, String message, MessageLevel level, Sound sound) {
        MessageUtil.sendMessage(manager, player, level, message);
        player.playSound(player.getLocation(), sound, 0.5F, 1F);
    }
    
    private void startConversation(Player player, StringPrompt prompt) {
        new ConversationFactory(CLStuff.getInstance())
                .withFirstPrompt(prompt)
                .withLocalEcho(false)
                .withModality(true)
                .buildConversation(player)
                .begin();
        player.closeInventory();
    }
    
    private void setItemRewardsItem() {
        List<String> list = new ArrayList<>();
        for (int e = 0; e < reward.getItemRewards().size(); e++) {
            ItemStack i = reward.getItemRewards().get(e);
            list.add("§3" + e + ": §b" + " " + "(x" + i.getAmount() + ") "
                    + (i.getItemMeta().hasDisplayName() ? i.getItemMeta().getDisplayName() : i.getType().name()));
        }
        
        inventory.setItem(1, new ItemBuilder(Material.DIAMOND)
                .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Item Rewards")
                .addLore("", "§3§lLEFT CLICK §bto add item in main hand", "§3§lRIGHT CLICK §bto remove item", "")
                .addLore(list)
                .build());
        
        inventory.setClickAction(1, p -> {
            startConversation(p, new NumberRewardPrompt("§bEnter an index to remove an item from:",
                    i -> {
                        if (i < 0 || i >= reward.getItemRewards().size()) {
                            contact(p, "Index out of bounds!", MessageLevel.WARNING, Sound.BLOCK_NOTE_BLOCK_PLING);
                            return;
                        }
                        reward.getItemRewards().remove(i.intValue());
                        contact(p, "Successfully removed item reward.", MessageLevel.SUCCESS, Sound.BLOCK_NOTE_BLOCK_PLING);
                        setItemRewardsItem();
                    }));
        }, ClickType.RIGHT);
        
        inventory.setClickAction(1, p -> {
            reward.getItemRewards().add(p.getInventory().getItemInMainHand().clone());
            contact(p, "Item added.", MessageLevel.SUCCESS, Sound.BLOCK_NOTE_BLOCK_PLING);
            setItemRewardsItem();
        }, ClickType.LEFT);
    }
    
    private void setCommandRewardsItem() {
        List<String> list = new ArrayList<>();
        for (int e = 0; e < reward.getCommandsRewards().size(); e++)
            list.add("§3" + e + ": §b" + (reward.getCommandsRewards().get(e)));
        
        
        inventory.setItem(2, new ItemBuilder(Material.COMMAND_BLOCK)
                .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Command Rewards")
                .addLore("", "§3§lLEFT CLICK §bto add command", "§3§lRIGHT CLICK §bto remove a command", "")
                .addLore(list)
                .build());
        
        inventory.setClickAction(2, p -> {
            startConversation(p, new NumberRewardPrompt("§bEnter an index to remove command from:",
                    i -> {
                        if (i < 0 || i >= reward.getCommandsRewards().size()) {
                            contact(p, "Index out of bounds!", MessageLevel.WARNING, Sound.BLOCK_NOTE_BLOCK_PLING);
                            return;
                        }
                        reward.getCommandsRewards().remove(i.intValue());
                        contact(p, "Successfully removed command reward.", MessageLevel.SUCCESS, Sound.BLOCK_NOTE_BLOCK_PLING);
                        setCommandRewardsItem();
                    }));
        }, ClickType.RIGHT);
        
        inventory.setClickAction(2, p -> startConversation(p, new StringRewardPrompt("§bEnter the command, NOT including a '/' that should be run by the console when a player gets this reward.",
                s -> {
                    reward.getCommandsRewards().add(s);
                    contact(p, "Successfully added command reward.", MessageLevel.SUCCESS, Sound.BLOCK_NOTE_BLOCK_PLING);
                    setCommandRewardsItem();
                })), ClickType.LEFT);
    }
    
    private void setTitleRewardsItem() {
        List<String> list = new ArrayList<>();
        for (int e = 0; e < reward.getTitleRewards().size(); e++)
            list.add("§3" + e + ": §b" + (reward.getTitleRewards().get(e)));
        
        inventory.setItem(3, new ItemBuilder(Material.NAME_TAG)
                .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Title Rewards")
                .addLore("", "§3§lLEFT CLICK §bto add title reward", "§3§lRIGHT CLICK §bto remove title reward", "")
                .addLore(list)
                .build());
        
        inventory.setClickAction(3, p -> {
            startConversation(p, new NumberRewardPrompt("§bEnter an index to remove title from:",
                    i -> {
                        if (i < 0 || i >= reward.getTitleRewards().size()) {
                            contact(p, "Index out of bounds!", MessageLevel.WARNING, Sound.BLOCK_NOTE_BLOCK_PLING);
                            return;
                        }
                        reward.getTitleRewards().remove(i.intValue());
                        contact(p, "Successfully removed title reward.", MessageLevel.SUCCESS, Sound.BLOCK_NOTE_BLOCK_PLING);
                        setTitleRewardsItem();
                    }));
        }, ClickType.RIGHT);
        
        inventory.setClickAction(3, p -> startConversation(p, new StringRewardPrompt("§bEnter the player title. Use '&' for color codes.",
                s -> {
                    reward.getTitleRewards().add(s);
                    contact(p, "Successfully added title reward.", MessageLevel.SUCCESS, Sound.BLOCK_NOTE_BLOCK_PLING);
                    setTitleRewardsItem();
                })), ClickType.LEFT);
    }
    
    private void setTitleMessageItem() {
        inventory.setItem(5, new ItemBuilder(Material.CHAIN_COMMAND_BLOCK)
                .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Title Message")
                .addLore("", "§3§lLEFT CLICK §bto set title message", "§3§lRIGHT CLICK §bto view title message",
                        "§3§lSHIFT-LEFT CLICK §bto remove message.")
                .build());
        
        inventory.setClickAction(5, p -> contact(p, reward.getTitleMessage().equals("") ? "ERROR: No title message currently set." : reward.getTitleMessage(),
                MessageLevel.WARNING, Sound.BLOCK_NOTE_BLOCK_PLING), ClickType.RIGHT);
        
        inventory.setClickAction(5, p -> startConversation(p, new StringRewardPrompt("§bEnter the title message. Use '&' for color codes.",
                s -> {
                    reward.setTitleMessage(ChatColor.translateAlternateColorCodes('&', s));
                    contact(p, "Successfully set title message.", MessageLevel.SUCCESS, Sound.BLOCK_NOTE_BLOCK_PLING);
                    setTitleMessageItem();
                })), ClickType.LEFT);
        
        inventory.setClickAction(5, p -> {
            reward.setTitleMessage("");
            contact(p, "Successfully removed title message.", MessageLevel.SUCCESS, Sound.BLOCK_NOTE_BLOCK_PLING);
        }, ClickType.SHIFT_LEFT);
    }
    
    private void setSubtitleMessageItem() {
        inventory.setItem(6, new ItemBuilder(Material.CHAIN_COMMAND_BLOCK)
                .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Subtitle Message")
                .addLore("", "§3§lLEFT CLICK §bto set subtitle message",
                        "§3§lRIGHT CLICK §bto view subtitle message",
                        "§3§lSHIFT-LEFT CLICK §bto remove message.")
                .build());
        
        inventory.setClickAction(6, p -> contact(p, reward.getSubtitleMessage().equals("") ? "ERROR: No subtitle message currently set." : reward.getSubtitleMessage(),
                MessageLevel.WARNING, Sound.BLOCK_NOTE_BLOCK_PLING), ClickType.RIGHT);
        
        inventory.setClickAction(6, p -> startConversation(p, new StringRewardPrompt("§bEnter the subtitle message. Use '&' for color codes.",
                s -> {
                    reward.setSubtitleMessage(ChatColor.translateAlternateColorCodes('&', s));
                    contact(p, "Successfully set subtitle message.", MessageLevel.SUCCESS, Sound.BLOCK_NOTE_BLOCK_PLING);
                    setSubtitleMessageItem();
                })), ClickType.LEFT);
        
        inventory.setClickAction(6, p -> {
            reward.setSubtitleMessage("");
            contact(p, "Successfully removed subtitle message.", MessageLevel.SUCCESS, Sound.BLOCK_NOTE_BLOCK_PLING);
        }, ClickType.SHIFT_LEFT);
    }
    
    private void setChatMessageItem() {
        inventory.setItem(7, new ItemBuilder(Material.REPEATING_COMMAND_BLOCK)
                .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Chat Message")
                .addLore("", "§3§lLEFT CLICK §bto set chat message", "§3§lRIGHT CLICK §bto view chat message",
                        "§3§lSHIFT-LEFT CLICK §bto remove message.")
                .build());
        
        inventory.setClickAction(7, p -> contact(p, reward.getChatMessage().equals("") ? "ERROR: No chat message currently set." : reward.getChatMessage(),
                MessageLevel.WARNING, Sound.BLOCK_NOTE_BLOCK_PLING), ClickType.RIGHT);
        
        inventory.setClickAction(7, p -> startConversation(p, new StringRewardPrompt("§bEnter the chat message. Use '&' for color codes.",
                s -> {
                    reward.setChatMessage(ChatColor.translateAlternateColorCodes('&', s));
                    contact(p, "Successfully set chat message.", MessageLevel.SUCCESS, Sound.BLOCK_NOTE_BLOCK_PLING);
                    setChatMessageItem();
                })), ClickType.LEFT);
        
        inventory.setClickAction(7, p -> {
            reward.setChatMessage("");
            contact(p, "Successfully removed chat message.", MessageLevel.SUCCESS, Sound.BLOCK_NOTE_BLOCK_PLING);
        }, ClickType.SHIFT_LEFT);
    }
    
    private void setInformationItem() {
        inventory.setItem(4, new ItemBuilder(Material.STONE)
                .setCustomModelData(5)
                .setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Information")
                .addLore("", "§3Key: §b" + reward.getKey(),
                        "§3Item rewards: §b" + reward.getItemRewards().size(),
                        "§3Command rewards: §b" + reward.getCommandsRewards().size(),
                        "§3Title rewards: §b" + reward.getTitleRewards().size(), "",
                        "§d§oUse %player% to display player name")
                .build());
    }
    
    private void setMilestoneInformation() {
        List<String> list = new ArrayList<>();
        for (int e = 0; e < reward.getInformation().size(); e++)
            list.add("§3" + e + ": §b" + (reward.getInformation().get(e)));
        
        
        inventory.setItem(8, new ItemBuilder(Material.DIAMOND_BLOCK)
                .setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Milestone Information")
                .addLore("", "§3§lLEFT CLICK §bto add line", "§3§lRIGHT CLICK §bto remove a line", "")
                .addLore(list)
                .build());
        
        inventory.setClickAction(8, p -> {
            startConversation(p, new NumberRewardPrompt("§bEnter an index to remove line from:",
                    i -> {
                        if (i < 0 || i >= reward.getInformation().size()) {
                            contact(p, "Index out of bounds!", MessageLevel.WARNING, Sound.BLOCK_NOTE_BLOCK_PLING);
                            return;
                        }
                        reward.getInformation().remove(i.intValue());
                        contact(p, "Successfully removed information line.", MessageLevel.SUCCESS, Sound.BLOCK_NOTE_BLOCK_PLING);
                        setMilestoneInformation();
                    }));
        }, ClickType.RIGHT);
        
        inventory.setClickAction(8, p -> startConversation(p, new StringRewardPrompt("§bEnter the information line, color codes are acceptable.",
                s -> {
                    reward.getInformation().add(ChatColor.translateAlternateColorCodes('&', s));
                    contact(p, "Successfully added information line.", MessageLevel.SUCCESS, Sound.BLOCK_NOTE_BLOCK_PLING);
                    setMilestoneInformation();
                })), ClickType.LEFT);
    }
    
    private class StringRewardPrompt extends StringPrompt {
        
        private String prompt;
        private Consumer<String> onAccept;
        
        public StringRewardPrompt(String prompt, Consumer<String> onAccept) {
            this.prompt = prompt;
            this.onAccept = onAccept;
        }
        
        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            return prompt + " Enter '&&' to exit prompt.";
        }
        
        @Override
        public @Nullable Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {
            Player player = (Player) conversationContext.getForWhom();
            if (s == null)
                return this;
            else if (s.equals("&&")) {
                MessageUtil.sendMessage(manager, player, MessageLevel.SUCCESS, "Successfully exited prompt.");
                display(player);
            } else {
                MessageUtil.sendMessage(manager, player, MessageLevel.SUCCESS, "Successfully saved prompt.");
                onAccept.accept(s);
                display(player);
            }
            
            return Prompt.END_OF_CONVERSATION;
        }
    }
    
    private class NumberRewardPrompt extends StringPrompt {
        
        private String prompt;
        private Consumer<Integer> onAccept;
        
        public NumberRewardPrompt(String prompt, Consumer<Integer> onAccept) {
            this.prompt = prompt;
            this.onAccept = onAccept;
        }
        
        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            return prompt + " Enter '&&' to exit prompt.";
        }
        
        @Override
        public @Nullable Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {
            Player player = (Player) conversationContext.getForWhom();
            if (s == null)
                return this;
            else if (s.equals("&&")) {
                MessageUtil.sendMessage(manager, player, MessageLevel.SUCCESS, "Successfully exited prompt.");
                display(player);
            } else {
                int i;
                try {
                    i = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    return this;
                }
                onAccept.accept(i);
                display(player);
            }
            
            return Prompt.END_OF_CONVERSATION;
        }
    }
}
