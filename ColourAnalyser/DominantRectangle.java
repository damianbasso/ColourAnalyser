package ColourAnalyser;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.geom.AffineTransform;


public abstract class DominantRectangle extends JPanel {

    // private List<ColorWeight> colors;
    protected HashMap<ColorWeight, Float> colorsToSize = new HashMap<>();
    protected Image pic;

    public DominantRectangle(List<ColorWeight> colors, BufferedImage pic) {
        int totalWeight = colors.stream().mapToInt(e -> e.getWeight()).sum();
        for (ColorWeight cw : colors) {
            colorsToSize.put(cw, (float)cw.getWeight()/totalWeight);
        }
        if (pic.getWidth() > pic.getHeight()) {
            this.pic=pic.getScaledInstance(800, pic.getHeight() * 800/pic.getWidth(), 0);
        }
        else {
            this.pic=pic.getScaledInstance(pic.getWidth() * 800/pic.getHeight(), 800, 0);    
        }
    }

    public static DominantRectangle getDominantRectangle(List<ColorWeight> colors, BufferedImage pic) {
        if (pic.getWidth() > pic.getHeight()) {
            return new HorizontalRectangle(colors, pic);
        }
        return new VerticalRectangle(colors, pic);
    }

    public abstract int getWidth();

    public abstract int getHeight();

    protected abstract void paintComponent(Graphics g);
}
