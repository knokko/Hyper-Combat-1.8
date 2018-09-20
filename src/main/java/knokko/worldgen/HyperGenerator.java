package knokko.worldgen;

import java.util.Random;

import knokko.blocks.HyperBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class HyperGenerator implements IWorldGenerator {
	
	public static void register(){
		GameRegistry.registerWorldGenerator(new HyperGenerator(), 1);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider generator, IChunkProvider provider) {
		generateOres(world, random, HyperBlocks.copperOre.getDefaultState(), chunkX * 16, chunkZ * 16, 64, 8, 10);
		generateOres(world, random, HyperBlocks.siliconOre.getDefaultState(), chunkX * 16, chunkZ * 16, 30, 6, 4);
	}
	
	private void generateOres(World world, Random random, IBlockState ore, int blockX, int blockZ, int maxHeight, int size, int amount){
		for (int i = 0; i < amount; i++) {
			int xCoord = blockX + random.nextInt(16);
			int zCoord = blockZ + random.nextInt(16);
			int yCoord = random.nextInt(maxHeight);
			new WorldGenMinable(ore, size).generate(world, random, new BlockPos(xCoord, yCoord, zCoord));
		}
	}

}
