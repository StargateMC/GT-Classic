package gtclassic.blocks;

import gtclassic.GTClassic;
import ic2.core.block.base.BlockMultiID;
import ic2.core.block.base.tile.TileEntityBlock;
import ic2.core.platform.textures.Ic2Icons;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GTBlockEUGenerator extends BlockMultiID {
    public GTBlockEUGenerator(String reg, String un){
        super(Material.IRON);
        this.setHardness(4.0F);
        this.setResistance(20.0F);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(GTClassic.creativeTabGT);
        this.setRegistryName(reg);
        this.setUnlocalizedName(GTClassic.MODID + "." + un);
    }

    @Override
    public List<Integer> getValidMetas() {
        return Arrays.asList(0);
    }

    @Override
    public TileEntityBlock createNewTileEntity(World worldIn, int meta){
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite[] getIconSheet(int meta)
    {
        return Ic2Icons.getTextures("gtclassic_blocks");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getTextureFromState(IBlockState state, EnumFacing side) {
        if (side == EnumFacing.DOWN) {
            return Ic2Icons.getTextures("gtclassic_blocks")[40];
        } else if (side == EnumFacing.UP){
            return Ic2Icons.getTextures("gtclassic_blocks")[41];
        }else {
            return Ic2Icons.getTextures("gtclassic_blocks")[25];
        }
        //return Ic2Icons.getTextures("gtclassic_blocks")[100];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getParticleTexture(IBlockState state) {
        return Ic2Icons.getTextures("gtclassic_blocks")[100];
    }

    @Override
    public List<IBlockState> getValidStateList()
    {
        IBlockState def = getDefaultState();
        List<IBlockState> states = new ArrayList<IBlockState>();
        for(EnumFacing side : EnumFacing.VALUES)
        {
            states.add(def.withProperty(getMetadataProperty(), 0).withProperty(allFacings, side).withProperty(active, false));
            states.add(def.withProperty(getMetadataProperty(), 0).withProperty(allFacings, side).withProperty(active, true));
        }
        return states;
    }

    @Override
    public List<IBlockState> getValidStates()
    {
        return getBlockState().getValidStates();
    }
}
