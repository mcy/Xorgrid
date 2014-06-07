package com.xorinc.xorgrid.generator;

import java.util.Map.Entry;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;


public class WorldListener implements Listener {

	@EventHandler
	public void onWorldInit(WorldInitEvent event) {
		
		World w = event.getWorld();
		
		if(!(w.getGenerator() instanceof GridGenerator))
			return;
		
		GridGenerator gen = (GridGenerator) w.getGenerator();
		
		for(Entry<String, String> e : gen.preset.gamerules.entrySet()){
			
			w.setGameRuleValue(e.getValue(), e.getValue());
		}
		
		w.setFullTime(gen.preset.fixedTime);
		w.setAnimalSpawnLimit(0);
	}
	
}
