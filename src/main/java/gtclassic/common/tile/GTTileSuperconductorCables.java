package gtclassic.common.tile;

import gtclassic.api.tile.GTTileBaseSuperconductorCable;
import net.minecraft.init.Blocks;
import ic2.api.energy.EnergyNet;

public class GTTileSuperconductorCables {

	public static class SuperconductorMAX extends GTTileBaseSuperconductorCable {

		public SuperconductorMAX() {
		}

		@Override
		public double getConductorBreakdownEnergy() {
			return (8 << 45 * 2);
		}

		@Override
		public double getInsulationBreakdownEnergy() {
			return (8 << 45 * 2);
		}

		@Override
		public double getInsulationEnergyAbsorption() {
			return (8 << 45 * 2);
		}

		@Override
		public void removeConductor() {
			this.world.setBlockToAir(this.getPos());
			this.world.setBlockState(this.getPos(), Blocks.FIRE.getDefaultState());
		}
	}

	public static class SuperconductorIV extends GTTileBaseSuperconductorCable {

		public SuperconductorIV() {
		}

		@Override
		public double getConductorBreakdownEnergy() {
			return (8 << 15 * 2) + 1;
		}

		@Override
		public double getInsulationBreakdownEnergy() {
			return (8 << 15 * 2) + 1;
		}

		@Override
		public double getInsulationEnergyAbsorption() {
			return (8 << 15 * 2) + 1;
		}

		@Override
		public void removeConductor() {
			this.world.setBlockToAir(this.getPos());
			this.world.setBlockState(this.getPos(), Blocks.FIRE.getDefaultState());
		}
	}

	public static class SuperconductorHV extends GTTileBaseSuperconductorCable {

		public SuperconductorHV() {
		}

		@Override
		public double getConductorBreakdownEnergy() {
			return EnergyNet.instance.getPowerFromTier(6) + 1;
		}

		@Override
		public double getInsulationBreakdownEnergy() {
			return EnergyNet.instance.getPowerFromTier(6) + 1;
		}

		@Override
		public double getInsulationEnergyAbsorption() {
			return EnergyNet.instance.getPowerFromTier(6) + 1;
		}

		@Override
		public void removeConductor() {
			this.world.setBlockToAir(this.getPos());
			this.world.setBlockState(this.getPos(), Blocks.FIRE.getDefaultState());
		}
	}
}
