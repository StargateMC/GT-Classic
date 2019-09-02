package gtclassic.fluid;

import java.util.Random;

import javax.annotation.Nonnull;

import gtclassic.material.GTMaterial;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GTFluidBlockGas extends GTFluidBlock {

	public GTFluidBlockGas(GTMaterial mat) {
		super(mat);
	}

	@Override
	public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state,
			@Nonnull Random rand) {
		// tries to merge less than full blocks
		if (world.getBlockState(pos.up()).getBlock().equals(this)) {
			if (!isSourceBlock(world, pos.up()) && !isSourceBlock(world, pos)) {
				// MABYEDO someday make this sum the two levels
				world.setBlockState(pos.up(), this.getDefaultState(), 2);
			}
		}
		// moves the gas block up and dissipates
		if (world.isAirBlock(pos.up())) {
			if (state.getValue(LEVEL) < 7) {
				world.setBlockState(pos.up(), state.withProperty(LEVEL, state.getValue(LEVEL) + 1), 2);
			}
			world.setBlockToAir(pos);
		}
	}
}