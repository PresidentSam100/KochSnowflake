import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class KochSnowFlake extends JFrame {

    private SnowFlake myPanel;
    private JSlider mySlider;

    public static void main(String[] args)
    {
        KochSnowFlake app = new KochSnowFlake();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public KochSnowFlake()
    {
        super("Koch Snowflake");
        this.getContentPane().setLayout(new BorderLayout());
        JPanel sliderPanel = new JPanel(new FlowLayout());
        sliderPanel.add(new JLabel("Recursion Depth"));
        mySlider = new JSlider(0,10,0);
        mySlider.setMajorTickSpacing(1);
        mySlider.setPaintTicks(true);
        sliderPanel.add(mySlider);
        mySlider.addChangeListener(new SliderListener());
        this.getContentPane().add(sliderPanel,BorderLayout.SOUTH);
        myPanel = new SnowFlake();
        myPanel.setPreferredSize(new Dimension(600,600));
        this.getContentPane().add(myPanel,BorderLayout.CENTER);
        setSize(600,700);
        setResizable(false);
        setVisible(true);
    }

    public class SliderListener implements ChangeListener
    {
        public void stateChanged(ChangeEvent ce)
        {
            if (mySlider.getValueIsAdjusting())
                return;
            myPanel.setDepth(mySlider.getValue());
        }
    }
}