package io.github.white1amp;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathBan extends JavaPlugin
		implements Listener{
	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(this, this);
		if(!getDataFolder().exists()){
			getDataFolder().mkdir();
			}
		File f = new File(getDataFolder(),"config.yml");
		if(!f.exists()){
			saveDefaultConfig();
			}
		File players = new File(getDataFolder(),"Players");
		if(!players.exists()){
			players.mkdir();
		}
		DeathTimeMinus();
	}
	public boolean onCommand(CommandSender sender,Command cmd,String lable,String[] args){
		if(lable.equalsIgnoreCase("undb")){
			if(!(sender instanceof Player)){return true;} 
			Player p = (Player) sender;
			if((args.length>1) || (args.length==0)){
				return true;
			}
			if(p.isOp()==false){return true;}
			File data = new File(getDataFolder()+"\\Players\\",args[0].toLowerCase()+".yml");
				if(data.exists()){
					data.delete();
					p.sendMessage("§c已解除该玩家的死亡封禁！");
					return true;
				}
				p.sendMessage("§a该玩家不在死亡封禁列表中");
			}
		return false;
	}
	@EventHandler
	public void onPlayerJoin(PlayerLoginEvent e){
		Player p = e.getPlayer();
		File data = new File(getDataFolder()+"\\Players\\",p.getName().toLowerCase()+".yml");
		if(data.exists()){
			FileConfiguration a = YamlConfiguration.loadConfiguration(data);
			e.disallow(Result.KICK_OTHER,"§c[§4死亡封禁§c]"+"\n"+"§c剩余时间：§f"+a.getInt("DeathBanTime")+"§c秒");
			return;
		}
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity().getPlayer();
		File data = new File(getDataFolder()+"\\Players\\",p.getName().toLowerCase()+".yml");
		if(!data.exists()){
			try {
				data.createNewFile();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			FileConfiguration a = YamlConfiguration.loadConfiguration(data);
			p.kickPlayer("§c死亡封禁，剩余时间：§4"+getConfig().getInt("Time")+"§c秒");
			a.set("DeathBanTime", getConfig().getInt("Time"));
			try {
				a.save(data);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
	}
	@SuppressWarnings("deprecation")
	void DeathTimeMinus(){
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new BukkitRunnable(){
			@Override	
			public void run(){
					File f = new File(getDataFolder(),"\\Players\\");
					File[] fr = f.listFiles();
					if(fr.length==0){return;}
					for(File fpd:fr){
						String name = fpd.getName();
						if((fpd.isDirectory())||(!(name.endsWith("yml")))){return;}
						FileConfiguration a = YamlConfiguration.loadConfiguration(fpd);
						int sj = a.getInt("DeathBanTime");
						if(sj<=0){
							fpd.delete();
							return;
						}
						a.set("DeathBanTime", sj-1);
						try {
							a.save(fpd);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
			}
		}, 0L,20L);
	}
}