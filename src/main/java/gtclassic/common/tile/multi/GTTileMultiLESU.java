package gtclassic.common.tile.multi;

import java.util.Map;

import gtclassic.api.interfaces.IGTDebuggableTile;
import gtclassic.api.interfaces.IGTMultiTileStatus;
import gtclassic.common.GTLang;
import gtclassic.common.container.GTContainerLESU;
import gtclassic.common.util.GTIBlockFilters;
import ic2.api.energy.tile.IMultiEnergySource;
import ic2.core.IC2;
import ic2.core.RotationList;
import ic2.core.block.base.tile.TileEntityElectricBlock;
import ic2.core.inventory.container.ContainerIC2;
import ic2.core.platform.lang.components.base.LocaleComp;
import ic2.core.util.helpers.AabbUtil;
import ic2.core.util.helpers.AabbUtil.BoundingBox;
import ic2.core.util.helpers.AabbUtil.Processor;
import ic2.core.util.obj.IClickable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;

public class GTTileMultiLESU extends TileEntityElectricBlock
		implements IMultiEnergySource, IGTMultiTileStatus, IClickable, IGTDebuggableTile {

	private int blockCount;
	public boolean enabled = true;
	public static final int BASE_ENERGY = 10000000;
	public AabbUtil.IBlockFilter filter = new GTIBlockFilters.LESUCasingFilter(this);
	public Processor task = null;

	public GTTileMultiLESU() {
		super(3, 512, BASE_ENERGY);
		this.blockCount = 0;
		this.addGuiFields(new String[] { "blockCount", "maxEnergy" });
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.blockCount = nbt.getInteger("blockCount");
		this.maxEnergy = nbt.getInteger("maxEnergy");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("blockCount", this.blockCount);
		nbt.setInteger("maxEnergy", this.maxEnergy);
		return nbt;
	}

	@Override
	public ContainerIC2 getGuiContainer(EntityPlayer player) {
		return new GTContainerLESU(player.inventory, this);
	}

	@Override
	public LocaleComp getBlockName() {
		return GTLang.LESU;
	}

	public int getProcessRate() {
		return 32;
	}

	@Override
	public double getWrenchDropRate() {
		return 1.0D;
	}

	@Override
	public void update() {
		checkArea();
	}

	@Override
	public int getSinkTier() {
		return 1;
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		if ((amount > 32) || (amount <= 0.0D)) {
			return 0.0D;
		}
		energy = ((int) (energy + amount));
		int left = 0;
		if (energy >= maxEnergy) {
			left = energy - maxEnergy;
			energy = maxEnergy;
		}
		getNetwork().updateTileGuiField(this, "energy");
		return left;
	}

	private void checkArea() {
		if (!this.enabled) {
			this.blockCount = 0;
			this.maxEnergy = BASE_ENERGY;
			this.getNetwork().updateTileGuiField(this, "blockCount");
			this.getNetwork().updateTileGuiField(this, "maxEnergy");
			return;
		}
		if (task != null && world.isAreaLoaded(pos, 16)) {
			task.update();
			if (!task.isFinished()) {
				return;
			}
			this.blockCount = task.getResultCount();
			if (this.blockCount > 256) {
				this.blockCount = 256;
			}
			this.maxEnergy = BASE_ENERGY + (this.blockCount * 750000);
			this.getNetwork().updateTileGuiField(this, "blockCount");
			this.getNetwork().updateTileGuiField(this, "maxEnergy");
		}
		if (world.getTotalWorldTime() % 128 == 0) {
			if (!world.isAreaLoaded(pos, 16))
				return;
			task = AabbUtil.createBatchTask(world, new BoundingBox(this.pos, 256), this.pos, RotationList.ALL, filter, 64, false, false, true);
			task.update();
		}
	}

	public int getCount() {
		return blockCount;
	}

	@Override
	public int getMultipleEnergyPacketAmount() {
		return 1 + (this.blockCount / 10);
	}

	@Override
	public boolean sendMultipleEnergyPackets() {
		return this.blockCount > 9;
	}

	@Override
	public boolean getStructureValid() {
		return this.blockCount > 0;
	}

	@Override
	public boolean hasRightClick() {
		return true;
	}

	@Override
	public boolean onRightClick(EntityPlayer player, EnumHand hand, EnumFacing facing, Side side) {
		if (player.isSneaking() && player.getHeldItemMainhand().isEmpty()) {
			this.onNetworkEvent(player, 0);
			if (this.isSimulating()) {
				IC2.platform.messagePlayer(player, this.getRedstoneMode());
			}
			player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
			return true;
		}
		return false;
	}

	@Override
	public boolean hasLeftClick() {
		return false;
	}

	@Override
	public void onLeftClick(EntityPlayer var1, Side var2) {
		// Needed for interface
	}

	@Override
	public void getData(Map<String, Boolean> data) {
		data.put("Lapotron Blocks: " + this.getCount(), true);
		data.put("Energy Packets: " + this.getMultipleEnergyPacketAmount(), true);
	}
}
