package gtclassic.tile;

import java.util.ArrayList;
import java.util.List;

import gtclassic.GTBlocks;
import gtclassic.GTConfig;
import gtclassic.GTMod;
import gtclassic.container.GTContainerUUMAssembler;
import gtclassic.gui.GTGuiMachine.GTUUMAssemblerGui;
import gtclassic.helpers.GTHelperStack;
import gtclassic.material.GTMaterial;
import gtclassic.material.GTMaterialGen;
import gtclassic.util.GTFilterUUMAssembler;
import gtclassic.util.recipe.GTRecipeMultiInputList;
import ic2.api.classic.recipe.machine.MachineOutput;
import ic2.api.recipe.IRecipeInput;
import ic2.core.RotationList;
import ic2.core.block.base.tile.TileEntityElecMachine;
import ic2.core.inventory.base.IHasGui;
import ic2.core.inventory.container.ContainerIC2;
import ic2.core.inventory.filters.CommonFilters;
import ic2.core.inventory.management.AccessRule;
import ic2.core.inventory.management.InventoryHandler;
import ic2.core.inventory.management.SlotType;
import ic2.core.item.recipe.entry.RecipeInputItemStack;
import ic2.core.platform.registry.Ic2Items;
import ic2.core.util.misc.StackUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;

public class GTTileUUMAssembler extends TileEntityElecMachine implements ITickable, IHasGui {

	int digitalCount;
	int currentCost;
	int amountPer;
	public static final GTRecipeMultiInputList RECIPE_LIST = new GTRecipeMultiInputList("gt.uumassembler");
	public static final ResourceLocation GUI_LOCATION = new ResourceLocation(GTMod.MODID, "textures/gui/uumassembler.png");

	public GTTileUUMAssembler() {
		super(14, 512);
		maxEnergy = 100000;
		this.addGuiFields(new String[] { "digitalCount", "currentCost", "amountPer" });
	}

	@Override
	protected void addSlots(InventoryHandler handler) {
		handler.registerDefaultSideAccess(AccessRule.Both, RotationList.ALL);
		handler.registerDefaultSlotAccess(AccessRule.Import, 12);
		handler.registerDefaultSlotAccess(AccessRule.Export, 13);
		handler.registerDefaultSlotsForSide(RotationList.DOWN.invert(), 12);
		handler.registerDefaultSlotsForSide(RotationList.UP.invert(), 13);
		handler.registerInputFilter(CommonFilters.uuMatter, 12);
		handler.registerSlotType(SlotType.Input, 12);
		handler.registerSlotType(SlotType.Output, 13);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.digitalCount = nbt.getInteger("digitalCount");
		this.currentCost = nbt.getInteger("currentCost");
		this.amountPer = nbt.getInteger("amountPer");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("digitalCount", this.digitalCount);
		nbt.setInteger("currentCost", this.currentCost);
		nbt.setInteger("amountPer", this.amountPer);
		return nbt;
	}

	@Override
	public ContainerIC2 getGuiContainer(EntityPlayer player) {
		return new GTContainerUUMAssembler(player.inventory, this);
	}

	@Override
	public Class<? extends GuiScreen> getGuiClass(EntityPlayer var1) {
		return GTUUMAssemblerGui.class;
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return !this.isInvalid();
	}

	@Override
	public boolean hasGui(EntityPlayer var1) {
		return true;
	}

	@Override
	public void onGuiClosed(EntityPlayer var1) {
	}

	@Override
	public void update() {
		tryInputUU();
		tryAssemble();
	}

	public void tryInputUU() {
		if (this.digitalCount < 3456 && GTHelperStack.isEqual(this.getStackInSlot(12), Ic2Items.uuMatter.copy())) {
			this.getStackInSlot(12).shrink(1);
			this.digitalCount = this.digitalCount + 1;
			this.updateGui();
		}
	}

	public void tryAssemble() {
		// Is there even something to replicate?
		if (this.currentCost == 0 || this.getStackInSlot(11).isEmpty()) {
			return;
		}
		// Is there enough UU to replicate?
		if (this.digitalCount < this.currentCost) {
			return;
		}
		// Will the output stack even fit!?
		ItemStack newStack = StackUtil.copyWithSize(this.getStackInSlot(11), this.amountPer);
		if (!GTHelperStack.canMerge(newStack, this.getStackInSlot(13))) {
			return;
		}
		// Is the machine paused?
		if (this.redstoneEnabled()) {
			return;
		}
		// Ok lets do this
		if (this.energy >= 50) {
			int count = this.getStackInSlot(13).getCount() + this.amountPer;
			this.setStackInSlot(13, StackUtil.copyWithSize(newStack, count));
			this.digitalCount = this.digitalCount - this.currentCost;
			this.useEnergy(50);
			this.updateGui();
		}
	}

