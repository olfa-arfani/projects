package mbds.first.endtoend.applet;

import java.io.IOException;

import javax.smartcardio.CardChannel;

import org.nfctools.io.NfcDevice;
import org.nfctools.mf.MfAccess;
import org.nfctools.spi.acs.AcsReaderWriter;

public class ScmReaderWriter extends AcsReaderWriter {

	public static final String TERMINAL_NAME = "SCM Microsystems Inc. SDI011G Contactless Reader 0";

	public ScmReaderWriter(NfcDevice nfcDevice) {
		super(nfcDevice);
		if (!cardTerminal.getName().contains(TERMINAL_NAME))
			throw new IllegalArgumentException("card terminal not supported");
	}

	@Override
	protected void loginIntoSector(MfAccess mfAccess, CardChannel cardChannel) throws IOException {
		super.loginIntoSector(mfAccess, cardChannel, (byte)0x00);
	}

}
