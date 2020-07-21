package gtclassic.common.tile;

import ic2.api.classic.network.adv.IBitLevelOverride;
import ic2.api.classic.network.adv.NetworkField.BitLevel;
import ic2.api.energy.EnergyNet;
import ic2.core.block.base.tile.TileEntityTransformer;

public class GTTileSupercondensator extends TileEntityTransformer implements IBitLevelOverride {

	public GTTileSupercondensator() {
		super((8 << 15 * 2), (8 << 45 * 2), (8 << 46 * 2));
	}

        @Override
        public void update() {
            
        }
	@Override
	public BitLevel getOverride(int paramInt, String paramString) {
		return BitLevel.Bit64;
	}

	@Override
	public boolean hasOverride(int field, String paramString) {
		return field == 3;
	}

	public static int level(int tier) {
		return (int) (8 << tier * 2);
	}

	public void doFusionHeliumThings() {
		if (this.energy + 1048576 <= this.maxEnergy) {
			this.energy = this.energy + 1048576;
		}
	}
}