	public boolean redstoneEnabled() {
		return this.world.isBlockPowered(this.getPos());
	}

	@Override
	public boolean supportsNotify() {
		return true;
	}

	public void updateGui() {
		this.getNetwork().updateTileGuiField(this, "digitalCount");
		this.getNetwork().updateTileGuiField(this, "currentCost");
		this.getNetwork().updateTileGuiField(this, "amountPer");
	}

	public ResourceLocation getGuiTexture() {
		return GUI_LOCATION;
	}

	public void updateCost() {
		this.currentCost = GTFilterUUMAssembler.getCost(this.getStackInSlot(11));
		this.amountPer = GTFilterUUMAssembler.getAmountPer(this.getStackInSlot(11));
		updateGui();
	}

	@Override
	public boolean canSetFacing(EntityPlayer player, EnumFacing facing) {
		return facing != getFacing() && facing.getAxis().isHorizontal();
	}

	public int getStoredUU() {
		return this.digitalCount;
	}

	public int getCurrentCost() {
		return this.currentCost;
	}

	public int getAmountPer() {
		return this.amountPer;
	}

	public static void init() {
		if (GTConfig.generateUUMAssemblerRecipes) {
			addUUMAssemblerValue(1, GTMaterialGen.get(Blocks.COBBLESTONE, 16));
			addUUMAssemblerValue(1, GTMaterialGen.get(Blocks.STONE, 16));
			addUUMAssemblerValue(4, GTMaterialGen.get(Blocks.GLASS, 32));
			addUUMAssemblerValue(2, GTMaterialGen.get(Blocks.GRASS, 32));
			addUUMAssemblerValue(1, GTMaterialGen.get(Blocks.LOG, 8, 0));
			addUUMAssemblerValue(1, GTMaterialGen.get(Blocks.LOG, 8, 1));
			addUUMAssemblerValue(1, GTMaterialGen.get(Blocks.LOG, 8, 2));
			addUUMAssemblerValue(1, GTMaterialGen.get(Blocks.LOG, 8, 3));
			addUUMAssemblerValue(1, GTMaterialGen.get(Blocks.LOG2, 8, 0));
			addUUMAssemblerValue(1, GTMaterialGen.get(Blocks.LOG2, 8, 1));
			addUUMAssemblerValue(1, GTMaterialGen.get(Blocks.DIRT, 16));
			addUUMAssemblerValue(5, GTMaterialGen.get(Blocks.SPONGE, 6));
			addUUMAssemblerValue(4, GTMaterialGen.get(Blocks.END_STONE, 16));
			addUUMAssemblerValue(3, GTMaterialGen.get(Blocks.MOSSY_COBBLESTONE, 16));
			addUUMAssemblerValue(2, GTMaterialGen.get(Blocks.SANDSTONE, 16));
			addUUMAssemblerValue(2, GTMaterialGen.get(Blocks.RED_SANDSTONE, 16));
			addUUMAssemblerValue(2, GTMaterialGen.get(Blocks.SNOW, 4));
			addUUMAssemblerValue(2, GTMaterialGen.get(Blocks.WATER, 1));
			addUUMAssemblerValue(3, GTMaterialGen.get(Blocks.LAVA, 1));
			addUUMAssemblerValue(5, GTMaterialGen.get(Blocks.IRON_ORE, 5));
			addUUMAssemblerValue(5, GTMaterialGen.get(Blocks.GOLD_ORE, 2));
			addUUMAssemblerValue(4, GTMaterialGen.get(Blocks.OBSIDIAN, 12));
			addUUMAssemblerValue(3, GTMaterialGen.get(Blocks.NETHERRACK, 16));
			addUUMAssemblerValue(5, GTMaterialGen.get(Items.NETHER_WART, 32));
			addUUMAssemblerValue(6, GTMaterialGen.get(Blocks.GLOWSTONE, 8));
			addUUMAssemblerValue(6, GTMaterialGen.get(Blocks.CACTUS, 48));
			addUUMAssemblerValue(3, GTMaterialGen.get(Blocks.VINE, 24));
			addUUMAssemblerValue(5, GTMaterialGen.get(Items.CHORUS_FRUIT, 4));
			addUUMAssemblerValue(3, GTMaterialGen.get(Blocks.WOOL, 12));
			addUUMAssemblerValue(3, GTMaterialGen.get(Items.COAL, 20));
			addUUMAssemblerValue(9, GTMaterialGen.get(Items.DIAMOND));
			addUUMAssemblerValue(4, GTMaterialGen.get(Items.REDSTONE, 24));
			addUUMAssemblerValue(6, GTMaterialGen.get(Items.QUARTZ, 32));
			addUUMAssemblerValue(4, GTMaterialGen.get(Items.DYE, 9, 4));
			addUUMAssemblerValue(4, GTMaterialGen.get(Items.FEATHER, 32));
			addUUMAssemblerValue(5, GTMaterialGen.get(Items.LEATHER, 32));
			addUUMAssemblerValue(3, GTMaterialGen.get(Items.SNOWBALL, 16));
			addUUMAssemblerValue(7, GTMaterialGen.get(Items.GUNPOWDER, 15));
			addUUMAssemblerValue(5, GTMaterialGen.get(Items.ENDER_PEARL, 2));
			addUUMAssemblerValue(5, GTMaterialGen.get(Items.CLAY_BALL, 48));
			addUUMAssemblerValue(5, GTMaterialGen.get(Items.BLAZE_ROD, 6));
			addUUMAssemblerValue(5, GTMaterialGen.get(Items.PRISMARINE_CRYSTALS, 16));
			addUUMAssemblerValue(5, GTMaterialGen.get(Items.PRISMARINE_SHARD, 32));
			addUUMAssemblerValue(5, GTMaterialGen.get(Items.DYE, 32, 3));
			addUUMAssemblerValue(5, GTMaterialGen.get(Items.DYE, 48, 0));
			addUUMAssemblerValue(6, GTMaterialGen.get(Items.REEDS, 48));
			addUUMAssemblerValue(5, GTMaterialGen.get(Items.FLINT, 32));
			addUUMAssemblerValue(4, GTMaterialGen.get(Items.BONE, 32));
			addUUMAssemblerValue(4, GTMaterialGen.getIc2(Ic2Items.stickyResin, 21));
			addUUMAssemblerValue(7, GTMaterialGen.getIc2(Ic2Items.iridiumOre));
			addUUMAssemblerValue(7, GTMaterialGen.getDust(GTMaterial.Iridium, 1));
			addUUMAssemblerValue(5, GTMaterialGen.get(Blocks.MYCELIUM, 24));
			addUUMAssemblerValue(5, GTMaterialGen.get(Blocks.STONEBRICK, 48, 3));
			addUUMAssemblerValue(3, GTMaterialGen.getIc2(Ic2Items.copperOre, 5));
			addUUMAssemblerValue(3, GTMaterialGen.getIc2(Ic2Items.tinOre, 5));
			addUUMAssemblerValue(5, GTMaterialGen.getIc2(Ic2Items.silverOre, 2));
			addUUMAssemblerValue(7, GTMaterialGen.get(Items.EMERALD));
			addUUMAssemblerValue(7, GTMaterialGen.getGem(GTMaterial.Ruby, 1));
			addUUMAssemblerValue(7, GTMaterialGen.getGem(GTMaterial.Sapphire, 1));
			addUUMAssemblerValue(6, GTMaterialGen.get(GTBlocks.oreBauxite, 6));
			addUUMAssemblerValue(5, GTMaterialGen.getDust(GTMaterial.Titanium, 2));
			addUUMAssemblerValue(5, GTMaterialGen.getDust(GTMaterial.Aluminium, 16));
			addUUMAssemblerValue(7, GTMaterialGen.getDust(GTMaterial.Platinum, 1));
			addUUMAssemblerValue(7, GTMaterialGen.getDust(GTMaterial.Tungsten, 7));
		}
	}

