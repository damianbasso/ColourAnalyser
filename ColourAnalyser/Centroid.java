package ColourAnalyser;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;


public class Centroid {
    
    private float hue;
    private float brightness;
    private Color color;
    private List<ColorWeight> cluster;
    private int weight = 1;

    public Centroid (List<ColorWeight> cluster) {
        this.cluster = cluster;
        color = ColorWeight.averageWeights(cluster);
        hue = Color.RGBtoHSB(color.getRed(), color.getGreen(),color.getBlue(), null)[0];
        brightness = Color.RGBtoHSB(color.getRed(), color.getGreen(),color.getBlue(), null)[1];
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
        brightness = Color.RGBtoHSB(color.getRed(), color.getGreen(),color.getBlue(), null)[1];

        color = newColor;
        weight = newCluster.stream().mapToInt(c -> c.getWeight()).sum();
        return true;
    }

    private float squared(float val) {
        return val*val;
    }
    
    /**
     * Calculates the total distance between every colour in the data set and the mean.
     * @return - the sum distance between all the nodes and the mean
     */
    public double sumDistanceFromMean() {
        // return cluster.stream().mapToDouble(c -> Math.abs(hue - c.getHue())).sum();
        return cluster.stream().mapToDouble(c -> distFromColor(c)).sum();
    
    }


    public float distFromColor(Color c1) {
        return squared(c1.getRed()-color.getRed()) + squared(c1.getGreen()-color.getGreen()) + squared(c1.getBlue()-color.getBlue());
        // float[] hsb = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);

        // float[] myhsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        // return Math.abs(hsb[0]-myhsb[0]) + Math.abs(hsb[1]-myhsb[1])*15 + Math.abs(hsb[2]-myhsb[2]) *15;
    }

    public ColorWeight getColorWeight() {
        return ColorWeight.averageWeights(cluster);
    }


}
