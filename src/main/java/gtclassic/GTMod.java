package gtclassic;

import org.apache.logging.log4j.Logger;

import gtclassic.api.commands.GTCommandTeleport;
import gtclassic.api.helpers.GTHelperMods;
import gtclassic.api.material.GTMaterialGen;
import gtclassic.common.GTBlocks;
import gtclassic.common.GTConfig;
import gtclassic.common.GTCreativeTab;
import gtclassic.common.GTCrops;
import gtclassic.common.GTFluids;
import gtclassic.common.GTItems;
import gtclassic.common.GTJei;
import gtclassic.common.GTOreDict;
import gtclassic.common.GTWorldGen;
import gtclassic.common.event.GTEventCheckSpawn;
import gtclassic.common.event.GTEventEntityViewRenderEvent;
import gtclassic.common.event.GTEventLootTableLoad;
import gtclassic.common.event.GTEventOnLivingFall;
import gtclassic.common.event.GTEventPopulateChunk;
import gtclassic.common.proxy.GTProxyCommon;
import gtclassic.common.recipe.GTRecipe;
import gtclassic.common.recipe.GTRecipeIterators;
import gtclassic.common.recipe.GTRecipeMods;
import gtclassic.common.recipe.GTRecipeProcessing;
import gtclassic.common.tile.GTTileCentrifuge;
import gtclassic.common.tile.GTTileMagicEnergyConverter;
import gtclassic.common.tile.GTTileMatterFabricator;
import gtclassic.common.tile.GTTileUUMAssembler;
import gtclassic.common.tile.multi.GTTileMultiFusionReactor;
import gtclassic.common.util.GTIDSUStorageManager;
import gtclassic.common.worldgen.GTWorldTwilightForest;
import ic2.core.IC2;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = GTMod.MODID, name = GTMod.MODNAME, version = GTMod.MODVERSION, dependencies = GTMod.DEPENDS, useMetadata = true)
public class GTMod {

	public static final String MODID = "gtclassic";
	public static final String MODNAME = "GregTech Classic";
	public static final String MODVERSION = "1.0.7";
	public static final String DEPENDS = "required-after:ic2;required-after:ic2-classic-spmod;after:twilightforest@[3.9.984,)";
	public static final CreativeTabs creativeTabGT = new GTCreativeTab(MODID);
	@SidedProxy(clientSide = MODID + ".common.proxy.GTProxyClient", serverSide = MODID + ".common.proxy.GTProxyServer")
	public static GTProxyCommon proxy;
	@Mod.Instance
	public static GTMod instance;
	public static Logger logger;
	private static boolean quickDebug = false;
	public static boolean debugMode = GTConfig.debugMode || quickDebug;

	@Mod.EventHandler
	public synchronized void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		proxy.preInit(event);
		logger.info("Hello from GregTech Classic!");
		GTFluids.registerFluids();
		GTBlocks.registerTiles();
		GTMaterialGen.initFlags();
		GTMaterialGen.init();
		GTBlocks.registerBlocks();
		GTItems.initItems();
		GTItems.registerItems();
		GTCrops.init();
		GTOreDict.init();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		GTRecipeIterators.init();
		GTRecipeProcessing.init();
		GTTileCentrifuge.init();
		GTTileUUMAssembler.init();
		GTTileMagicEnergyConverter.init();
		GTRecipe.initShapeless();
		GTRecipe.initItems();
		GTRecipe.initBlocks();
		GTRecipe.initIC2();
		GameRegistry.registerWorldGenerator(new GTWorldGen(), 0);
		MinecraftForge.EVENT_BUS.register(new GTEventOnLivingFall());
		MinecraftForge.EVENT_BUS.register(new GTEventLootTableLoad());
		MinecraftForge.EVENT_BUS.register(new GTEventCheckSpawn());
		MinecraftForge.EVENT_BUS.register(new GTEventEntityViewRenderEvent());
		MinecraftForge.EVENT_BUS.register(new GTEventPopulateChunk());
		// MinecraftForge.EVENT_BUS.register(new GTEventPlayerTick());
		IC2.saveManager.registerGlobal("IDSU_Storage", GTIDSUStorageManager.class, false);
		proxy.init(e);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
		GTFluids.postInitProperities();
		GTRecipeIterators.postInit();
		GTTileMatterFabricator.postInit();
		GTTileMultiFusionReactor.postInit();
		GTRecipeMods.postInit();
		GTJei.initEntries();
		if (GTConfig.compatTwilightForest && Loader.isModLoaded(GTHelperMods.TFOREST)) {
			GTWorldTwilightForest.initStalactites();
		}
	}

	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new GTCommandTeleport());
	}

	/**
	 * A logger that will only work if debug mode is enabled
	 * 
	 * @param message
	 */
	public static void debugLogger(String message) {
		if (debugMode) {
			logger.info(message);
		}
	}
}
