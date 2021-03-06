package gtclassic.api.tile.multi;

import gtclassic.api.interfaces.IGTMultiTileStatus;
import gtclassic.api.tile.GTTileBaseMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public abstract class GTTileMultiBaseMachine extends GTTileBaseMachine implements IGTMultiTileStatus {

	public boolean lastState;
	public boolean firstCheck = true;

	/**
	 * Constructor for a GTC Multi-block Machine.
	 * 
	 * @param slots        - int amount of slots, input, outputs, battery etc..
	 * @param upgrades     - int amount of upgrade slots
	 * @param defaultinput - int default energy input
	 * @param maxinput     - int maximum energy input
	 */
	public GTTileMultiBaseMachine(int slots, int upgrades, int defaultinput, int maxinput) {
		super(slots, upgrades, defaultinput, 100, maxinput);
		this.addGuiFields(new String[] { "lastState" });
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.lastState = nbt.getBoolean("lastState");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("lastState", lastState);
		return nbt;
	}

	@Override
	public boolean canWork() {
		boolean superCall = super.canWork();
		if (superCall) {
			if (world.getTotalWorldTime() % 256 == 0 || firstCheck) {
				lastState = checkStructure();
				firstCheck = false;
				this.getNetwork().updateTileGuiField(this, "lastState");
			}
			superCall = superCall && lastState;
		}
		return superCall;
	}

	public boolean checkStructure() {
		if (!world.isAreaLoaded(pos, 3)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean hasGui(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean getStructureValid() {
		return lastState;
	}
}