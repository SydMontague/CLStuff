package de.craftlancer.clstuff;

import de.craftlancer.clstuff.adminshop.AdminShopManager;
import de.craftlancer.clstuff.afk.AFKListener;
import de.craftlancer.clstuff.arena.ArenaGUI;
import de.craftlancer.clstuff.citizensets.CitizenSetsManager;
import de.craftlancer.clstuff.commands.CLStuffCommands;
import de.craftlancer.clstuff.commands.CenterMapCommand;
import de.craftlancer.clstuff.commands.CraftCommand;
import de.craftlancer.clstuff.commands.ItemBuilderCommand;
import de.craftlancer.clstuff.commands.StatsCommand;
import de.craftlancer.clstuff.commands.WildCommand;
import de.craftlancer.clstuff.connectionmessages.ConnectionMessages;
import de.craftlancer.clstuff.deathmessages.DeathMessageCommandHandler;
import de.craftlancer.clstuff.deathmessages.DeathMessageListener;
import de.craftlancer.clstuff.economy.BankManager;
import de.craftlancer.clstuff.economy.ToggleBalanceDisplay;
import de.craftlancer.clstuff.emotes.EmoteManager;
import de.craftlancer.clstuff.explosionregulator.ExplosionRegulator;
import de.craftlancer.clstuff.help.CCHelpCommandHandler;
import de.craftlancer.clstuff.heroes.Heroes;
import de.craftlancer.clstuff.inventorymanagement.InventoryManagement;
import de.craftlancer.clstuff.mobcontrol.ItemCooldowns;
import de.craftlancer.clstuff.mobcontrol.MobControl;
import de.craftlancer.clstuff.navigation.NavigationCommandHandler;
import de.craftlancer.clstuff.premium.DonatorTicketRegistry;
import de.craftlancer.clstuff.premium.ModelToken;
import de.craftlancer.clstuff.premium.RecolorCommand;
import de.craftlancer.clstuff.pvp.PvPProtection;
import de.craftlancer.clstuff.rankings.Rankings;
import de.craftlancer.clstuff.resourcepack.CustomBlockRegistry;
import de.craftlancer.clstuff.rewards.RewardsManager;
import de.craftlancer.clstuff.squest.ServerQuests;
import de.craftlancer.core.CLCore;
import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.util.MessageUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.plugin.java.JavaPlugin;

import java.io.StringReader;
import java.util.Arrays;

public class CLStuff extends JavaPlugin implements Listener {
    
    private static CLStuff instance;
    
    private static String ADMIN_PERMISSION = "clstuff.admin";
    
    private WGNoDropFlag flag;
    private ServerQuests serverQuests;
    private Rankings rankings;
    private ModelToken tokens;
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
    private PvPProtection pvp;
    private ItemCooldowns itemCooldowns;
    private BankManager bankManager;
    private ToggleBalanceDisplay balanceDisplay;
    
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
        MessageUtil.register(this, prefix, ChatColor.WHITE, ChatColor.YELLOW, ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_AQUA, ChatColor.GREEN);
        
        useDiscord = Bukkit.getPluginManager().getPlugin("DiscordSRV") != null;
        
        getCommand("wiki").setExecutor(CLStuffCommands::wikiCommand);
        getCommand("voteall").setExecutor(CLStuffCommands::voteallCommand);
        getCommand("map").setExecutor(CLStuffCommands::mapCommand);
        getCommand("time").setExecutor(CLStuffCommands::timeCommand);
        getCommand("store").setExecutor(CLStuffCommands::storeCommand);
        getCommand("ping").setExecutor(CLStuffCommands::pingCommand);
        getCommand("howtoplay").setExecutor(CLStuffCommands::howtoplayCommand);
        getCommand("dungeons").setExecutor(CLStuffCommands::dungeonsCommand);
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("cchelp").setExecutor(new CCHelpCommandHandler(this));
        getCommand("wild").setExecutor(new WildCommand(this));
        getCommand("countEntities").setExecutor(CLStuffCommands::countEntitiesCommand);
        getCommand("giveclaimblocks").setExecutor(CLStuffCommands::giveClaimblocksCommand);
        getCommand("fixitems").setExecutor(CLStuffCommands::fixItem);
        getCommand("itembuilder").setExecutor(new ItemBuilderCommand(this));
        getCommand("recolor").setExecutor(new RecolorCommand(this));
        getCommand("craft").setExecutor(new CraftCommand());
        getCommand("centermap").setExecutor(new CenterMapCommand(this));
        getCommand("navigation").setExecutor(new NavigationCommandHandler(this, CLCore.getInstance().getNavigationManager()));
        getCommand("tablistreload").setExecutor(new Tablist(this));
        getCommand("deathmessages").setExecutor(new DeathMessageCommandHandler(this));
        
        this.customBlockRegistry = new CustomBlockRegistry(this);
        this.rewardsManager = new RewardsManager(this);
        this.rankings = new Rankings(this);
        this.heroes = new Heroes(this);
        this.flag = new WGNoDropFlag(this);
        this.serverQuests = new ServerQuests(this);
        this.tokens = new ModelToken(this);
        this.exploNerf = new ExplosionRegulator(this);
        this.adminShop = new AdminShopManager(this);
        this.bankManager = new BankManager(this);
        this.balanceDisplay = new ToggleBalanceDisplay(this);
        
        Bukkit.getPluginManager().registerEvents(new CLAntiCheat(this), this);
        Bukkit.getPluginManager().registerEvents(new LagFixes(this), this);
        Bukkit.getPluginManager().registerEvents(new AFKListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PreventCMDUpgrade(this), this);
        Bukkit.getPluginManager().registerEvents(new DeathMessageListener(this), this);
        
        this.citizenSets = new CitizenSetsManager(this);
        this.emotes = new EmoteManager(this);
        this.connectionMessages = new ConnectionMessages(this);
        this.donatorTicketRegistry = new DonatorTicketRegistry(this);
        this.inventoryManagement = new InventoryManagement(this);
        this.mobControl = new MobControl(this);
        this.itemCooldowns = new ItemCooldowns(this);
        //this.pvp = new PvPProtection(this);
        
        try {
            this.arenaGUI = new ArenaGUI(this);
        } catch (Exception e) {
            e.printStackTrace();
            // we don't want things to crash just because someone messed up something
        }
        
        if (Bukkit.getPluginManager().getPlugin("CombatLogX") != null)
            Bukkit.getPluginManager().registerEvents(new CombatLogXListener(), this);
        
        Bukkit.getPluginManager().registerEvents(this, this);
        new LambdaRunnable(this::save).runTaskTimer(this, 18000L, 18000L);
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
    
    public PvPProtection getPvPProtection() {
        return pvp;
    }
    
    public Rankings getRankings() {
        return rankings;
    }
    
    public ToggleBalanceDisplay getBalanceDisplay() {
        return balanceDisplay;
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
        bankManager.save();
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
