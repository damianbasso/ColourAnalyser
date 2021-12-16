import java.util.ArrayList;
import java.util.List;
import java.awt.Color;


public class Centroid {
    
    private float hue;
    private Color color;
    private List<ColorWeight> cluster;
    private int weight = 1;

    public Centroid (List<ColorWeight> cluster) {
        this.cluster = cluster;
        color = ColorWeight.averageWeights(cluster);
        hue = Color.RGBtoHSB(color.getRed(), color.getGreen(),color.getBlue(), null)[0];
    }
    
    /**
     * Calculates the total distance between every colour in the data set and the mean.
     * @return - the sum distance between all the nodes and the mean
     */
    public double sumDistanceFromMean() {
        // return cluster.stream().mapToDouble(c -> Math.abs(hue - c.getHue())).sum();
        return cluster.stream().mapToDouble(c -> (hue - c.getHue()) * (hue - c.getHue())).sum();
    
    }

    /**
     * 
     * @return - true if the centroid changes, false otherwise
     */
    public boolean recalculateCentroid(List<ColorWeight> newCluster) {
        ColorWeight newColor = ColorWeight.averageWeights(cluster);
        if (cluster.equals(newCluster)) {
            return false;
        }
        cluster = newCluster;
        hue = Color.RGBtoHSB(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), null)[0];
        color = newColor;
        weight = newCluster.stream().mapToInt(c -> c.getWeight()).sum();
        return true;
    }
    
    public float distFromColor(Color c1) {
        float i = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null)[0];
        return Math.abs(i-hue);
    }

    public ColorWeight getColorWeight() {
        return ColorWeight.averageWeights(cluster);
    }


}
