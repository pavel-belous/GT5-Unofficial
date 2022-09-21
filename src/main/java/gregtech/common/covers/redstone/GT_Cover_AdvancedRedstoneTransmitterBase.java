package gregtech.common.covers.redstone;

import com.google.common.io.ByteArrayDataInput;
import gregtech.api.GregTech_API;
import gregtech.api.gui.widgets.GT_GuiIcon;
import gregtech.api.gui.widgets.GT_GuiIconCheckButton;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.util.GT_Utility;
import gregtech.api.util.ISerializableObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class GT_Cover_AdvancedRedstoneTransmitterBase<T extends GT_Cover_AdvancedRedstoneTransmitterBase.TransmitterData> extends GT_Cover_AdvancedWirelessRedstoneBase<T> {

    public GT_Cover_AdvancedRedstoneTransmitterBase(Class<T> typeToken, ITexture coverTexture) {
        super(typeToken, coverTexture);
    }

    private static void unregisterSignal(byte aSide, TransmitterData aCoverVariable, ICoverable aTileEntity) {
        long hash = GregTech_API.hashCoverCoords(aTileEntity, aSide);
        removeSignalAt(aCoverVariable.uuid, aCoverVariable.frequency, hash);
    }

    @Override
    public boolean onCoverRemovalImpl(byte aSide, int aCoverID, TransmitterData aCoverVariable, ICoverable aTileEntity,
                                      boolean aForced) {
        unregisterSignal(aSide, aCoverVariable, aTileEntity);
        return true;
    }

    @Override
    protected void onBaseTEDestroyedImpl(byte aSide, int aCoverID, TransmitterData aCoverVariable, ICoverable aTileEntity) {
        unregisterSignal(aSide, aCoverVariable, aTileEntity);
    }

    public static class TransmitterData extends GT_Cover_AdvancedWirelessRedstoneBase.WirelessData {
        protected boolean invert;

        public TransmitterData(int frequency, UUID uuid, boolean invert) {
            super(frequency, uuid);
            this.invert = invert;
        }

        public TransmitterData() {
            this(0, null, false);
        }

        public boolean isInvert() {
            return invert;
        }

        @Nonnull
        @Override
        public ISerializableObject copy() {
            return new TransmitterData(frequency, uuid, invert);
        }

        @Nonnull
        @Override
        public NBTBase saveDataToNBT() {
            NBTTagCompound tag = (NBTTagCompound) super.saveDataToNBT();
            tag.setBoolean("invert", invert);

            return tag;
        }

        @Override
        public void writeToByteBuf(ByteBuf aBuf) {
            super.writeToByteBuf(aBuf);
            aBuf.writeBoolean(invert);
        }

        @Override
        public void loadDataFromNBT(NBTBase aNBT) {
            super.loadDataFromNBT(aNBT);

            NBTTagCompound tag = (NBTTagCompound) aNBT;
            invert = tag.getBoolean("invert");
        }

        @Nonnull
        @Override
        public ISerializableObject readFromPacket(ByteArrayDataInput aBuf, EntityPlayerMP aPlayer) {
            super.readFromPacket(aBuf, aPlayer);
            invert = aBuf.readBoolean();

            return this;
        }
    }

    /**
     * GUI Stuff
     */

    @Override
    public Object getClientGUIImpl(byte aSide, int aCoverID, TransmitterData aCoverVariable, ICoverable aTileEntity,
                                   EntityPlayer aPlayer, World aWorld) {
        return new TransmitterGUI<>(aSide, aCoverID, aCoverVariable, aTileEntity);
    }

    protected class TransmitterGUI<X extends TransmitterData> extends WirelessGUI<X> {

        private final GT_GuiIconCheckButton invertButton;

        private final String INVERTED = GT_Utility.trans("INVERTED", "Inverted");
        private final String NORMAL = GT_Utility.trans("NORMAL", "Normal");

        public TransmitterGUI(byte aSide, int aCoverID, X aCoverVariable, ICoverable aTileEntity) {
            super(aSide, aCoverID, aCoverVariable, aTileEntity);
            invertButton = new GT_GuiIconCheckButton(this, 1, startX + spaceX * 8, startY + spaceY * 1, GT_GuiIcon.REDSTONE_ON, GT_GuiIcon.REDSTONE_OFF, INVERTED, NORMAL);
        }

        @Override
        public void drawExtras(int mouseX, int mouseY, float parTicks) {
            super.drawExtras(mouseX, mouseY, parTicks);
            this.getFontRenderer().drawString(
                GT_Utility.trans("246", "Frequency"),
                startX + spaceX * 5,
                4 + startY,
                textColor);
            this.getFontRenderer().drawString(
                GT_Utility.trans("601", "Use Private Frequency"),
                startX + spaceX,
                startY + spaceY * 1 + 4,
                textColor);
            this.getFontRenderer().drawString(
                coverVariable.invert ? INVERTED : NORMAL,
                startX + spaceX * 9,
                startY + spaceY * 1 + 4,
                textColor);
        }

        @Override
        protected void update() {
            super.update();
            invertButton.setChecked(coverVariable.invert);
        }

        @Override
        public void buttonClicked(GuiButton btn) {
            if (btn == invertButton) {
                coverVariable.invert = !coverVariable.invert;
            }

            super.buttonClicked(btn);
        }
    }
}
