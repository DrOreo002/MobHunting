package one.lindegaard.MobHunting.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.compatibility.CitizensCompat;
import one.lindegaard.MobHunting.compatibility.CustomMobsCompat;
import one.lindegaard.MobHunting.compatibility.MysteriousHalloweenCompat;
import one.lindegaard.MobHunting.compatibility.MythicMobsCompat;
import one.lindegaard.MobHunting.compatibility.TARDISWeepingAngelsCompat;
import one.lindegaard.MobHunting.util.Misc;

public class ReloadCommand implements ICommand {

	private MobHunting plugin;

	public ReloadCommand(MobHunting plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return "mobhunting.reload";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { ChatColor.GOLD + label + ChatColor.WHITE + " - to reload MobHunting configuration." };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.reload.description");
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {

		plugin.getGrindingManager().saveData();

		long starttime = System.currentTimeMillis();
		int i = 1;
		while (plugin.getDataStoreManager().isRunning() && (starttime + 10000 > System.currentTimeMillis())) {
			if (((int) (System.currentTimeMillis() - starttime)) / 1000 == i) {
				Messages.debug("saving data (%s)");
				i++;
			}
		}

		if (plugin.getConfigManager().loadConfig()) {
			int n = Misc.getOnlinePlayersAmount();
			if (n > 0) {
				Messages.debug("Reloading %s online playerSettings from the database", n);
				// reload player settings
				for (Player player : Misc.getOnlinePlayers())
					plugin.getPlayerSettingsmanager().load(player);
				// reload bounties
				if (!plugin.getConfigManager().disablePlayerBounties)
					for (Player player : Misc.getOnlinePlayers())
						plugin.getBountyManager().load(player);
				// reload achievements
				for (Player player : Misc.getOnlinePlayers())
					MobHunting.getInstance().getAchievementManager().load(player);
			}

			if (MythicMobsCompat.isSupported())
				MythicMobsCompat.loadMythicMobsData();
			if (TARDISWeepingAngelsCompat.isSupported())
				TARDISWeepingAngelsCompat.loadTARDISWeepingAngelsMobsData();
			if (CustomMobsCompat.isSupported())
				CustomMobsCompat.loadCustomMobsData();
			if (MysteriousHalloweenCompat.isSupported())
				MysteriousHalloweenCompat.loadMysteriousHalloweenMobsData();
			if (CitizensCompat.isSupported())
				CitizensCompat.loadCitizensData();

			plugin.getMessages().senderSendMessage(sender,ChatColor.GREEN + Messages.getString("mobhunting.commands.reload.reload-complete"));

		} else
			plugin.getMessages().senderSendMessage(sender,ChatColor.RED + Messages.getString("mobhunting.commands.reload.reload-error"));

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
