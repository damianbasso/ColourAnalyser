import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.awt.Color;

public class DominantRectangle extends JPanel {

    private List<ColorWeight> colors;
    private HashMap<ColorWeight, Float> colorsToSize = new HashMap<>();

    public DominantRectangle(List<ColorWeight> colors) {
        int totalWeight = colors.stream().mapToInt(e -> e.getWeight()).sum();
        for (ColorWeight cw : colors) {
            colorsToSize.put(cw, (float)cw.getWeight()/totalWeight);
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int ypos = 10;
        List<ColorWeight> colors = new ArrayList<>(colorsToSize.keySet());
        colors.sort(Comparator.comparing(ColorWeight:: getWeight).reversed());
        for (ColorWeight cw: colors) {
            g.setColor(cw);
            // g.drawRect(10, 10, 600, 510);
            g.fillRect(10, ypos, 800, Math.round(colorsToSize.get(cw) * 500));
            ypos += Math.round(colorsToSize.get(cw) * 500);
        }
        
    }
}
