package it.unibo.oop.lab.reactivegui03;


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * This is a first example on how to realize a reactive GUI.
 */
public final class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton bUp = new JButton("up");
    private final JButton bDown = new JButton("down");
    private final JButton bStop = new JButton("stop");
    private final AgentUp agent;
    /**
     * Builds a new CGUI.
     */
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel(new FlowLayout());
        panel.add(this.display);
        panel.add(this.bUp);
        panel.add(this.bDown);
        panel.add(this.bStop);   
        this.getContentPane().add(panel);
        this.setVisible(true);
        /*
         * Create the counter agent and start it. This is actually not so good:
         * thread management should be left to
         * java.util.concurrent.ExecutorService
         */
        this.agent = new AgentUp();
        final AgentDisable agentDisable = new AgentDisable();
        new Thread(agentDisable).start();
        new Thread(agent).start();
        
        /*
         * Register a listener that stops it
         */
        bStop.addActionListener(e -> {
                agent.stopCounting();
            });
        
        bUp.addActionListener(e -> {
                agent.restartCountingIfStopped();
        });
        
        bDown.addActionListener(e -> {
                agent.invertCounting();
            });
    }
    
    private class AgentUp implements Runnable {
        private volatile boolean stop;
        private int counter;
        private volatile int delta = 1;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(Integer.toString(AgentUp.this.counter)));
                    this.counter += delta;             
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        public void stopCounting() {
            this.stop = true;
        }
        
        public void restartCountingIfStopped() {
            this.stop = false;
            this.delta = 1;
        }
        
        public void invertCounting() {
            this.stop = false;
            this.delta = -1;
        }
    }
    
    private class AgentDisable implements Runnable {
        private static final int TEN_SECONDS_IN_MS = 10_000;
        @Override
        public void run() {
                try {    
                    Thread.sleep(TEN_SECONDS_IN_MS);
                    SwingUtilities.invokeAndWait(() -> {
                        AnotherConcurrentGUI.this.bDown.setEnabled(false);
                        AnotherConcurrentGUI.this.bStop.setEnabled(false);
                        AnotherConcurrentGUI.this.bUp.setEnabled(false);
                        AnotherConcurrentGUI.this.agent.stopCounting();
                    });       
                    
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
        }
        
    }
}
