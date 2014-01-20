package mbds.first.endtoend.applet;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.List;

import javax.crypto.NoSuchPaddingException;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import netscape.javascript.JSObject;
import org.nfctools.NfcAdapter;
import org.nfctools.api.NfcTagListener;
import org.nfctools.api.Tag;
import org.nfctools.mf.ul.Type2NfcTagListener;
import org.nfctools.ndef.NdefListener;
import org.nfctools.ndef.NdefOperations;
import org.nfctools.ndef.NdefOperationsListener;
import org.nfctools.ndef.Record;
import org.nfctools.ndef.wkt.records.TextRecord;
import org.nfctools.scio.Terminal;
import org.nfctools.scio.TerminalMode;
import org.nfctools.snep.Sneplet;
import org.nfctools.utils.LoggingUnknownTagListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirstApplet extends JApplet implements NdefListener, Sneplet,
		NfcTagListener, NdefOperationsListener {

	private static final long serialVersionUID = 1L;
	private JLabel lbl;
	private NfcAdapter nfcAdapter;
	private JSObject jso;
	private String successCallback = "successCallback";
	private String errorCallback = "errorCallback";
	private Logger log = LoggerFactory.getLogger(getClass());

	// Called when this applet is loaded into the browser.
	public void init() {

		jso = JSObject.getWindow(this);
		// Execute a job on the event-dispatching thread; creating this applet's
		// GUI.
		lbl = new JLabel("Applet loaded and ready for command........");
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {

					startListenning();

				}
			});
		} catch (Exception e) {
			lbl.setText("createGUI didn't complete successfully "
					+ e.getMessage());
			e.printStackTrace();
		}
		add(lbl);
	}

	public Collection<Record> doGet(Collection<Record> requestRecords) {
		log.info("SNEP get");
		onNdefMessages(requestRecords);
		return null;
	}

	public void doPut(Collection<Record> requestRecords) {
		log.info("SNEP put");
		onNdefMessages(requestRecords);
	}

	// private String showError(String error) {
	// System.out.println("Writing ..... " + error);
	// lbl.setText("Error said ! " + error);
	// FileWriter fstream;
	// String ret;
	// try {
	// fstream = new FileWriter("G:\\test.log");
	// BufferedWriter out = new BufferedWriter(fstream);
	// out.append("ERROR: " + error + "\n");
	// // Close the output stream
	// out.close();
	// ret = error;
	//
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// lbl.setText("Writing ..... " + error);
	// e.printStackTrace();
	// ret = e.getMessage();
	// }
	// return ret;
	// }

	private void invokeCallback(String callback, String[] message) {
		try {
			jso.call(callback, message);
		} catch (Exception e) {
			System.out.println("Exception occured" + e.toString());
		}
	}

	public void stopListenning() {
		if (nfcAdapter != null) {
			nfcAdapter.stopListening();
		}
	}

	private boolean InitializeNfc() {

		try {
			Terminal terminal = TerminalUtils.getAvailableTerminal();
			if (terminal != null) {
				nfcAdapter = new NfcAdapter(
						TerminalUtils.getAvailableTerminal(),
						TerminalMode.INITIATOR);
				nfcAdapter.registerTagListener(this);
				nfcAdapter
						.registerUnknownTagListerner(new LoggingUnknownTagListener());
				nfcAdapter.registerTagListener(new Type2NfcTagListener(this));
				// nfcAdapter.registerTagListener(new CustomMifareTag(this));
				return true;
			}
		} catch (Exception e) {

		}
		return false;
	}

	public void startListenning() {
		if (InitializeNfc()) {
			nfcAdapter.startListening();
			lbl.setText("Please put the tag on the terminal!");
		} else {
			lbl.setText("No terminals available");
			invokeCallback("errorCallback", new String[] { "404" });
		}
	}

	public void setValue(String string) {

		this.lbl.setText(string);
	}

	public boolean canHandle(Tag tag) {
		// TODO Auto-generated method stub
		return false;
	}

	public void handleTag(Tag tag) {
		// TODO Auto-generated method stub

	}

	public void onNdefOperations(NdefOperations ndefOperations) {

		EncryptDecryptDES tripleDES = new EncryptDecryptDES();
		System.out.println("Formated: " + ndefOperations.isFormatted()
				+ " Writable: " + ndefOperations.isWritable());
		String result = "";
		TextRecord textToWrite = new TextRecord();
		String lblText = "";
		if (ndefOperations.isWritable()) {

			result = generateText();
			try {				

				textToWrite = new TextRecord(tripleDES.encrypt(result));

			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (ndefOperations.isFormatted())
				ndefOperations.writeNdefMessage(textToWrite);
			else
				ndefOperations.format(textToWrite);

			lblText = textToWrite.getText();
			if (lblText != null) {
				lbl.setText(lblText);
				invokeCallback(successCallback, new String[] { lblText });
				nfcAdapter.stopListening();
			} else {
				invokeCallback(errorCallback, new String[] { "No tag found" });
			}
			System.out.println("Done");
		} else {
			System.out.println("NOT SUPPORTED FORMAT");
			return;
		}

	}

	public String generateText() {
		String chars = "0123456789";
		String name = "FIRST-";
		for (int x = 0; x < 3; x++) {
			int i = (int) Math.floor(Math.random() * 10);
			name += chars.charAt(i);
		}
		System.out.println("HERE!!!" + name);
		return name;
	}

	public void onNdefMessages(Collection<Record> records) {
		// TODO Auto-generated method stub

	}

}
