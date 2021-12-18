package ColourAnalyser;

import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.geom.AffineTransform;

public class HorizontalRectangle extends DominantRectangle {

    public HorizontalRectangle(List<ColorWeight> colors, BufferedImage pic) {
        super(colors, pic);
    }

    public int getWidth() {
        return pic.getWidth(null) + 16;
    }

    public int getHeight() {
        return pic.getHeight(null) * 2;
    }
    
    protected void paintComponent(Graphics g) {
        int xpos = 0;
        List<ColorWeight> colors = new ArrayList<>(colorsToSize.keySet());
        colors.sort(Comparator.comparing(ColorWeight:: getWeight).reversed());
        g.setFont(new Font("TimesRoman", Font.PLAIN, 30)); 
        g.drawImage(pic, 0, 0, null);

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform defaultAt = g2d.getTransform();
        AffineTransform rotated = AffineTransform.getQuadrantRotateInstance(-1);

        for (ColorWeight cw: colors) {
            // g.drawRect(10, 10, 600, 510);
            System.out.println(colorsToSize.get(cw));
            g.setColor(cw);
            g.fillRect(xpos, pic.getHeight(null), Math.round(colorsToSize.get(cw) * pic.getWidth(null)), pic.getHeight(null));
            g.setColor(Color.WHITE);

            // Write text
            if (colorsToSize.get(cw) > 0.04) {
                String text = "RGB (" + cw.getRed() + ", " + cw.getGreen() + ", " + cw.getBlue() + ")      " + Math.round(colorsToSize.get(cw) * 10000) / 100.0 + "%";
                int textWidth = (int)Math.round(g.getFontMetrics().getStringBounds(text, g).getWidth());
                int textHeight = (int)Math.round(g.getFontMetrics().getStringBounds(text, g).getHeight());
                   
                // rotates the coordinate by 90 degree counterclockwise
                g2d.setTransform(rotated);
                g.drawString(text, (int) Math.round(-1.5*pic.getHeight(null)) - textWidth/2 , xpos + Math.round(colorsToSize.get(cw) * pic.getWidth(null))/2 + textHeight/2);
                g2d.setTransform(defaultAt);

            }
            xpos += Math.round(colorsToSize.get(cw) * pic.getWidth(null));
        }
    }

}
