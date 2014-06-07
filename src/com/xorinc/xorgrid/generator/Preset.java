package com.xorinc.xorgrid.generator;

import static com.xorinc.xorgrid.Range.*; //com.octagami.mayhemlib.misc.Range.range;
import static org.bukkit.Material.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

//import com.octagami.mayhemlib.number.NumberUtil;


public class Preset {

	private static Set<Material> blacklist = EnumSet.of(AIR, SAPLING, BED, POWERED_RAIL, DETECTOR_RAIL, PISTON_EXTENSION, PISTON_MOVING_PIECE,
														TORCH, FIRE, REDSTONE_WIRE, SIGN_POST, WOOD_DOOR, LADDER, RAILS, WALL_SIGN, LEVER,
														STONE_PLATE, IRON_DOOR, WOOD_PLATE, REDSTONE_TORCH_OFF, REDSTONE_TORCH_ON, STONE_BUTTON,
														SNOW, PORTAL, DIODE_BLOCK_ON, DIODE_BLOCK_OFF, TRAP_DOOR, ENDER_PORTAL, COCOA, TRIPWIRE_HOOK,
														FLOWER_POT, WOOD_BUTTON, SKULL, GOLD_PLATE, IRON_PLATE, REDSTONE_COMPARATOR_OFF,
														REDSTONE_COMPARATOR_ON, ACTIVATOR_RAIL, CARPET, DOUBLE_PLANT);
	
	Material[] materials;
	Map<Material, byte[]> data = new EnumMap<Material, byte[]>(Material.class);
	
	int fixedTime;
	Map<String, String> gamerules;
	
	boolean[] sections = new boolean[GridGenerator.BUILD_LIMIT / 6];
	
	Biome[] biomes;
	
	Map<LootContainer, ItemStack[]> loot = new EnumMap<LootContainer, ItemStack[]>(LootContainer.class);
	Map<LootContainer, Integer> lootAmount;
	EntityType[] spawnerTypes;
	
	Vector endPortal;
	
	public enum LootContainer {CHEST, FURNACE, TRAP}
	
	public Preset(Map<Material, Integer> distribution, Map<Material, Map<Byte, Integer>> dataMap, int time, Map<String, String> gamerules, List<Integer> sectionsToFill, 
			Map<Biome, Integer> biomes, Map<LootContainer, Map<ItemStack, Integer>> loot, Map<LootContainer, Integer> lootAmount, Map<EntityType, Integer> spawnerTypes, Vector endPortal) {
		
		List<Material> list = new ArrayList<Material>();
		
		for(Entry<Material, Integer> e : distribution.entrySet()) {
			
			Material m = e.getKey();
			
			if(!m.isBlock() || blacklist.contains(m))
				continue;
			
			for(int i : range(0, e.getValue())){
				
				list.add(m);
			}
			
			if(dataMap.containsKey(m)){
				List<Byte> bytes = new ArrayList<Byte>();
				
				
				for(Entry<Byte, Integer> f : dataMap.get(m).entrySet()){
					
					byte b = f.getKey();
					
					if(b >= 16)
						continue;
					
					for(int j : range(0, f.getValue())){
						
						bytes.add(b);
					}
				}
				
				data.put(m, toByteArray(bytes));
			}
		
		}
		
		this.materials = list.toArray(new Material[list.size()]);
		
		this.fixedTime = time;
		this.gamerules = new HashMap<String, String>(gamerules);
		
		for(int i : sectionsToFill) {
			
			if(i < sections.length){
				
				sections[i] = true;
			}
		}
		
		List<Biome> listB = new ArrayList<Biome>();
		
		for(Entry<Biome, Integer> e : biomes.entrySet()) {
			
			Biome b = e.getKey();
			
			for(int i : range(0, e.getValue())){
				
				listB.add(b);
			}
		}
		
		this.biomes = listB.toArray(new Biome[listB.size()]);
		
		for(Entry<LootContainer, Map<ItemStack, Integer>> e : loot.entrySet()){
			
			LootContainer l = e.getKey();
			
			List<ItemStack> listI = new ArrayList<ItemStack>();
			
			for(Entry<ItemStack, Integer> f : e.getValue().entrySet()) {
				
				ItemStack item = f.getKey();
				
				if(item == null)
					continue;
				
				for(int i : range(0, f.getValue())){
					
					listI.add(item);
				}
			}
			
			this.loot.put(l, listI.toArray(new ItemStack[listI.size()]));
			
		}
		
		this.lootAmount = new EnumMap<LootContainer, Integer>(lootAmount);
		
		List<EntityType> listE = new ArrayList<EntityType>();
		
		for(Entry<EntityType, Integer> e : spawnerTypes.entrySet()) {
			
			EntityType en = e.getKey();
			
			for(int i : range(0, e.getValue())){
				
				listE.add(en);
			}
		}
		
		this.spawnerTypes = listE.toArray(new EntityType[listE.size()]);
		
		this.endPortal = endPortal.clone();
	}
	
	public static byte[] toByteArray(List<Byte> list) {
		
		byte[] result = new byte[list.size()];
		
		for(int i : range(0, result.length))
			result[i] = list.get(i);
		
		return result;
	}
}
