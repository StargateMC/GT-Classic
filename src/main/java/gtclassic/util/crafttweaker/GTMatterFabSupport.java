package gtclassic.util.crafttweaker;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import gtclassic.material.GTMaterialGen;
import gtclassic.recipe.GTRecipeUUAmplifier;
import ic2.api.recipe.IRecipeInput;
import ic2.core.platform.registry.Ic2Items;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Locale;

@ZenClass("mods.gtclassic.MatterFab")
@ZenRegister
public class GTMatterFabSupport {
    @ZenMethod
    public static void addRecipe(IIngredient input, int amplifier) {
        GTCraftTweakerActions.apply(new MatterFabRecipeAction(GTCraftTweakerActions.of(input), amplifier));
    }

    private static final class MatterFabRecipeAction implements IAction {

        private final IRecipeInput input;
        private final int amplifier;

        MatterFabRecipeAction(IRecipeInput input, int amplifier) {
            this.input = input;
            this.amplifier = amplifier;
        }

        @Override
        public void apply() {
            GTRecipeUUAmplifier.addAmplifierToJei(new IRecipeInput[]{input}, GTRecipeUUAmplifier.value(amplifier), GTMaterialGen.getIc2(Ic2Items.uuMatter, 1));
        }

        @Override
        public String describe() {
            return String.format(Locale.ENGLISH, "Add Recipe[%s, %s] to %s", this.input, this.amplifier, GTRecipeUUAmplifier.RECIPE_LIST);
        }
    }
}
