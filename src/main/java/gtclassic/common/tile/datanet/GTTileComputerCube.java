package gtclassic.common.tile.datanet;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gtclassic.api.helpers.GTUtility;
import gtclassic.api.interfaces.IGTDataNetObject;
import gtclassic.api.interfaces.IGTDebuggableTile;
import gtclassic.common.GTBlocks;
import gtclassic.common.container.GTContainerComputerCube;
import gtclassic.common.util.GTIBlockFilters;
import ic2.core.RotationList;
import ic2.core.block.base.tile.TileEntityElecMachine;
import ic2.core.inventory.base.IHasGui;
import ic2.core.inventory.container.ContainerIC2;
import ic2.core.inventory.gui.GuiComponentContainer;
import ic2.core.inventory.management.InventoryHandler;
import ic2.core.util.helpers.AabbUtil;
import ic2.core.util.helpers.AabbUtil.BoundingBox;
import ic2.core.util.helpers.AabbUtil.Processor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GTTileComputerCube extends TileEntityElecMachine
		implements IHasGui, IGTDebuggableTile, ITickable, IGTDataNetObject {

	boolean isOnlyComputer = true;
	private Processor task = null;
	private AabbUtil.IBlockFilter filter = new GTIBlockFilters.DataNetFilter();
	public Set<BlockPos> dataNet = new HashSet<>();
	public int nodeCount = 0;

	public GTTileComputerCube() {
		super(0, 512);
		this.maxEnergy = 10000;
		this.addGuiFields(new String[] { "nodeCount" });
	}

	@Override
	public void onUnloaded() {
		if (this.isSimulating() && this.hasNodes()) {
			for (BlockPos pPos : dataNet) {
				TileEntity worldTile = world.getTileEntity(pPos);
				if (worldTile instanceof GTTileBaseDataNode) {
					((GTTileBaseDataNode) worldTile).computer = null;
				}
			}
		}
		super.onUnloaded();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Class<? extends GuiScreen> getGuiClass(EntityPlayer player) {
		return GuiComponentContainer.class;
	}

	@Override
	public ContainerIC2 getGuiContainer(EntityPlayer player) {
		return new GTContainerComputerCube(player.inventory, this);
	}

	@Override
	protected void addSlots(InventoryHandler handler) {
		// nothing for now
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return !this.isInvalid();
	}

	@Override
	public void onGuiClosed(EntityPlayer entityPlayer) {
	}

	@Override
	public boolean hasGui(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean canRemoveBlock(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean canSetFacing(EntityPlayer player, EnumFacing facing) {
		return facing != getFacing() && facing.getAxis().isHorizontal();
	}

	@Override
	public boolean supportsNotify() {
		return true;
	}

	@Override
	public double getWrenchDropRate() {
		return 1.0D;
	}

	@Override
	public void update() {
		this.setActive(this.isOnlyComputer && this.energy > this.getNodeCount());
		if (world.getTotalWorldTime() % GTUtility.DATA_NET_RESET_RATE == 0) {
			this.isOnlyComputer = true;
			this.dataNet.clear();
		}
		// set energy drain to node count, this includes itself
		if (this.getNodeCount() > 0) {
			this.useEnergy(this.getNodeCount());
		}
		if (this.isOnlyComputer) {
			if (task != null && world.isAreaLoaded(pos, 16)) {
				task.update();
				if (!task.isFinished()) {
					return;
				}
				// getting all objects but skipping cables as nodes
				for (BlockPos tPos : task.getResults()) {
					if (!world.isBlockLoaded(tPos)) {
						continue;
					}
					if (world.getBlockState(tPos).getBlock() != GTBlocks.dataCable) {
						this.dataNet.add(tPos);
					}
				}
				this.nodeCount = 0;
				if (!dataNet.isEmpty()) {
					for (BlockPos pPos : dataNet) {
						TileEntity worldTile = world.getTileEntity(pPos);
						if (worldTile != this && worldTile instanceof GTTileComputerCube) {
							((GTTileComputerCube) worldTile).isOnlyComputer = false;
						}
						if (worldTile instanceof GTTileBaseDataNode) {
							((GTTileBaseDataNode) worldTile).computer = this.energy > 0 ? this : null;
							this.nodeCount++;
						}
					}
				}
				this.updateGui();
			}
			if (world.getTotalWorldTime() % GTUtility.DATA_NET_SEARCH_RATE == 0) {
				if (!world.isAreaLoaded(pos, 16))
					return;
				task = AabbUtil.createBatchTask(world, new BoundingBox(this.pos, 256), this.pos, RotationList.ALL, filter, 64, false, false, true);
				task.update();
			}
		}
	}

	public boolean hasNodes() {
		return this.dataNet != null && !this.dataNet.isEmpty();
	}

	public int getNodeCount() {
		return this.nodeCount;
	}

	@Override
	public void getData(Map<String, Boolean> data) {
		data.put("Nodes in network: " + this.getNodeCount(), false);
	}

	public void updateGui() {
		this.getNetwork().updateTileGuiField(this, "nodeCount");
	}
}
