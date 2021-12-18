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

public class VerticalRectangle extends DominantRectangle {

    public VerticalRectangle(List<ColorWeight> colors, BufferedImage pic) {
        super(colors, pic);
    }

    public int getWidth() {
        return pic.getWidth(null) * 2;
    }

    public int getHeight() {
        return pic.getHeight(null);
    }
    
    protected void paintComponent(Graphics g) {
        int ypos = 0;
        List<ColorWeight> colors = new ArrayList<>(colorsToSize.keySet());
        colors.sort(Comparator.comparing(ColorWeight:: getWeight).reversed());
        g.setFont(new Font("TimesRoman", Font.PLAIN, 30)); 
        g.drawImage(pic, 0, 0, null);
        for (ColorWeight cw: colors) {
            // g.drawRect(10, 10, 600, 510);
            System.out.println(colorsToSize.get(cw));
            g.setColor(cw);
            g.fillRect(pic.getWidth(null), ypos, pic.getWidth(null), Math.round(colorsToSize.get(cw) * pic.getHeight(null)));
            g.setColor(Color.WHITE);

            // Write text
            if (colorsToSize.get(cw) > 0.04) {
                String text = "RGB (" + cw.getRed() + ", " + cw.getGreen() + ", " + cw.getBlue() + ")      " + Math.round(colorsToSize.get(cw) * 10000) / 100.0 + "%";
                int textWidth = (int)Math.round(g.getFontMetrics().getStringBounds(text, g).getWidth());
                int textHeight = (int)Math.round(g.getFontMetrics().getStringBounds(text, g).getHeight());
                g.drawString(text, pic.getWidth(null)*3/2 - textWidth/2, ypos + Math.round(colorsToSize.get(cw) * pic.getHeight(null)/2 + textHeight * 1/2));
            }
            ypos += Math.round(colorsToSize.get(cw) * pic.getHeight(null));
        }
    }

}
