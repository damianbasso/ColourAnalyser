import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

// import java.awt.*;
import java.awt.Color;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.image.BufferedImage;
import java.awt.FlowLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class ColourScanner {

    private List<ColorWeight> colorsByWeight;
    private ColorWeight averageColour;

    public ColourScanner(File file) throws IOException{
        colorsByWeight = parseImage(file);
        averageColour = ColorWeight.averageWeights(colorsByWeight);
    }

    /**
     * parses the given image, loading in the colour objects as a list as well as calculating
     * the overall average colour of the image
     */
    private List<ColorWeight> parseImage(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);

        // Image scaled = image.getScaledInstance(image.getWidth()/8, image.getHeight()/8, Image.SCALE_SMOOTH);
        // // ImageIO.write(scaled, "JPG", new File("C:\\Users\\damia\\OneDrive\\Documents\\Code\\yoooo.jpg"));

        // int w = image.getWidth();
        // int h = image.getHeight();
        // BufferedImage scaledImage = new BufferedImage((w * 2),(h * 2), BufferedImage.TYPE_INT_ARGB);
        // AffineTransform at = AffineTransform.getScaleInstance(2.0, 2.0);
        // AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        // scaledImage = ato.filter(image, scaledImage);

        List<ColorWeight> colorsByWeight = new ArrayList<>();
        for (int x = 0; x <image.getWidth(); x++) {
            for (int y = 0; y< image.getHeight(); y++) {
                // int clr = image.getRGB(x, y);
                Color colour = new Color(image.getRGB(x, y));
                // colorsByWeight.add(new ColorWeight(colour));
                int index = colorsByWeight.indexOf(colour);
                if (index == -1) {
                    // adds a new colour with weight 1
                    colorsByWeight.add(new ColorWeight(colour));
                }
                else {
                    // if the colour has already been recorded, its weight is adjusted
                    colorsByWeight.get(index).increaseWeight(1);
                }
            }
        }
        return colorsByWeight;
    }
        


    /**
     * Initialises the means and clusters to be used in the kMeansCluster algorithm.
     * Delegates by sorting the colours by hue, and then evenly partitioning it in blocks into clusters.
     * @param colorsByWeight - A list of all the colours in the image to be parsed
     * @param k - the number of clusters to be formed
     * @return - The initialised centroids (means) and their associated clusters
     */
    private HashMap<Centroid, List<ColorWeight>> sortAndDelegateMeans(int k) {
        System.out.println("sorting");
        colorsByWeight.sort(new ByHSB());
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        System.out.println("Initialising");
        // for (i = 0; i<colorsByWeight.size(); i += (colorsByWeight.size()/numOfDom)) {
        for (int i = 0; i<k; i += 1) {
            meansToClusters.put(new Centroid( Arrays.asList(colorsByWeight.get(i * colorsByWeight.size()/ k))), new ArrayList<>());
        }
        return meansToClusters;
    }

    /**
     * Initialises the means and clusters to be used in the kMeansCluster algorithm.
     * Delegates by sorting the colours by hue, and then evenly partitioning it in blocks into clusters.
     * @param colorsByWeight - A list of all the colours in the image to be parsed
     * @param k - the number of clusters to be formed
     * @return - The initialised centroids (means) and their associated clusters
     */
    private HashMap<Centroid, List<ColorWeight>> randAverageMeans(int k) {
        
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        HashMap<Integer, List<ColorWeight>> clusters = new HashMap<>();
        for (int a = 0; a<k; k++) {
            clusters.put(a, new ArrayList<>());
        }
        for (ColorWeight cw : colorsByWeight) {
            Random rand = new Random();
            
            clusters.get(rand.nextInt(k)).add(cw);

        }
        for (List<ColorWeight> colors : clusters.values()) {
            meansToClusters.put(new Centroid(colors), colors);
        }
        return meansToClusters;
    }

    private HashMap<Centroid, List<ColorWeight>> forgySelectMeans(int k) {
        
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        Random rand = new Random();
        for (int i =0; i<k; i++) {
            meansToClusters.put(new Centroid(Arrays.asList(colorsByWeight.get(rand.nextInt(colorsByWeight.size())))), new ArrayList<>());
        }
        return meansToClusters;
    }

    // private static HashMap<ColorWeight, List<ColorWeight>> forgyMeansToClusters(List<ColorWeight> colorsByWeight, int k) {

    private List<Centroid> kMeansCluster(int k) {
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        // HashMap<Centroid, List<ColorWeight>> rearrangedClusters = new HashMap<>();
        meansToClusters = this.sortAndDelegateMeans(k);
        // meansToClusters = randMeansToClusters(colorsByWeight, k);
        // meansToClusters = forgyMeansToClusters(colorsByWeight, k);
        
        // Here, we need initial means to be set
        while (true)
        {
            // Assign clusters
            // System.out.println("Assigning");
            for (ColorWeight curr: colorsByWeight) {
                float dist = 0;
                // System.out.println("c");
                Centroid closest = null;
                // Centroid closest = meansToClusters.keySet().stream().min((f1,f2) -> Math.round(f1.distFromColor(curr)) - Math.round(f2.distFromColor(curr))).get();
                for(Centroid key : meansToClusters.keySet()) {
                    // System.out.println("d");
                    if(closest == null || key.distFromColor(curr) < dist) {
                        // System.out.println("Distance: " + dist);
                        dist = key.distFromColor(curr);
                        // System.out.println("Distance: " + dist);
                        closest = key;
                    }
                }
                meansToClusters.get(closest).add(curr);
            }
            // System.out.println("LEGO " + meansToClusters);
            System.out.println("newMeans");
            // if (meansToClusters.keySet().stream().allMatch(c -> c.recalculateCentroid(meansToClusters.get(c)) == false)) {
            //     System.out.println("BREAK");
            //     break;
            // }
            boolean noChange = true;
            for (Centroid c : meansToClusters.keySet()) {
                if (c.recalculateCentroid(meansToClusters.get(c))) {
                    noChange = false;
                }
            }
            if (noChange) {
                break;
            }
        }
        return new ArrayList<>(meansToClusters.keySet());
    }


    private void graphColour(int k) {
        
        List<Centroid> centroids = this.kMeansCluster(k);
        // System.out.println("GGGGGGGGGGG");
        // centroids.stream().forEach(e-> System.out.println(e.getColorWeight().getHue()));
        List<ColorWeight> means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
        displayColors(means);

    }

    private static void displayColors(List<ColorWeight> means) {
        DominantRectangle rect = new DominantRectangle(means);
        JFrame window = new JFrame();
        window.setSize(840, 560);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(rect);

        window.setVisible(true);
    }

    private void findDominantColors() {
        
        List<Centroid> centroids = kMeansCluster(2);

        List<ColorWeight> means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
        List<ColorWeight> lastMeans = means;
        double dist = centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum(); 
        double lastDistance = 2*dist;
        // for (int k=2; k<12; k++) {
        int k = 3;
        while(lastDistance * 0.85 > dist) {
        // while(lastDistance/ dist > 1.15) {
            lastDistance = dist;
            centroids = kMeansCluster(k);
            lastMeans = means;
            means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
            dist = centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum();
            k++;
        }
        displayColors(lastMeans);
    }

    private void graphValuesOfK() {
        List<List<Centroid>> centroidsToDistance = new ArrayList<>();
        for (int k=1; k<12; k++) {
            List<Centroid> centroids = kMeansCluster(k);
            centroidsToDistance.add(centroids);
        }
        List<Double> vals = new ArrayList<>();
        for (List<Centroid> curr:centroidsToDistance) {
            // System.out.println(curr.stream().mapToDouble(Centroid::sumDistanceFromMean).sum());
            vals.add(curr.stream().mapToDouble(Centroid::sumDistanceFromMean).sum());
        }
        LineChart chart = new LineChart(vals);
        chart.pack();
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );
    }

    
    public static void main(String args[]) throws IOException {
        System.out.println("BRUH");
        File file = new File("C:\\Users\\damia\\OneDrive\\Documents\\Code\\lego.png");
        if (file.exists()) {
            System.out.println("found ya");
        }
        ColourScanner cs = new ColourScanner(file);
        
        // graphColour(colorsByWeight, 5);
        cs.findDominantColors();
        // graphValuesOfK(colorsByWeight);

    }
}
