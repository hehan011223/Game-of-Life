package edu.neu.coe.info6205.life.ui;

//import edu.neu.csye6200.fd.FrameFluidSet;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * A Test application for the Wolfram Biological Growth application
 * @author MMUNSON
 */
public class MyApp extends LifeApp {
    

	private static Logger log = Logger.getLogger(MyApp.class.getName());
        
        //private FrameFluidSet ffs = new FrameFluidSet();

        //private JFrame frame = null;
	protected JPanel mainPanel = null;
	protected JPanel northPanel = null;
	protected JButton startBtn = null;
	protected JButton stopBtn = null;
        private JButton generationset = null;
        private JCheckBox ruleBox1 = null;
        private JCheckBox ruleBox2 = null;
        private JButton ruleset = null;
        
        private NextCanvas bgPanel = new NextCanvas(frame);
	
    /**
     * Sample app constructor
     */
    public MyApp() {
        
    	        frame.setSize(820, 880); // initial Frame size
		frame.setTitle("MyApp");
		
		menuMgr.createDefaultActions(); // Set up default menu items
		log.info("MyApp started");
    	showUI(); // Cause the Swing Dispatch thread to display the JFrame
    }
   
    /**
     * Create a main panel that will hold the bulk of our application display
     */
	@Override
	public JPanel getMainPanel() {
	
        mainPanel = new JPanel();
    	mainPanel.setLayout(new BorderLayout());
    	mainPanel.add(BorderLayout.SOUTH, getNorthPanel());
    	
    	bgPanel = new NextCanvas(frame);
    	//mainPanel.add(BorderLayout.CENTER, bgPanel);
    	
    	return mainPanel;
	}
        
    
	/**
	 * Create a top panel that will hold control buttons
	 * @return
	 */
    public JPanel getNorthPanel() {
    	northPanel = new JPanel();
    	northPanel.setLayout(new FlowLayout());
    	
    	startBtn = new JButton("Start");
    	startBtn.addActionListener(this); // Allow the app to hear about button pushes
    	northPanel.add(startBtn);
    	
    	stopBtn = new JButton("Stop"); // Allow the app to hear about button pushes
    	stopBtn.addActionListener(this);
    	northPanel.add(stopBtn);
        
        /*generationset = new JButton("Set generation");
	generationset.addActionListener(this);
        northPanel.add(generationset); 
        
        ruleset = new JButton("Set Rule");
	ruleset.addActionListener(this);
        northPanel.add(ruleset); */
        
        
        /*ruleBox1= new JCheckBox("Ruel1");
        ruleBox1.addActionListener(this);
        northPanel.add(ruleBox1);
        ruleBox2= new JCheckBox("Ruel2");
        ruleBox2.addActionListener(this);
        northPanel.add(ruleBox2);*/
        
        
       /*SpinnerNumberModel model3 = new SpinnerNumberModel(10, 0, 100, 5); 
        JSpinner spinner3 = new JSpinner(model3); 
        northPanel.add(spinner3);
        ffs.setMAX_GENERATION((int) spinner3.getValue());*/

    	return northPanel;
    }
    
	@Override
	public void actionPerformed(ActionEvent ae) {
		log.info("We received an ActionEvent " + ae);
                
                if(ae.getActionCommand().equalsIgnoreCase("Start")){
			System.out.println("Start pressed");
                       bgPanel.start();
                        //bgPanel.drawParticle();

                        //ffs.run();
                        //this.initGUI();
                        
                        
                }
                if (ae.getActionCommand().equalsIgnoreCase("Stop")){
			System.out.println("Stop pressed");
                        bgPanel.stop();
			bgPanel = new NextCanvas(frame);
                }
                
//                if(ae.getActionCommand().equalsIgnoreCase("Set generation")){
//			System.out.println("Set generation.");
//
//			String input = JOptionPane.showInputDialog("Enter new integeral generation:");
//			int ge = Integer.parseInt(input);
//			
//			bgPanel.setge(ge);
//			
//			}
//                if(ae.getActionCommand().equalsIgnoreCase("Set Rule")){
//                    System.out.println("Set Rule.");
//                    Object[] possibleValues = { "Rule 1", "Rule 2"}; 
//                    Object selectedValue = JOptionPane.showInputDialog(null, "Please select the rule", "Select the rule", 
//                    JOptionPane.INFORMATION_MESSAGE, null, 
//                    possibleValues, possibleValues[0]);
//                    if(selectedValue=="Rule 1"){ 
//                    bgPanel.setRule(0);}
//                    else{
//                        bgPanel.setRule(1);
//                    }
//                }
                
	}

	@Override
	public void windowOpened(WindowEvent e) {
		log.info("Window opened");
	}

	@Override
	public void windowClosing(WindowEvent e) {	
		log.info("Window closing");
	}



	@Override
	public void windowClosed(WindowEvent e) {
		log.info("Window closed");
	}



	@Override
	public void windowIconified(WindowEvent e) {
		log.info("Window iconified");
	}



	@Override
	public void windowDeiconified(WindowEvent e) {	
		log.info("Window deiconified");
	}



	@Override
	public void windowActivated(WindowEvent e) {
		log.info("Window activated");
	}



	@Override
	public void windowDeactivated(WindowEvent e) {	
		log.info("Window deactivated");
	}
	
}