	public static void addUUMAssemblerValue(int uuRequired, ItemStack output) {
		addUUMAssemblerValue(new IRecipeInput[] {
				new RecipeInputItemStack(GTMaterialGen.getIc2(Ic2Items.uuMatter, uuRequired)) }, output);
	}

	private static void addUUMAssemblerValue(IRecipeInput[] inputs, ItemStack... outputs) {
		List<IRecipeInput> inlist = new ArrayList<>();
		List<ItemStack> outlist = new ArrayList<>();
		for (IRecipeInput input : inputs) {
			inlist.add(input);
		}
		for (ItemStack output : outputs) {
			outlist.add(output);
		}
		addUUMAssemblerValue(inlist, new MachineOutput(null, outlist));
	}

	private static void addUUMAssemblerValue(List<IRecipeInput> input, MachineOutput output) {
		RECIPE_LIST.addRecipe(input, output, output.getAllOutputs().get(0).getUnlocalizedName(), 1);
	}

	public static void removeRecipe(String id) {
		RECIPE_LIST.removeRecipe(id);
	}

	@Override
	public List<ItemStack> getDrops() {
		List<ItemStack> list = new ArrayList<>();
		list.add(this.getStackInSlot(12));
		list.add(this.getStackInSlot(13));
		return list;
	}
}