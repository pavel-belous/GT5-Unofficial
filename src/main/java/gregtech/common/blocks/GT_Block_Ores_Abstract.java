package gregtech.common.blocks;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.ITexture;
import gregtech.api.items.GT_Generic_Block;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import gregtech.common.render.GT_Renderer_Block;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class GT_Block_Ores_Abstract extends GT_Generic_Block implements ITileEntityProvider {
    public static ThreadLocal<GT_TileEntity_Ores> mTemporaryTileEntity = new ThreadLocal();
    public static boolean FUCKING_LOCK = false;
    public static boolean tHideOres;
    private final String aTextName = ".name";
    private final String aTextSmall = "Small ";
    public static Set aBlockedOres = new HashSet<Materials>();

    protected GT_Block_Ores_Abstract(String aUnlocalizedName, int aOreMetaCount, boolean aHideFirstMeta, Material aMaterial) {
        super(GT_Item_Ores.class, aUnlocalizedName, aMaterial);
        this.isBlockContainer = true;
        setStepSound(soundTypeStone);
        setCreativeTab(GregTech_API.TAB_GREGTECH_ORES);
        tHideOres = Loader.isModLoaded("NotEnoughItems") && GT_Mod.gregtechproxy.mHideUnusedOres;
        if(aOreMetaCount > 8 || aOreMetaCount < 0) aOreMetaCount = 8;

        for (int i = 0; i < 16; i++) {
            GT_ModHandler.addValuableOre(this, i, 1);
        }
        for (int i = 1; i < GregTech_API.sGeneratedMaterials.length; i++) {
            if (GregTech_API.sGeneratedMaterials[i] != null) {
                for (int j = 0; j < aOreMetaCount; j++) {
                    if (!this.getEnabledMetas()[j]) continue;
                    GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (i + (j * 1000)) + aTextName, GT_LanguageManager.i18nPlaceholder ? getLocalizedNameFormat(GregTech_API.sGeneratedMaterials[i]) : getLocalizedName(GregTech_API.sGeneratedMaterials[i]));
                    GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + ((i + 16000) + (j * 1000)) + aTextName, aTextSmall + (GT_LanguageManager.i18nPlaceholder ? getLocalizedNameFormat(GregTech_API.sGeneratedMaterials[i]) : getLocalizedName(GregTech_API.sGeneratedMaterials[i])));
                    if ((GregTech_API.sGeneratedMaterials[i].mTypes & 0x8) != 0 && !aBlockedOres.contains(GregTech_API.sGeneratedMaterials[i])) {
                        GT_OreDictUnificator.registerOre(this.getProcessingPrefix()[j] != null ? this.getProcessingPrefix()[j].get(GregTech_API.sGeneratedMaterials[i]) : "", new ItemStack(this, 1, i + (j * 1000)));
                        if (tHideOres) {
                            if (!(j == 0 && !aHideFirstMeta)) {
                                codechicken.nei.api.API.hideItem(new ItemStack(this, 1, i + (j * 1000)));
                            }
                            codechicken.nei.api.API.hideItem(new ItemStack(this, 1, (i + 16000) + (j * 1000)));
                        }
                    }
                }
            }
        }
        for (int i = 1; i < GregTech_API.sGeneratedExtendedMaterials.length; i++) {
            if (GregTech_API.sGeneratedExtendedMaterials[i] != null) {
                for (int j = 0; j < aOreMetaCount; j++) {
                    if (!this.getEnabledMetas()[j]) continue;
                    GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (i + (j * 1000 + 8000)) + aTextName, GT_LanguageManager.i18nPlaceholder ? getLocalizedNameFormat(GregTech_API.sGeneratedExtendedMaterials[i]) : getLocalizedName(GregTech_API.sGeneratedExtendedMaterials[i]));
                    GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + ((i + 24000) + (j * 1000 + 8000)) + aTextName, aTextSmall + (GT_LanguageManager.i18nPlaceholder ? getLocalizedNameFormat(GregTech_API.sGeneratedExtendedMaterials[i]) : getLocalizedName(GregTech_API.sGeneratedExtendedMaterials[i])));
                    if ((GregTech_API.sGeneratedExtendedMaterials[i].mTypes & 0x8) != 0 && !aBlockedOres.contains(GregTech_API.sGeneratedExtendedMaterials[i])) {
                        GT_OreDictUnificator.registerOre(this.getProcessingPrefix()[j] != null ? this.getProcessingPrefix()[j].get(GregTech_API.sGeneratedExtendedMaterials[i]) : "", new ItemStack(this, 1, i + (j * 1000 + 8000)));
                        if (tHideOres) {
                            if (!(j == 0 && !aHideFirstMeta)) {
                                codechicken.nei.api.API.hideItem(new ItemStack(this, 1, i + (j * 1000 + 8000)));
                            }
                            codechicken.nei.api.API.hideItem(new ItemStack(this, 1, (i + 24000) + (j * 1000 + 8000)));
                        }
                    }
                }
            }
        }
    }

    public int getBaseBlockHarvestLevel(int aMeta) {
        return 0;
    }

    @Override
    public void onNeighborChange(IBlockAccess aWorld, int aX, int aY, int aZ, int aTileX, int aTileY, int aTileZ) {
        if (!FUCKING_LOCK) {
            FUCKING_LOCK = true;
            TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
            if ((tTileEntity instanceof GT_TileEntity_Ores)) {
                ((GT_TileEntity_Ores) tTileEntity).onUpdated();
            }
        }
        FUCKING_LOCK = false;
    }

    @Override
    public void onNeighborBlockChange(World aWorld, int aX, int aY, int aZ, Block aBlock) {
        if (!FUCKING_LOCK) {
            FUCKING_LOCK = true;
            TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
            if ((tTileEntity instanceof GT_TileEntity_Ores)) {
                ((GT_TileEntity_Ores) tTileEntity).onUpdated();
            }
        }
        FUCKING_LOCK = false;
    }

    public String getLocalizedNameFormat(Materials aMaterial) {
    	switch (aMaterial.mName) {
        case "InfusedAir":
        case "InfusedDull":
        case "InfusedEarth":
        case "InfusedEntropy":
        case "InfusedFire":
        case "InfusedOrder":
        case "InfusedVis":
        case "InfusedWater":
            return "%material Infused Stone";
        case "Vermiculite":
        case "Bentonite":
        case "Kaolinite":
        case "Talc":
        case "BasalticMineralSand":
        case "GraniticMineralSand":
        case "GlauconiteSand":
        case "CassiteriteSand":
        case "GarnetSand":
        case "QuartzSand":
        case "Pitchblende":
        case "FullersEarth":
            return "%material";
        default:
            return "%material" + OrePrefixes.ore.mLocalizedMaterialPost;
    	}
    }

    public String getLocalizedName(Materials aMaterial) {
        return aMaterial.getDefaultLocalizedNameForItem(getLocalizedNameFormat(aMaterial));
    }

    @Override
    public boolean onBlockActivated(World aWorld, int aX, int aY, int aZ, EntityPlayer aPlayer, int aSide, float par1, float par2, float par3) {
        if (!aPlayer.isSneaking() || !aPlayer.capabilities.isCreativeMode) {
            return false;
        }

        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (!(tTileEntity instanceof GT_TileEntity_Ores)) {
            return false;
        }

        boolean tNatural = (((GT_TileEntity_Ores) tTileEntity).mNatural = !((GT_TileEntity_Ores) tTileEntity).mNatural);
        GT_Utility.sendChatToPlayer(aPlayer, "Ore \"mNatural\" flag set to: " + tNatural);
        return true;
    }

    @Override
    public boolean onBlockEventReceived(World p_149696_1_, int p_149696_2_, int p_149696_3_, int p_149696_4_, int p_149696_5_, int p_149696_6_) {
        super.onBlockEventReceived(p_149696_1_, p_149696_2_, p_149696_3_, p_149696_4_, p_149696_5_, p_149696_6_);
        TileEntity tileentity = p_149696_1_.getTileEntity(p_149696_2_, p_149696_3_, p_149696_4_);
        return tileentity != null ? tileentity.receiveClientEvent(p_149696_5_, p_149696_6_) : false;
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return (!(entity instanceof EntityDragon)) && (super.canEntityDestroy(world, x, y, z, entity));
    }

    @Override
    public String getHarvestTool(int aMeta) {
        return aMeta < 8 ? "pickaxe" : "shovel";
    }

    @Override
    public int getHarvestLevel(int aMeta) {
        return aMeta == 5 || aMeta == 6 ? 2 : aMeta % 8;
    }

    @Override
    public float getBlockHardness(World aWorld, int aX, int aY, int aZ) {
        return 1.0F + getHarvestLevel(aWorld.getBlockMetadata(aX, aY, aZ)) * 1.0F;
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World aWorld, int aX, int aY, int aZ, double explosionX, double explosionY, double explosionZ) {
        return 1.0F + getHarvestLevel(aWorld.getBlockMetadata(aX, aY, aZ)) * 1.0F;
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    public abstract String getUnlocalizedName();

    @Override
    public String getLocalizedName() {
        return StatCollector.translateToLocal(getUnlocalizedName() + aTextName);
    }

    @Override
    public int getRenderType() {
        if (GT_Renderer_Block.INSTANCE == null) {
            return super.getRenderType();
        }
        return GT_Renderer_Block.INSTANCE.mRenderID;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return true;
    }

    @Override
    public boolean hasTileEntity(int aMeta) {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World aWorld, int aMeta) {
        return createTileEntity(aWorld, aMeta);
    }

    @Override
    public IIcon getIcon(IBlockAccess aIBlockAccess, int aX, int aY, int aZ, int aSide) {
        return Blocks.stone.getIcon(0, 0);
    }

    @Override
    public IIcon getIcon(int aSide, int aMeta) {
        return Blocks.stone.getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister aIconRegister) {
    }

    @Override
    public int getDamageValue(World aWorld, int aX, int aY, int aZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (((tTileEntity instanceof GT_TileEntity_Ores))) {
            return ((GT_TileEntity_Ores) tTileEntity).getMetaData();
        }
        return 0;
    }

    @Override
    public void breakBlock(World aWorld, int aX, int aY, int aZ, Block par5, int par6) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof GT_TileEntity_Ores)) {
            mTemporaryTileEntity.set((GT_TileEntity_Ores) tTileEntity);
        }
        super.breakBlock(aWorld, aX, aY, aZ, par5, par6);
        aWorld.removeTileEntity(aX, aY, aZ);
    }

    public abstract OrePrefixes[] getProcessingPrefix(); //Must have 8 entries; an entry can be null to disable automatic recipes.

    public abstract boolean[] getEnabledMetas(); //Must have 8 entries.

    public abstract Block getDroppedBlock();

    public abstract Materials[] getDroppedDusts(); //Must have 8 entries; can be null.

    @Override
    public ArrayList<ItemStack> getDrops(World aWorld, int aX, int aY, int aZ, int aMeta, int aFortune) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof GT_TileEntity_Ores)) {
            return ((GT_TileEntity_Ores) tTileEntity).getDrops(getDroppedBlock(), aFortune);
        }
        return mTemporaryTileEntity.get() == null ? new ArrayList() : ((GT_TileEntity_Ores) mTemporaryTileEntity.get()).getDrops(getDroppedBlock(), aFortune);
    }

    @Override
    public TileEntity createTileEntity(World aWorld, int aMeta) {
        return new GT_TileEntity_Ores();
    }

    public abstract ITexture[] getTextureSet(); //Must have 16 entries.

    @Override
    public void getSubBlocks(Item aItem, CreativeTabs aTab, List aList) {
        for (int i = 0; i < GregTech_API.sGeneratedMaterials.length; i++) {
            Materials tMaterial = GregTech_API.sGeneratedMaterials[i];
            if ((tMaterial != null) && ((tMaterial.mTypes & 0x8) != 0)&& !aBlockedOres.contains(tMaterial)) {
                if (!(new ItemStack(aItem, 1, i).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i));
                if (!(new ItemStack(aItem, 1, i + 1000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 1000));
                if (!(new ItemStack(aItem, 1, i + 2000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 2000));
                if (!(new ItemStack(aItem, 1, i + 3000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 3000));
                if (!(new ItemStack(aItem, 1, i + 4000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 4000));
                if (!(new ItemStack(aItem, 1, i + 5000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 5000));
                if (!(new ItemStack(aItem, 1, i + 6000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 6000));
                if (!(new ItemStack(aItem, 1, i + 7000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 7000));
                if (!(new ItemStack(aItem, 1, i + 16000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 16000));
                if (!(new ItemStack(aItem, 1, i + 17000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 17000));
                if (!(new ItemStack(aItem, 1, i + 18000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 18000));
                if (!(new ItemStack(aItem, 1, i + 19000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 19000));
                if (!(new ItemStack(aItem, 1, i + 20000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 20000));
                if (!(new ItemStack(aItem, 1, i + 21000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 21000));
                if (!(new ItemStack(aItem, 1, i + 22000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 22000));
                if (!(new ItemStack(aItem, 1, i + 23000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 23000));
            }
        }
        for (int i = 0; i < GregTech_API.sGeneratedExtendedMaterials.length; i++) {
            Materials tMaterial = GregTech_API.sGeneratedExtendedMaterials[i];
            if ((tMaterial != null) && ((tMaterial.mTypes & 0x8) != 0)&& !aBlockedOres.contains(tMaterial)) {
                if (!(new ItemStack(aItem, 1, i).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i));
                if (!(new ItemStack(aItem, 1, i + 8000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 8000));
                if (!(new ItemStack(aItem, 1, i + 9000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 9000));
                if (!(new ItemStack(aItem, 1, i + 10000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 10000));
                if (!(new ItemStack(aItem, 1, i + 11000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 11000));
                if (!(new ItemStack(aItem, 1, i + 12000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 12000));
                if (!(new ItemStack(aItem, 1, i + 13000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 13000));
                if (!(new ItemStack(aItem, 1, i + 14000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 14000));
                if (!(new ItemStack(aItem, 1, i + 24000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 24000));
                if (!(new ItemStack(aItem, 1, i + 25000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 25000));
                if (!(new ItemStack(aItem, 1, i + 26000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 26000));
                if (!(new ItemStack(aItem, 1, i + 27000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 27000));
                if (!(new ItemStack(aItem, 1, i + 28000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 28000));
                if (!(new ItemStack(aItem, 1, i + 29000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 29000));
                if (!(new ItemStack(aItem, 1, i + 30000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 30000));
                if (!(new ItemStack(aItem, 1, i + 31000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 31000));
            }
        }
    }
}
