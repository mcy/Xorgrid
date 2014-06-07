package com.xorinc.xorgrid.config;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.octagami.claimit.util.LoadItemUtil;
import com.xorinc.xorgrid.Xorgrid;
import com.xorinc.xorgrid.generator.GridGenerator;
import com.xorinc.xorgrid.generator.Preset;
import com.xorinc.xorgrid.generator.Preset.LootContainer;


public class PresetConfig {

	private static final String CONFIG_NAME = "presets.yml";
	private static Xorgrid plugin = JavaPlugin.getPlugin(Xorgrid.class);
	
	
	public static Map<String, GridGenerator> load() {
		
		Map<String, GridGenerator> data = new HashMap<String, GridGenerator>();
		
    	if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
		
		File f = new File(plugin.getDataFolder(), CONFIG_NAME);
        if (!f.exists()) {
            try {
            	f.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Unable to create " + CONFIG_NAME + "!");
                return data;
            }
        }

        YamlConfiguration conf = new YamlConfiguration();

        try {
            conf.load(f);
        } catch (Exception e) {
            plugin.getServer().getLogger().severe("Unable to load " + CONFIG_NAME);
            plugin.getServer().getLogger().severe("Error:");
            plugin.getServer().getLogger().severe(e.getMessage());
            return data;
        }
		
        for(String key : conf.getKeys(false)){
        	
        	try {
        		
        		if(!conf.isConfigurationSection(key))
        			continue;
        		
        		ConfigurationSection presetCon = conf.getConfigurationSection(key);
        		ConfigurationSection blocks = presetCon.getConfigurationSection("blocks");
        		
        		Map<Material, Integer> blockDist = new EnumMap<Material, Integer>(Material.class);
        		
        		Map<Material, Map<Byte, Integer>> dataDist = new EnumMap<Material, Map<Byte, Integer>>(Material.class);
        		
        		for(String material : blocks.getKeys(false)){
        			
        			Material mat = Material.valueOf(material);
        			
        			ConfigurationSection blockSection = blocks.getConfigurationSection(material);
        			ConfigurationSection dataSection = blockSection.getConfigurationSection("dataWeights");
        			
        			blockDist.put(mat, blockSection.getInt("weight"));
        			
        			if(dataSection != null){
        			
	        			Map<Byte, Integer> map = new HashMap<Byte, Integer>();
	        			
	        			for(String byteTag : dataSection.getKeys(false)){
	        				
	        				map.put(Byte.parseByte(byteTag), dataSection.getInt(byteTag));
	        			}
	        			
	        			dataDist.put(mat, map);
        			}
        			
        		}
        		
        		int time = presetCon.getInt("startTime");
        		
        		
        		Map<String, String> rules = new HashMap<String, String>();
        		
        		if(presetCon.contains("gamerules")){
	        		ConfigurationSection rulesCon = presetCon.getConfigurationSection("gamerules");
	        		
	        		for(String rule : rulesCon.getKeys(false)){
	        			
	        			rules.put(rule, rulesCon.getString(rule));
	        		}
        		}
        		
        		List<Integer> sections = presetCon.getIntegerList("sectionsToFill");
        		
        		Map<Biome, Integer> biomeDistribution = new EnumMap<Biome, Integer>(Biome.class);
        		ConfigurationSection biomesCon = presetCon.getConfigurationSection("biomes");
        		
        		for(String biome : biomesCon.getKeys(false)){
        			
        			biomeDistribution.put(Biome.valueOf(biome), biomesCon.getInt(biome));
        		}
        		
        		if(biomeDistribution.isEmpty())
        			biomeDistribution.put(Biome.FOREST, 1);
        		
        		Map<LootContainer, Map<ItemStack, Integer>> loot = new EnumMap<LootContainer, Map<ItemStack, Integer>>(LootContainer.class);
        		Map<LootContainer, Integer> lootAmount = new EnumMap<LootContainer, Integer>(LootContainer.class);
        		ConfigurationSection lootCon = presetCon.getConfigurationSection("loot");
        		
        		for(String type : lootCon.getKeys(false)){
        			
        			LootContainer container = LootContainer.valueOf(type);
        			ConfigurationSection lootSection = lootCon.getConfigurationSection(type);
        			
        			lootAmount.put(container, lootSection.getInt("maxItems"));
        			
        			Map<ItemStack, Integer> itemMap = new HashMap<ItemStack, Integer>();
        			for(Map<?, ?> map : lootSection.getMapList("items")){
        				
        				int distr = (Integer) map.get("weight");
        				
        				MemoryConfiguration item = new MemoryConfiguration();
        				item.addDefaults((Map<String, Object>) map.get("item"));
        				itemMap.put(LoadItemUtil.loadRewardItem(item), distr);
        			}
        			
        			loot.put(container, itemMap);
        		}
       		
        		Map<EntityType, Integer> spawnerTypes = new EnumMap<EntityType, Integer>(EntityType.class);
        		
        		if(presetCon.contains("spawnerTypes")){
	        		ConfigurationSection entities = presetCon.getConfigurationSection("spawnerTypes");
	        		
	        		for(String type : entities.getKeys(false)){
	        			
	        			spawnerTypes.put(EntityType.valueOf(type), entities.getInt(type));
	        		}     		
        		}
        		if(spawnerTypes.isEmpty())
        			spawnerTypes.put(EntityType.ZOMBIE, 1);
        		
        		String[] enderPortal = presetCon.getString("enderPortal").split(",");
        		Vector portal = new Vector(Integer.parseInt(enderPortal[0]), Integer.parseInt(enderPortal[1]), Integer.parseInt(enderPortal[2]));
        		
        		Preset preset = new Preset(blockDist, dataDist, time, rules, sections, biomeDistribution, loot, lootAmount, spawnerTypes, portal);
        		
        		data.put(key, new GridGenerator(preset));
        	}
        	catch(Exception e) {
        		
        		plugin.getLogger().severe("Error loading preset '" + key + "'");
        		e.printStackTrace();
        		
        	}
        }
        
		return data;
	}
	
}
