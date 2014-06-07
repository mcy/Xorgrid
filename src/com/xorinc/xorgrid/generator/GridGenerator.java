package com.xorinc.xorgrid.generator;

import static com.xorinc.xorgrid.Range.*; //com.octagami.mayhemlib.misc.Range.range;
import static org.bukkit.Material.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

//import com.octagami.mayhemlib.string.StringUtil;
import com.xorinc.xorgrid.generator.Preset.LootContainer;


public class GridGenerator extends ChunkGenerator {

	public static final int BUILD_LIMIT = 256;
	
	final Preset preset;
	
	BlockPopulator populator = new BlockPopulator() {

		@SuppressWarnings("deprecation")
		@Override
		public void populate(World world, Random random, Chunk chunk) {

			Biome b = preset.biomes[random.nextInt(preset.biomes.length)];
			
			for(int i : range(0, 16))
				for(int j : range(0, 16))
					chunk.getBlock(i, 0, j).setBiome(b);;
			
			for(int s : range(0, BUILD_LIMIT/16)){
				
				if(!preset.sections[s])
					continue;
				
				for(int ix : range(0, 4)){
					for(int iy : range(0, 4)){
						for(int iz : range(0, 4)){
							
							int dx = ix*4, dy = iy*4 + s*16, dz = iz*4;
														
							Material mat = preset.materials[random.nextInt(preset.materials.length)];
							
							switch(mat) {
							
							case SAND:
							case GRAVEL:
								setBlock(chunk, dx, dy, dz, mat, random);
								if(dy != 0) setBlock(chunk, dx, dy - 1, dz, TRIPWIRE, random);
								break;
							
							case LONG_GRASS:
							case YELLOW_FLOWER:
							case RED_ROSE:
								setBlock(chunk, dx, dy, dz, GRASS, random);
								setBlock(chunk, dx, dy + 1, dz, mat, random);
								break;
						
							case DEAD_BUSH:
								setBlock(chunk, dx, dy, dz, HARD_CLAY, random);
								setBlock(chunk, dx, dy + 1, dz, mat, random);
								break;
								
							case RED_MUSHROOM:
							case BROWN_MUSHROOM:
								setBlock(chunk, dx, dy, dz, MYCEL, random);
								setBlock(chunk, dx, dy + 1, dz, mat, random);
								break;
							
							case CROPS:
							case CARROT:
							case POTATO:
							case PUMPKIN_STEM:
							case MELON_STEM:
								setBlock(chunk, dx, dy, dz, SOIL, random);
								setBlock(chunk, dx, dy + 1, dz, mat, random);
								break;
								
							case CACTUS:
								setBlock(chunk, dx, dy, dz, SAND, random);						
								setBlock(chunk, dx, dy + 1, dz, mat, random);
								if(dy != 0) setBlock(chunk, dx, dy - 1, dz, VINE, random);
								setBlock(chunk, dx, dy + 2, dz, TRIPWIRE, random);
								
								break;
								
							case SUGAR_CANE_BLOCK:
								setBlock(chunk, dx, dy, dz, DIRT, random);						
								setBlock(chunk, dx, dy + 1, dz, mat, random);
								setBlock(chunk, dx, dy + 2, dz, TRIPWIRE, random);
								
								setBlock(chunk, dx, dy, dz + 1, STATIONARY_WATER, random);
								break;
							
							case WATER_LILY:
								setBlock(chunk, dx, dy, dz, STATIONARY_WATER, random);
								setBlock(chunk, dx, dy + 1, dz, mat, random);
								break;
								
							case VINE:
								setBlock(chunk, dx, dy, dz, LEAVES, random);
								setBlock(chunk, dx + 1, dy, dz, mat, random);
								break;
								
							case NETHER_WARTS:
								setBlock(chunk, dx, dy, dz, SOUL_SAND, random);
								setBlock(chunk, dx, dy + 1, dz, mat, random);
								break;
								
							default:
								setBlock(chunk, dx, dy, dz, mat, random);
								break;
							
							}
						}
					}
				}			
			}
			
			for(BlockState state : chunk.getTileEntities()) {
				
				if(state instanceof Chest){
					
					populateInventory((InventoryHolder) state, LootContainer.CHEST, random);
				}
				
				else if(state instanceof Dispenser || state instanceof Dropper){
					
					populateInventory((InventoryHolder) state, LootContainer.TRAP, random);
				}
				
				else if(state instanceof Furnace){
					
					populateInventory((InventoryHolder) state, LootContainer.FURNACE, random);
				}
				
				else if(state instanceof CreatureSpawner){
					
					((CreatureSpawner) state).setSpawnedType(preset.spawnerTypes[random.nextInt(preset.spawnerTypes.length)]);
				}
			}
			
			if(contains(preset.endPortal, chunk)){
				
				Block center = preset.endPortal.toLocation(world).getBlock();
				
				int portal = Material.ENDER_PORTAL_FRAME.getId();
				
				// SOUTH
				center.getRelative(-1, 0,  2).setTypeIdAndData(portal, (byte) 0x2, false);
				center.getRelative( 0, 0,  2).setTypeIdAndData(portal, (byte) 0x2, false);
				center.getRelative( 1, 0,  2).setTypeIdAndData(portal, (byte) 0x2, false);
				
				// WEST
				center.getRelative(-2, 0, -1).setTypeIdAndData(portal, (byte) 0x3, false);
				center.getRelative(-2, 0,  0).setTypeIdAndData(portal, (byte) 0x3, false);
				center.getRelative(-2, 0,  1).setTypeIdAndData(portal, (byte) 0x3, false);
				
				// NORTH
				center.getRelative(-1, 0, -2).setTypeIdAndData(portal, (byte) 0x0, false);
				center.getRelative( 0, 0, -2).setTypeIdAndData(portal, (byte) 0x0, false);
				center.getRelative( 1, 0, -2).setTypeIdAndData(portal, (byte) 0x0, false);
				
				// EAST
				center.getRelative( 2, 0, -1).setTypeIdAndData(portal, (byte) 0x1, false);
				center.getRelative( 2, 0,  0).setTypeIdAndData(portal, (byte) 0x1, false);
				center.getRelative( 2, 0,  1).setTypeIdAndData(portal, (byte) 0x1, false);
				
			}
		}
		
	};
	
