package mbds.first.endtoend.applet;

import java.io.IOException;

import org.nfctools.NfcException;
import org.nfctools.api.ApduTag;
import org.nfctools.api.NfcTagListener;
import org.nfctools.api.Tag;
import org.nfctools.api.TagType;
import org.nfctools.mf.MfConstants;
import org.nfctools.mf.block.MfBlock;
import org.nfctools.mf.classic.ClassicHandler;

import org.nfctools.mf.classic.MfClassicNdefOperations;
import org.nfctools.mf.classic.MfClassicReaderWriter;
import org.nfctools.mf.mad.Application;
import org.nfctools.mf.mad.ApplicationDirectory;
import org.nfctools.mf.ul.CapabilityBlock;
import org.nfctools.mf.ul.LockPage;
import org.nfctools.mf.ul.MemoryLayout;
import org.nfctools.mf.ul.MfUlReaderWriter;
import org.nfctools.mf.ul.Type2NdefOperations;
import org.nfctools.mf.ul.UltralightHandler;
import org.nfctools.ndef.NdefOperationsListener;
import org.nfctools.spi.acs.AcrMfClassicReaderWriter;
import org.nfctools.spi.acs.AcrMfUlReaderWriter;

public class CustomMifareTag implements NfcTagListener {
	private NdefOperationsListener ndefListener;

	public CustomMifareTag() {
	}

	public CustomMifareTag(NdefOperationsListener ndefListener) {
		this.ndefListener = ndefListener;
	}

	public boolean canHandle(Tag tag) {
		return tag.getTagType().equals(TagType.MIFARE_CLASSIC_1K)
				|| tag.getTagType().equals(TagType.MIFARE_CLASSIC_4K);
	}

	public void handleTag(Tag tag) {
		Type2NdefOperations ndefOperations = createNdefOperations((ApduTag)tag);
		if (ndefListener != null)
			ndefListener.onNdefOperations(ndefOperations);
	}

	protected Type2NdefOperations createNdefOperations(ApduTag tag) {
		MfUlReaderWriter readerWriter = new AcrMfUlReaderWriter(tag);
		MemoryLayout memoryLayout = null;
		boolean formatted = false;
		boolean writable = false;
		try {
			MfBlock[] initBlocks = readerWriter.readBlock(0, 5);
			CapabilityBlock capabilityBlock = new CapabilityBlock(initBlocks[3].getData());
			if (UltralightHandler.isBlank(initBlocks)) {
				if (UltralightHandler.isUltralight(initBlocks[4].getData())) {
					memoryLayout = MemoryLayout.ULTRALIGHT;
				}
				else if (UltralightHandler.isUltralightC(initBlocks[4].getData())) {
					memoryLayout = MemoryLayout.ULTRALIGHT_C;
				}
				else {
					throw new NfcException("Unknown tag size");
				}
				writable = true;
			}
			else if (UltralightHandler.isFormatted(initBlocks)) {
				formatted = true;
				if (capabilityBlock.getSize() == 0x06)
					memoryLayout = MemoryLayout.ULTRALIGHT;
				else if (capabilityBlock.getSize() == 0x12) {
					memoryLayout = MemoryLayout.ULTRALIGHT_C;
				}
				else {
					throw new NfcException("Unknown memory size " + capabilityBlock.getSize());
				}
				writable = !capabilityBlock.isReadOnly() && !isLocked(readerWriter, memoryLayout);
			}
			else {
				throw new NfcException("Unknown tag contents");
			}
		}
		catch (Exception e) {
			throw new NfcException(e);
		}
		return new Type2NdefOperations(memoryLayout, readerWriter, formatted, writable);
	}


	private boolean isLocked(MfUlReaderWriter readerWriter,
			MemoryLayout memoryLayout) throws IOException {
		for (LockPage lockPage : memoryLayout.getLockPages()) {
			MfBlock[] block = readerWriter.readBlock(lockPage.getPage(), 1);
			for (int lockByte : lockPage.getLockBytes()) {
				if (block[0].getData()[lockByte] != 0)
					return true;
			}
		}
		return false;
	}
}
