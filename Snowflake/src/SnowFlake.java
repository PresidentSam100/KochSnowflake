import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;


public class SnowFlake extends JPanel {

    private int myDepth; // this variable indicates how many overall layers
    //    of recursion should be drawn.

    private BufferedImage myCanvas; // an offscreen image where we'll do thd drawing and occasionally copy to screen.
    private Object myCanvasMutex;   // a lock to make sure only one thing uses myCanvas at a time.

    private SnowflakeThread drawingThread; // the portion of code that will do the drawing simultaneously with
    // occasional screen updates.

    // these two matching arrays of doubles are the x and y components
    //  of 6 vectors, pointing in the 6 "cardinal" directions.
    // To make a line that is D units long in the nth direction,
    // use x = D*i[n] and y = D*j[n].
    private double[] i = {1.0,  0.5, 	 -0.5,     -1.0, -0.5,     0.5};
    private double[] j = {0.0, -0.86603, -0.86603,  0.0,  0.86603, 0.86603};
    //         roughly...{E,    NE,       NW,       W,    SW,      SE}

    private double penLocX, penLocY; // where does the next line start?


    public SnowFlake()
    {
        super();
        myDepth = 0;
        setPenLoc(0.0,0.0);
        drawingThread = new SnowflakeThread();
        drawingThread.start();
        myCanvasMutex = new Object();
    }

    public void setDepth(int d)
    {
        if (d>-1)
        {
            myDepth=d;
            System.out.println("Setting depth to "+d+".");
//			repaint();
            drawingThread.interrupt();;
            drawingThread.startDrawing();
        }
    }

    public void setPenLoc(double x, double y)
    {
        penLocX = x;
        penLocY = y;
    }

    public void paintComponent(Graphics g)
    {
        //super.paintComponent(g);
        synchronized (myCanvasMutex)
        {
            if (myCanvas != null)
                g.drawImage(myCanvas, 0, 0, this);
        }
    }


    class SnowflakeThread extends Thread
    {
        private boolean needsRestart;
        private boolean shouldInterrupt;

        public SnowflakeThread()
        {
            needsRestart = true;
            shouldInterrupt = false;
        }

        public void interrupt()
        {
            shouldInterrupt = true;
        }

        public void startDrawing()
        {
            needsRestart = true;
        }

        public void run() // this is what gets called when we tell this thread to start(). You should NEVER call run() directly.
        {

            while (true)
            {
                if (needsRestart && getHeight() > 5 && getWidth() > 5)
                {
                    // reset with a new image....
                    synchronized (myCanvasMutex)
                    {
                        myCanvas = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                        Graphics g = myCanvas.getGraphics();
                        g.setColor(Color.BLACK);
                        g.fillRect(0,0,getWidth(),getHeight());
                    }

                    setPenLoc(100, 400);
                    drawRecursiveLine(0, 400.0, myDepth);
                    drawRecursiveLine(2, 400.0, myDepth);
                    drawRecursiveLine(4, 400.0, myDepth);
                    if (!shouldInterrupt)
                        needsRestart = false; // we're done! (and we didn't just get interrupted.)
                }
                try
                {
                    Thread.sleep(250); // wait a quarter second before you consider running again.
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                shouldInterrupt = false;

            }
        }

        /**
         * This is the method you have to finish!
         *
         * @param direction      which of the 6 "cardinal" directions to go
         * @param length         how long a line should you draw?
         * @param recursionsToGo how many more times do you need to subdivide the line?
         */
        public void drawRecursiveLine(int direction,
                                      double length,
                                      int recursionsToGo)
        {
            if (shouldInterrupt)  // bail out if we need to cancel and leave.
                return;

            // TODO: This is just the base case. You need to finish this method.
            if (recursionsToGo == 0) {
                //------------------------------------------------- BASE CASE START
                double nextXLoc = penLocX + length * i[direction];
                double nextYLoc = penLocY + length * j[direction];
                synchronized (myCanvasMutex) // wait to get access to myCanvas to draw in it, and lock it.
                {
                    Graphics mCanv_g = myCanvas.getGraphics();
                    mCanv_g.setColor(Color.RED);
                    mCanv_g.drawLine((int) penLocX, (int) penLocY, (int) nextXLoc, (int) nextYLoc);
                } // done with myCanvas for now.

                setPenLoc(nextXLoc, nextYLoc);
                repaint();
                return;
                //------------------------------------------------- BASE CASE END
            }
            drawRecursiveLine(direction,length/3,recursionsToGo-1);
            drawRecursiveLine((direction+5)%6,length/3,recursionsToGo-1);
            drawRecursiveLine((direction+7)%6,length/3,recursionsToGo-1);
            drawRecursiveLine(direction,length/3,recursionsToGo-1);
        }
    }

}