package com.xorinc.xorgrid;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import com.xorinc.xorgrid.config.PresetConfig;
import com.xorinc.xorgrid.generator.GridGenerator;


public class Xorgrid extends JavaPlugin {

	private Map<String, GridGenerator> generators;
	
	@Override
	public void onEnable(){
		
		generators = PresetConfig.load();
	}
	
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String world, String id){
		
		 return generators.get(id);
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] args){
		
		((Player) s).teleport(new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3])));
		return true;
	}
	
}
