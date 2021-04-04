package de.craftlancer.clstuff;

import de.craftlancer.clstuff.adminshop.AdminShopManager;
import de.craftlancer.clstuff.afk.AFKListener;
import de.craftlancer.clstuff.arena.ArenaGUI;
import de.craftlancer.clstuff.citizensets.CitizenSetsManager;
import de.craftlancer.clstuff.citizensets.commands.CitizenSetCommandHandler;
import de.craftlancer.clstuff.commands.CLStuffCommands;
import de.craftlancer.clstuff.commands.CenterMapCommand;
import de.craftlancer.clstuff.commands.CraftCommand;
import de.craftlancer.clstuff.commands.ItemBuilderCommand;
import de.craftlancer.clstuff.commands.StatsCommand;
import de.craftlancer.clstuff.commands.WildCommand;
import de.craftlancer.clstuff.connectionmessages.ConnectionMessages;
import de.craftlancer.clstuff.connectionmessages.ConnectionMessagesCommandHandler;
import de.craftlancer.clstuff.emotes.EmoteCommand;
import de.craftlancer.clstuff.emotes.EmoteManager;
import de.craftlancer.clstuff.explosionregulator.ExplosionRegulator;
import de.craftlancer.clstuff.help.CCHelpCommandHandler;
import de.craftlancer.clstuff.heroes.Heroes;
import de.craftlancer.clstuff.heroes.commands.HeroesCommandHandler;
import de.craftlancer.clstuff.inventorymanagement.InventoryManagement;
import de.craftlancer.clstuff.inventorymanagement.InventoryManagementCommandHandler;
import de.craftlancer.clstuff.mobcontrol.ItemCooldowns;
import de.craftlancer.clstuff.mobcontrol.MobControl;
import de.craftlancer.clstuff.premium.DonatorTicketCommandHandler;
import de.craftlancer.clstuff.premium.DonatorTicketRegistry;
import de.craftlancer.clstuff.premium.ModelToken;
import de.craftlancer.clstuff.premium.RecolorCommand;
import de.craftlancer.clstuff.rankings.Rankings;
import de.craftlancer.clstuff.rankings.RankingsCommandHandler;
import de.craftlancer.clstuff.resourcepack.CustomBlockRegistry;
import de.craftlancer.clstuff.resourcepack.command.CustomBlockCommandHandler;
import de.craftlancer.clstuff.rewards.RewardsCommandHandler;
import de.craftlancer.clstuff.rewards.RewardsManager;
import de.craftlancer.clstuff.squest.ServerQuests;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.NMSUtils;
import de.craftlancer.core.Utils;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.StringReader;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class CLStuff extends JavaPlugin implements Listener {
    
    private static CLStuff instance;
    
    private static String ADMIN_PERMISSION = "clstuff.admin";
    
    private WGNoDropFlag flag;
    private ServerQuests serverQuests;
    private Rankings rankings;
    private ModelToken tokens;
    private RecolorCommand recolor;
    private ExplosionRegulator exploNerf;
    private boolean useDiscord = false;
    private ArenaGUI arenaGUI;
    private Heroes heroes;
    private AdminShopManager adminShop;
    private CitizenSetsManager citizenSets;
    private EmoteManager emotes;
    private MobControl mobControl;
    private ConnectionMessages connectionMessages;
    private DonatorTicketRegistry donatorTicketRegistry;
    private InventoryManagement inventoryManagement;
    private RewardsManager rewardsManager;
    private CustomBlockRegistry customBlockRegistry;
    
    @Override
    public void onLoad() {
        WGNoDropFlag.registerFlag();
        ItemCooldowns.registerFlag();
    }
    
    public static CLStuff getInstance() {
        return instance;
    }
    
    public static String getAdminPermission() {
        return ADMIN_PERMISSION;
    }
    
    @Override
    public void onEnable() {
        instance = this;
        BaseComponent prefix = new TextComponent(new ComponentBuilder("[").color(ChatColor.WHITE).append("Craft").color(ChatColor.DARK_RED).append("Citizen")
                .color(ChatColor.WHITE).append("]").color(ChatColor.WHITE).create());
        MessageUtil.registerPlugin(this, prefix, ChatColor.WHITE, ChatColor.YELLOW, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_AQUA);
        
        useDiscord = Bukkit.getPluginManager().getPlugin("DiscordSRV") != null;
        
        getCommand("wiki").setExecutor(CLStuffCommands::wikiCommand);
        getCommand("voteall").setExecutor(CLStuffCommands::voteallCommand);
        getCommand("map").setExecutor(CLStuffCommands::mapCommand);
        getCommand("time").setExecutor(CLStuffCommands::timeCommand);
        getCommand("store").setExecutor(CLStuffCommands::storeCommand);
        
        getCommand("howtoplay").setExecutor((a, b, c, d) -> {
            String commandLine = "minecraft:give " + a.getName()
                    + " written_book{pages:[\"[\\\"\\\",{\\\"text\\\":\\\" \\\\u0020 \\\\u0020 \\\\u263d\\\\u25ba\\\",\\\"color\\\":\\\"gold\\\"},{\\\"text\\\":\\\"Craft\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\"Citizen\\\",\\\"bold\\\":true,\\\"color\\\":\\\"gray\\\"},{\\\"text\\\":\\\"\\\\n\\\\nWelcome \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Peasant "
                    + a.getName()
                    + "\\\",\\\"bold\\\":true,\\\"color\\\":\\\"gold\\\"},{\\\"text\\\":\\\"!\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Brave enough to conquer \\\",\\\"italic\\\":true},{\\\"text\\\":\\\"Alinor\\\",\\\"italic\\\":true,\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/ConquerPoints\\\"}},{\\\"text\\\":\\\" or raid some \\\",\\\"color\\\":\\\"reset\\\",\\\"italic\\\":true},{\\\"text\\\":\\\"dungeons\\\",\\\"italic\\\":true,\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Dungeons_%26_Raids\\\"}},{\\\"text\\\":\\\"?\\\\nBecome a cunning merchant, build a village or found a mighty kingdom?\\\",\\\"color\\\":\\\"reset\\\",\\\"italic\\\":true},{\\\"text\\\":\\\"\\\\n\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/kit boat\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/kit peasant\\\",\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\"\\\\n \\\",\\\"color\\\":\\\"reset\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"Medieval Fantasy Survival RPG\\\\n\\\\n\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba \\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\"Clan & FactionMob\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Clans\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Portals\\\",\\\"color\\\":\\\"dark_blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Portals\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba \\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\"PvP Events\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/ConquerPoints\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Dungeons & Raids\\\",\\\"color\\\":\\\"dark_blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Dungeons_%26_Raids\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba \\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\"Ranks\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Ranks\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba \\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\"Rentable Shops\\\",\\\"color\\\":\\\"dark_blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Marketstalls\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Blueprints\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Blueprints\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Pumpkinbandit\\\",\\\"color\\\":\\\"dark_blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Pumpkinbandit\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Cannons\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Cannons\\\"}},{\\\"text\\\":\\\"\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u263d\\\\u25ba\\\",\\\"color\\\":\\\"dark_gray\\\"},{\\\"text\\\":\\\" \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Roads\\\",\\\"color\\\":\\\"dark_blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Roads\\\"}}]\",\"[\\\"\\\",{\\\"text\\\":\\\"You can:\\\\n\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" PvP\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714 \\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\"Build Auto Farms\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" Build Traps\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" Steal Stuff & Loot\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" Use Optifine\\\\n\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Tip:\\\",\\\"bold\\\":true},{\\\"text\\\":\\\" Use \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/rankup\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" and \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"/stats\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" to unlock rewards!\\\\n \\\",\\\"color\\\":\\\"reset\\\"}]\",\"[\\\"\\\",{\\\"text\\\":\\\"Behaviour we expect:\\\",\\\"bold\\\":true},{\\\"text\\\":\\\"\\\\n\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" Be Polite\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" Help others\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" Play together\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2714\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_green\\\"},{\\\"text\\\":\\\" Be Mature\\\\n\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2716\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\" No Chat Spam\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2716\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\" No Cheats/Mods\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2716\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\" No Advertising\\\\n\\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"\\\\u2716\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"},{\\\"text\\\":\\\" No Bullying\\\\n\\\\nPlease read the \\\",\\\"color\\\":\\\"reset\\\"},{\\\"text\\\":\\\"Rules\\\",\\\"color\\\":\\\"blue\\\",\\\"clickEvent\\\":{\\\"action\\\":\\\"open_url\\\",\\\"value\\\":\\\"https://craftlancer.de/wiki/index.php/Rules\\\"}},{\\\"text\\\":\\\"!\\\",\\\"color\\\":\\\"reset\\\"}]\"],title:\"§6A Peasants Guide\",author:ReadMe}";
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
            return true;
        });
        
        getCommand("dungeons").setExecutor((a, b, c, d) -> {
            String commandLine = "minecraft:give " + a.getName()
                    + " written_book{pages:['[\"\",{\"text\":\"The Dungeon Guide\",\"bold\":true},{\"text\":\"\\\\n\\\\nTo get a taste of dungeons you should start by challenging the \",\"color\":\"reset\"},{\"text\":\"Hoolis brothers\",\"bold\":true},{\"text\":\" at the \",\"color\":\"reset\"},{\"text\":\"spawn\",\"bold\":true},{\"text\":\"!\\\\n\\\\nUse \",\"color\":\"reset\"},{\"text\":\"/spawn\",\"bold\":true,\"color\":\"dark_green\"},{\"text\":\" or use portal: \",\"color\":\"reset\"},{\"text\":\"stockades\",\"color\":\"dark_purple\"},{\"text\":\"\\\\n\\\\nThis is an entry level dungeon and keep inventory is on.\",\"color\":\"reset\"}]','[\"\",{\"text\":\"Finding Dungeons\",\"bold\":true},{\"text\":\"\\\\n\\\\nAll Dungeons are marked on the \",\"color\":\"reset\"},{\"text\":\"/map\",\"bold\":true,\"color\":\"dark_green\"},{\"text\":\" with their portal adresses!\\\\n\\\\nMost Dungeons can be entered at the \",\"color\":\"reset\"},{\"text\":\"valgard hub\",\"bold\":true},{\"text\":\".\\\\n\\\\nPortal: \",\"color\":\"reset\"},{\"text\":\"valgard\",\"color\":\"dark_purple\"}]','[\"\",{\"text\":\"Dungeon Progress\",\"bold\":true},{\"text\":\"\\\\n\\\\nDefeat the entry level bosses to loot keys, inorder to challenge the stronger bosses!\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u26a0 Citizens should not wander into dungeons without good gear! \\\\u26a0\",\"bold\":true}]','[\"\",{\"text\":\"Tips and Tricks\",\"bold\":true},{\"text\":\"\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6\",\"color\":\"dark_green\"},{\"text\":\" Rightclick a boss to taunt it.\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6\",\"color\":\"dark_green\"},{\"text\":\" Heroes might help you in a dungeon.\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6\",\"color\":\"dark_green\"},{\"text\":\" Dungeons have Keep Inventory \",\"color\":\"reset\"},{\"text\":\"enabled\",\"color\":\"dark_green\"},{\"text\":\".\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6\",\"color\":\"dark_red\"},{\"text\":\" Raids have Keep Inventory \",\"color\":\"reset\"},{\"text\":\"disabled\",\"color\":\"dark_red\"},{\"text\":\".\",\"color\":\"reset\"}]','[\"\",{\"text\":\"Loot and Treasure\",\"bold\":true},{\"text\":\"\\\\n\\\\nMany a Citizen has made his fortune in a dungeon.\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6\",\"color\":\"dark_green\"},{\"text\":\" Custom 3D Items\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6 \",\"color\":\"dark_green\"},{\"text\":\"Colored Items\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6\",\"color\":\"dark_green\"},{\"text\":\" Many Collectables\\\\n\",\"color\":\"reset\"},{\"text\":\"\\\\u25b6\",\"color\":\"dark_green\"},{\"text\":\" Custom Potions\\\\n \",\"color\":\"reset\"}]'],title:\"§6Dungeons and Raids\",author:Bjorn,display:{Lore:[\"Bjorn's Survival Guide to Dungeons\"]}}";
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
            
            // TODO give portal book
            
            return true;
        });
        
        getCommand("stats").setExecutor(new StatsCommand(this));
        
        getCommand("ping").setExecutor((a, b, c, d) -> {
            Player target = null;
            
            if (d.length == 1)
                target = Bukkit.getPlayer(d[0]);
            else if (a instanceof Player)
                target = (Player) a;
            
            if (target == null)
                a.sendMessage("No player found to check ping for.");
            else {
                int ping = NMSUtils.getPing(target);
                
                ChatColor pingColor = ChatColor.GREEN;
                if (ping < 100)
                    pingColor = ChatColor.GREEN;
                else if (ping < 250)
                    pingColor = ChatColor.GOLD;
                else
                    pingColor = ChatColor.RED;
                
                a.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Craft" + ChatColor.WHITE + "Citizen] " + target.getDisplayName() + ChatColor.YELLOW
                        + "'s Ping: " + pingColor + ping + ChatColor.YELLOW + " ms");
                
            }
            return true;
        });
        getCommand("cchelp").setExecutor(new CCHelpCommandHandler(this));
        getCommand("wild").setExecutor(new WildCommand(this));
        
        getCommand("countEntities").setExecutor((a, b, c, d) -> {
            if (!a.isOp())
                return true;
            
            Bukkit.getWorlds().forEach(w -> {
                Map<EntityType, EntityEntry> entityMap = new EnumMap<>(EntityType.class);
                int totalCount = 0;
                int totalActiveCount = 0;
                
                for (Entity e : w.getEntities()) {
                    boolean isActive = NMSUtils.isEntityActive(e);
                    entityMap.putIfAbsent(e.getType(), new EntityEntry());
                    entityMap.get(e.getType()).add(isActive);
                    totalCount++;
                    if (isActive)
                        totalActiveCount++;
                }
                
                a.sendMessage("Entities in World " + w.getName());
                a.sendMessage(String.format("Total: %d/%d", totalActiveCount, totalCount));
                entityMap.forEach((k, v) -> a.sendMessage(String.format("%s: %d/%d", k.name(), v.activeEntityCount, v.entityCount)));
                a.sendMessage("=========");
            });
            
            return true;
        });
        
        getCommand("giveclaimblocks").setExecutor((a, b, c, args) -> {
            if (args.length < 2)
                return false;
            
            if (!(a instanceof Player))
                return false;
            
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            Player sender = (Player) a;
            int amount = Utils.parseIntegerOrDefault(args[1], 0);
            
            if (amount <= 0) {
                MessageUtil.sendMessage(this, sender, MessageLevel.WARNING, "You must specify a positive amount of claimblocks.");
                return true;
            }
            if (!target.isOnline() && !target.hasPlayedBefore()) {
                MessageUtil.sendMessage(this, sender, MessageLevel.WARNING, "Target player not found.");
                return true;
            }
            
            PlayerData senderData = GriefPrevention.instance.dataStore.getPlayerData(sender.getUniqueId());
            PlayerData targetData = GriefPrevention.instance.dataStore.getPlayerData(target.getUniqueId());
            
            if (senderData.getRemainingClaimBlocks() < amount) {
                MessageUtil.sendMessage(this, sender, MessageLevel.WARNING, "You don't have enough claimblocks to send.");
                return true;
            }
            
            senderData.setBonusClaimBlocks(senderData.getBonusClaimBlocks() - amount);
            targetData.setBonusClaimBlocks(targetData.getBonusClaimBlocks() + amount);
            
            MessageUtil.sendMessage(this, sender, MessageLevel.INFO, String.format("You sent %d claimblocks to %s.", amount, target.getName()));
            if (target.isOnline())
                MessageUtil.sendMessage(this, target.getPlayer(), MessageLevel.INFO, String.format("%s sent you %d claimblocks.", sender.getName(), amount));
            else
                GriefPrevention.instance.dataStore.savePlayerData(target.getUniqueId(), targetData);
            
            return true;
        });
        
        customBlockRegistry = new CustomBlockRegistry(this);
        getCommand("customblock").setExecutor(new CustomBlockCommandHandler(this, customBlockRegistry));
        Bukkit.getPluginManager().registerEvents(customBlockRegistry, this);
        
        getCommand("fixitems").setExecutor(CLStuff::fixItem);
        
        rewardsManager = new RewardsManager(this);
        getCommand("rewards").setExecutor(new RewardsCommandHandler(this, rewardsManager));
        
        rankings = new Rankings(this);
        recolor = new RecolorCommand();
        getCommand("rankings").setExecutor(new RankingsCommandHandler(this, rankings));
        getCommand("recolor").setExecutor(recolor);
        
        getCommand("craft").setExecutor(new CraftCommand());
        getCommand("centermap").setExecutor(new CenterMapCommand(this));
        
        heroes = new Heroes(this);
        
        getCommand("heroes").setExecutor(new HeroesCommandHandler(this, heroes));
        
        flag = new WGNoDropFlag(this);
        serverQuests = new ServerQuests(this);
        
        tokens = new ModelToken(this);
        exploNerf = new ExplosionRegulator(this);
        Bukkit.getPluginManager().registerEvents(recolor, this);
        Bukkit.getPluginManager().registerEvents(exploNerf, this);
        Bukkit.getPluginManager().registerEvents(tokens, this);
        Bukkit.getPluginManager().registerEvents(new CLAntiCheat(this), this);
        Bukkit.getPluginManager().registerEvents(new LagFixes(this), this);
        Bukkit.getPluginManager().registerEvents(new AFKListener(this), this);
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new PreventCMDUpgrade(this), this);
        adminShop = new AdminShopManager(this);
        Bukkit.getPluginManager().registerEvents(adminShop, this);
        
        citizenSets = new CitizenSetsManager(this);
        getCommand("citizensets").setExecutor(new CitizenSetCommandHandler(this, citizenSets));
        Bukkit.getPluginManager().registerEvents(citizenSets, this);
        
        emotes = new EmoteManager(this);
        getCommand("emote").setExecutor(new EmoteCommand(emotes));
        
        //Bukkit.getPluginManager().registerEvents(new DeathMessageListener(), this);
        
        getCommand("itembuilder").setExecutor(new ItemBuilderCommand(this));
        
        connectionMessages = new ConnectionMessages(this);
        getCommand("connectionmessages").setExecutor(new ConnectionMessagesCommandHandler(this, connectionMessages));
        Bukkit.getPluginManager().registerEvents(connectionMessages, this);
        
        donatorTicketRegistry = new DonatorTicketRegistry(this);
        getCommand("donatortickets").setExecutor(new DonatorTicketCommandHandler(this, donatorTicketRegistry));
        
        inventoryManagement = new InventoryManagement(this);
        Bukkit.getPluginManager().registerEvents(inventoryManagement, this);
        getCommand("inventorymanagement").setExecutor(new InventoryManagementCommandHandler(this, inventoryManagement));
        if (Bukkit.getPluginManager().getPlugin("CombatLogX") != null)
            Bukkit.getPluginManager().registerEvents(new CombatLogXListener(), this);
        
        new LambdaRunnable(this::save).runTaskTimer(this, 18000L, 18000L);
        
        this.mobControl = new MobControl(this);
        new ItemCooldowns(this);
        
        try {
            arenaGUI = new ArenaGUI(this);
        } catch (Exception e) {
            e.printStackTrace();
            // we don't want things to crash just because someone messed up something
        }
        
        Tablist tablist = new Tablist(this);
        getCommand("tablistreload").setExecutor(tablist);
    }
    
    private static boolean fixItem(CommandSender a, Command b, String c, String[] d) {
        if (!(a instanceof Player))
            return true;
        
        Player p = (Player) a;
        PlayerInventory inv = p.getInventory();
        int fixed = 0;
        
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            Material type = item != null ? item.getType() : Material.AIR;
            
            switch (type) {
                case INK_SAC:
                case MUSIC_DISC_CHIRP:
                case MUSIC_DISC_WAIT:
                case TRIPWIRE_HOOK:
                case COMPASS:
                case STONE:
                case LILY_PAD:
                case CHEST:
                case PLAYER_HEAD:
                    break;
                default:
                    continue;
            }
            
            YamlConfiguration config = new YamlConfiguration();
            config.set("item", item);
            YamlConfiguration config2 = YamlConfiguration.loadConfiguration(new StringReader(config.saveToString()));
            ItemStack newItem = config2.getItemStack("item");
            
            if (newItem != null && item != null && !item.isSimilar(newItem)) {
                fixed++;
                inv.setItem(i, newItem);
            }
        }
        
        MessageUtil.sendMessage(CLStuff.getInstance(), a, MessageLevel.INFO, String.format("Fixed %d items in your Inventory.", fixed));
        return true;
    }
    
    /* Temporary conversion of items */
    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        fixInventory(event.getPlayer().getInventory());
    }
    
    @EventHandler
    public void onInvClose(PlayerJoinEvent event) {
        fixInventory(event.getPlayer().getInventory());
    }
    
    public static void fixInventory(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            Material type = item != null ? item.getType() : Material.AIR;
            
            switch (type) {
                case INK_SAC:
                case MUSIC_DISC_CHIRP:
                case MUSIC_DISC_WAIT:
                case TRIPWIRE_HOOK:
                case COMPASS:
                case STONE:
                case LILY_PAD:
                case CHEST:
                case PLAYER_HEAD:
                    break;
                default:
                    continue;
            }
            
            YamlConfiguration config = new YamlConfiguration();
            config.set("item", item);
            YamlConfiguration config2 = YamlConfiguration.loadConfiguration(new StringReader(config.saveToString()));
            ItemStack newItem = config2.getItemStack("item");
            
            if (newItem != null && item != null && !item.isSimilar(newItem)) {
                inv.setItem(i, newItem);
            }
        }
    }
    
    public boolean isUsingDiscord() {
        return useDiscord;
    }
    
    public WGNoDropFlag getNoDropFlag() {
        return flag;
    }
    
    public ModelToken getModelToken() {
        return tokens;
    }
    
    class EntityEntry {
        int entityCount = 0;
        int activeEntityCount = 0;
        
        public void add(boolean isEntityActive) {
            this.entityCount += 1;
            this.activeEntityCount += isEntityActive ? 1 : 0;
        }
    }
    
    public Rankings getRankings() {
        return rankings;
    }
    
    @Override
    public void onDisable() {
        save();
        Bukkit.getScheduler().cancelTasks(this);
    }
    
    private void save() {
        serverQuests.save();
        rankings.save();
        tokens.save();
        exploNerf.save();
        heroes.save();
        adminShop.save();
        citizenSets.save();
        emotes.save();
        donatorTicketRegistry.save();
        connectionMessages.save();
        inventoryManagement.save();
        rewardsManager.save();
        customBlockRegistry.save();
    }
    
    public CitizenSetsManager getCitizenSets() {
        return citizenSets;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void lecternFix(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.LECTERN && event.getItemInHand().getType() == Material.WRITTEN_BOOK)
            event.setCancelled(false);
    }
    
    /*
     * Prevent merging vanishing elytra
     */
    @EventHandler
    public void onElytraCraft(CraftItemEvent event) {
        ItemStack result = event.getInventory().getResult();
        Player player = (Player) event.getWhoClicked();
        
        // is there a curse of vanishing elytra in the recipe?
        boolean isCursedElytra = Arrays.stream(event.getInventory().getMatrix()).anyMatch(item -> item != null && item.getType() == Material.ELYTRA
                && item.getEnchantmentLevel(Enchantment.VANISHING_CURSE) > 0);
        
        if (result.getType() == Material.ELYTRA && isCursedElytra) {
            event.setResult(Result.DENY);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 1F);
            player.sendMessage(ChatColor.RED + "You cannot use elytras in crafting tables!");
        }
    }
    
    /*
     * Sethome on first chest placement
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChestPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.CHEST)
            return;
        
        Player p = event.getPlayer();
        if (p.getStatistic(Statistic.USE_ITEM, Material.CHEST) != 0)
            return;
        
        event.getPlayer().performCommand("sethome");
        p.sendMessage(ChatColor.GOLD + "This happened because you placed down your first chest.");
        p.sendMessage(ChatColor.GOLD + "You can change your spawnpoint at any time using /sethome.");
    }
    
    /*
     * Prevent custom helmets to be placed
     */
    @EventHandler
    public void onPumpkinPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        
        if (item.getType() == Material.CARVED_PUMPKIN && item.getItemMeta().hasCustomModelData())
            event.setCancelled(true);
    }
}
