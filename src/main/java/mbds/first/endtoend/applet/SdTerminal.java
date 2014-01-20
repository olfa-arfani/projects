package mbds.first.endtoend.applet;
import java.io.IOException;

import org.nfctools.api.TagListener;
import org.nfctools.scio.AbstractTerminal;
import org.nfctools.scio.TerminalMode;
import org.nfctools.spi.acs.AbstractTerminalTagScanner;
import org.nfctools.spi.acs.InitiatorTerminalTagScanner;
import org.nfctools.spi.acs.TargetTerminalTagScanner;


public class SdTerminal extends AbstractTerminal {
    private Thread scanningThread;
    private AbstractTerminalTagScanner tagScanner;
	public void registerTagListener(TagListener tagListener) {
		// TODO Auto-generated method stub
		 tagScanner.setTagListener(tagListener);
	}

	public void setMode(TerminalMode terminalMode) {
		// TODO Auto-generated method stub
		 if(TerminalMode.INITIATOR.equals(terminalMode))
	            tagScanner = new InitiatorTerminalTagScanner(cardTerminal);
	        else
	            tagScanner = new TargetTerminalTagScanner(cardTerminal);
	        scanningThread = new Thread(tagScanner);
	        scanningThread.setDaemon(true);
	}

	public void startListening() {
		// TODO Auto-generated method stub
		 scanningThread.start();
	}

	public void stopListening() {
		// TODO Auto-generated method stub
		scanningThread.interrupt();
	}

	public boolean canHandle(String terminalName) {
		// TODO Auto-generated method stub
		System.out.println("Terminal "+terminalName.contains("SCM"));
		return terminalName.contains("SCM");
	}

	public void initInitiatorDep() throws IOException {
		// TODO Auto-generated method stub
		try {
			throw new Exception("initInitiatorDep: Not implemented yet ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initTargetDep() throws IOException {
		// TODO Auto-generated method stub
		try {
			throw new Exception("initTargetDep: Not implemented yet ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
