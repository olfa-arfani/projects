
 package mbds.first.endtoend.applet;
 import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;
import javax.swing.border.*;

import netscape.javascript.JSObject;

   public class TestApplet extends JApplet
      implements ActionListener {
  private JPanel pane = null;
    private JScrollPane scrolling = null;
         private JTextPane fileBox = null;
     private JTextField tfFilename = null;
     private JButton butLoad = null;
     private final String LOAD = "load";
	private JSObject jso;

   public void init() {
   try {
	   jso = JSObject.getWindow(this);
      jbInit();
      invokeCallback("call",new String[]{"Called from applet"});
     } catch(Exception e) {
      e.printStackTrace();
     }
   }

   // method which will read data from file, and return it in
      // String
   public String readFile(String fn) {
     String thisLine, ret = "";
     try {
       FileInputStream fin =  new FileInputStream(fn);
       BufferedReader myInput = new BufferedReader
                         (new InputStreamReader(fin));
       while ((thisLine = myInput.readLine()) != null) {
         ret += thisLine + "\n";
       }
     } catch (Exception e) {
      ret = "Cannot load, exception! " + e.getMessage();
     }
     return ret;
   }

   private void jbInit() throws Exception {
     pane = new JPanel();
     pane.setBounds(new Rectangle(0, 0, 500, 325));
     pane.setLayout(null);
     pane.setBorder(BorderFactory.createEtchedBorder(
                       EtchedBorder.LOWERED));
     pane.setBackground(new Color(221, 194, 219));

     fileBox = new JTextPane();
     fileBox.setText("");
     fileBox.setEditable(false);
     scrolling = new JScrollPane(fileBox);
     scrolling.setBounds(new Rectangle(16, 65, 295, 225));

    tfFilename = new JTextField();
     tfFilename.setText("");
     tfFilename.setBounds(new Rectangle(16, 23, 206, 29));

     butLoad = new JButton();
     butLoad.setBounds(new Rectangle(231, 23, 80, 30));
     butLoad.setText("Load");
     butLoad.setActionCommand(LOAD);
     butLoad.addActionListener(this);
     pane.add(scrolling);
    pane.add(tfFilename);
     pane.add(butLoad);

     setContentPane(pane);
   }
   
   private String showError(String error){
	        System.out.println("Writing ..... " + error);

			 FileWriter fstream;
			 String ret ; 
			try {
				fstream = new FileWriter("G:/test.log");
				BufferedWriter out = new BufferedWriter(fstream);
				  out.append("ERROR: " + error + "\n");
				  //Close the output stream
				  out.close();
				  ret = error;
				 
			} catch (IOException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
				ret = e.getMessage();
			}
			return ret;
	 
	}
   
   private void invokeCallback(String callback, String[] message){
		try{
			jso.call(callback, message);
		}catch(Exception e){
		} 
	}
   
   public void Read(){
	   fileBox.setText(readFile(tfFilename.getText())); 
	   invokeCallback("call",new String[]{readFile(tfFilename.getText())});
   }


   public void actionPerformed(ActionEvent e) {
     if (e.getActionCommand().equals(LOAD)) {
         fileBox.setText(readFile(tfFilename.getText()));
         invokeCallback("call",new String[]{readFile(tfFilename.getText())});
     }
   }
 }