	public GridGenerator(Preset preset){
		
		this.preset = preset;
	}
	
	@Override
	public byte[][] generateBlockSections(World world, Random random, int x, int z, ChunkGenerator.BiomeGrid biomes) {
		
		return new byte[BUILD_LIMIT/6][];
	}
	
	@Override
	public boolean canSpawn(World world, int x, int z){
		
		return x % 4 == 0 && z % 4 == 0;		
	}
	
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		
		return Collections.singletonList(populator);
	}
	
	private void populateInventory(InventoryHolder holder, LootContainer type, Random random) {
		
		if(preset.lootAmount.get(type) == 0)
			return;
		
		ItemStack[] pool = preset.loot.get(type);
		
		if(pool.length == 0)
			return;
		
		if(holder instanceof Furnace){
			
			Furnace f = (Furnace) holder;			
			f.getInventory().setFuel(cloneAndAdjust(pool[random.nextInt(pool.length)], random));
		}
		else {
		
			Inventory inv = holder.getInventory();	
			for(int i : range(0, random.nextInt(preset.lootAmount.get(type)) + 1)){
				
				inv.setItem(random.nextInt(inv.getSize()), cloneAndAdjust(pool[random.nextInt(pool.length)], random));
				
			}
		}
		
	}
	
	private static ItemStack cloneAndAdjust(ItemStack i, Random random) {
		
		ItemStack copy = i.clone();
		
		copy.setAmount(1 + random.nextInt(i.getAmount()));
		
		return copy;
	}
	
    @SuppressWarnings("deprecation")
	private void setBlock(Chunk c, int x, int y, int z, Material m, Random random) {
        
    	Block b = c.getBlock(x, y, z);
    	byte data = 0;
    	
		if(preset.data.containsKey(m)){
			
			byte[] dataarr = preset.data.get(m);
			data = dataarr[random.nextInt(dataarr.length)];
			
		}
    	
		BlockState state = b.getState();
		
		state.setType(m);
		state.setRawData(data);
		
		state.update(true, false);		
    }
    
    private static boolean contains(Vector v, Chunk c) {
    	
    	int x = v.getBlockX(), y = v.getBlockY(), z = v.getBlockZ();
    	
    	return x / 16 == c.getX() && z / 16 == c.getZ() && 0 < y && y < BUILD_LIMIT && x % 16 > 1 && x % 16 < 15 && z % 16 > 1 && z % 16 < 15;
    }
	
}
