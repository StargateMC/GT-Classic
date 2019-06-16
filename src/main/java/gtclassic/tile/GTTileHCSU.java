package gtclassic.tile;

import gtclassic.container.GTContainerHCSU;
import ic2.api.energy.EnergyNet;
import ic2.core.block.base.tile.TileEntityElectricBlock;
import ic2.core.inventory.container.ContainerIC2;
import ic2.core.platform.lang.components.base.LangComponentHolder.LocaleBlockComp;
import ic2.core.platform.lang.components.base.LocaleComp;
import net.minecraft.entity.player.EntityPlayer;

public class GTTileHCSU extends TileEntityElectricBlock {

	public GTTileHCSU() {
		super(4, (int) EnergyNet.instance.getPowerFromTier(4), 100000000);
	}

	@Override
	public ContainerIC2 getGuiContainer(EntityPlayer player) {
		return new GTContainerHCSU(player.inventory, this);
	}

	@Override
	public int getProcessRate() {
		return 128;
	}

	@Override
	public double getWrenchDropRate() {
		return 1.0D;
	}

	@Override
	public LocaleComp getBlockName() {
		return new LocaleBlockComp(this.getBlockType().getUnlocalizedName());
	}
}