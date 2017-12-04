/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * Extension of the JPanel class that overrides the paintComponent method
 * @author Sam
 * @version 1.0
 * @see JPanel
 */
public class myJPanel extends JPanel{

    /**
     * The BufferedImage to paint
     */
    public BufferedImage image;
    
    /**
     * Constructor
     * @param isDoubleBuffered If true, the panel is double buffered
     * @param img The BufferedImage to draw in the panel
     * @see BufferedImage
     */
    public myJPanel(boolean isDoubleBuffered, BufferedImage img)
    {
        super(isDoubleBuffered);
        image = img;
    }

    /**
     * Override of the paintComponentMethod in such a way that the stored BufferedImage is also refreshed
     * @param g The graphics environment
     */
    @Override public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.drawImage(image, 0, 0, null);
    }

}
