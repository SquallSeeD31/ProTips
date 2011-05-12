package cc.co.squallseed31.protips;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Protips extends JavaPlugin {
	
    protected static final Logger log = Logger.getLogger("Minecraft");
	protected static PermissionHandler Permissions = null;
	
	//Set debugging true to see debug messages
	private static final Boolean debugging = false;
	
	@Override
	public void onDisable() {
	    String strEnable = "[" + Config.name + "] " + Config.version + " disabled.";
	    log.info(strEnable);
	    Config.save();
	}

	@Override
	public void onEnable() {
	    PluginManager pm = getServer().getPluginManager();

	    setupPermissions();
	    
	    Config.load(this);
		Config.save();
		
		Config.taskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new TipTimer(this), 20, 20);
		if (Config.taskId == -1) {
			log.severe("[" + Config.name + "] Error scheduling tips, disabling plugin.");
			pm.disablePlugin(this);
		}

	    String strEnable = "[" + Config.name + "] " + Config.version + " enabled.";
	    log.info(strEnable);
	}
	
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
	    Player player = null;
	    String senderName = null;
	    
	    //onCommand supports console sender, so we have to cast player
	    if (sender instanceof Player) {
		    player = (Player)sender;
		    senderName = player.getName();
	    } else {
	    	senderName = Config.consoleSender;
	    }
	    return true;
  }
	
  public void setupPermissions() {
    Plugin test = getServer().getPluginManager()
      .getPlugin("Permissions");

    if (Permissions == null)
      if (test != null) {
        getServer().getPluginManager().enablePlugin(test);
        Permissions = ((Permissions)test).getHandler();
      } else {
        log.info("[" + Config.name + "] version " + Config.version + 
          "requires Permissions, disabling...");
        getServer().getPluginManager().disablePlugin(this);
      }
  }
  
  public static void debug(String message) {
	  if (debugging) {
		  log.info(message);
	  }
  }
  
}
