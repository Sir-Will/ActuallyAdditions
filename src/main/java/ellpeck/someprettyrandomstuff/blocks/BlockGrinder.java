package ellpeck.someprettyrandomstuff.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ellpeck.someprettyrandomstuff.SomePrettyRandomStuff;
import ellpeck.someprettyrandomstuff.creative.CreativeTab;
import ellpeck.someprettyrandomstuff.inventory.GuiHandler;
import ellpeck.someprettyrandomstuff.tile.TileEntityGrinder;
import ellpeck.someprettyrandomstuff.util.IName;
import ellpeck.someprettyrandomstuff.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockGrinder extends BlockContainerBase implements IName{

    private IIcon topIcon;
    private IIcon onIcon;
    private IIcon bottomIcon;

    private boolean isDouble;

    public BlockGrinder(boolean isDouble){
        super(Material.wood);
        this.isDouble = isDouble;
        this.setBlockName(Util.setUnlocalizedName(this));
        this.setCreativeTab(CreativeTab.instance);
        this.setHarvestLevel("pickaxe", 0);
        this.setHardness(1.0F);
        this.setStepSound(soundTypeStone);
        this.setTickRandomly(true);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2){
        return new TileEntityGrinder(this.isDouble);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z){
        return world.getBlockMetadata(x, y, z) == 1 ? 12 : 0;
    }

    @Override
    public IIcon getIcon(int side, int meta){
        if(side == 1 && meta != 1) return this.topIcon;
        if(side == 1) return this.onIcon;
        if(side == 0) return this.bottomIcon;
        return this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand){
        int meta = world.getBlockMetadata(x, y, z);

        if (meta == 1){
            for(int i = 0; i < 5; i++){
                double xRand = new Random().nextDouble()/0.75D - 0.5D;
                double zRand = new Random().nextDouble()/0.75D - 0.5D;
                world.spawnParticle("crit", (double)x+0.4F, (double)y + 0.8F, (double)z+0.4F, xRand, 0.5D, zRand);
            }
            world.spawnParticle("smoke", (double)x+0.5F, (double)y + 1.0F, (double)z+0.5F, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconReg){
        this.blockIcon = iconReg.registerIcon(Util.MOD_ID_LOWER + ":" + this.getName());
        this.topIcon = iconReg.registerIcon(Util.MOD_ID_LOWER + ":blockGrinderTop");
        this.onIcon = iconReg.registerIcon(Util.MOD_ID_LOWER + ":blockGrinderOn");
        this.bottomIcon = iconReg.registerIcon(Util.MOD_ID_LOWER + ":blockGrinderBottom");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9){
        if(!world.isRemote){
            TileEntityGrinder grinder = (TileEntityGrinder)world.getTileEntity(x, y, z);
            if (grinder != null) player.openGui(SomePrettyRandomStuff.instance, this.isDouble ? GuiHandler.GRINDER_DOUBLE_ID : GuiHandler.GRINDER_ID, world, x, y, z);
            return true;
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int par6){
        this.dropInventory(world, x, y, z);
        super.breakBlock(world, x, y, z, block, par6);
    }

    @Override
    public String getName(){
        return isDouble ? "blockGrinderDouble" : "blockGrinder";
    }

    public static class TheItemBlock extends ItemBlock{

        private Block theBlock;

        public TheItemBlock(Block block){
            super(block);
            this.theBlock = block;
            this.setHasSubtypes(false);
            this.setMaxDamage(0);
        }

        @Override
        public EnumRarity getRarity(ItemStack stack){
            return ((BlockGrinder)theBlock).isDouble ? EnumRarity.epic : EnumRarity.rare;
        }

        @Override
        public String getUnlocalizedName(ItemStack stack){
            return this.getUnlocalizedName();
        }

        @Override
        @SuppressWarnings("unchecked")
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean isHeld) {
            if(Util.isShiftPressed()){
                for(int i = 0; i < (((BlockGrinder)theBlock).isDouble ? 3 : 4); i++){
                    list.add(StatCollector.translateToLocal("tooltip." + Util.MOD_ID_LOWER + "." + ((IName)theBlock).getName() + ".desc." + (i+1)));
                }
            }
            else list.add(Util.shiftForInfo());
        }

        @Override
        public int getMetadata(int damage){
            return damage;
        }
    }
}
