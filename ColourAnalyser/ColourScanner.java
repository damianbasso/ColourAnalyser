package ColourAnalyser;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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

// import org.jfree.chart.ChartPanel;
// import org.jfree.chart.ChartFactory;
// import org.jfree.chart.JFreeChart;
// import org.jfree.ui.ApplicationFrame;
// import org.jfree.ui.RefineryUtilities;
// import org.jfree.chart.plot.PlotOrientation;
// import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.BorderLayout;

public class ColourScanner {

    private List<ColorWeight> colorsByWeight;
    private ColorWeight averageColour;
    private BufferedImage image;

    public ColourScanner(File file) throws IOException{
        colorsByWeight = parseImage(file);
        System.out.println("parsing done");
        averageColour = ColorWeight.averageWeights(colorsByWeight);
        System.out.println("averaging done");
    }

    private List<ColorWeight> parseImage(File file) throws IOException {
        image = ImageIO.read(file);

        // Image scaled = image.getScaledInstance(image.getWidth()/8, image.getHeight()/8, Image.SCALE_SMOOTH);
        // ImageIO.write(scaled, "JPG", new File("C:\\Users\\damia\\OneDrive\\Documents\\Code\\yoooo.jpg"));

        // int w = image.getWidth();
        // int h = image.getHeight();

        // BufferedImage scaledImage = new BufferedImage((Math.round(w * 800/h)),Math.round(h *800/h), BufferedImage.TYPE_INT_ARGB);
        // AffineTransform at = AffineTransform.getScaleInstance(Math.round(800/h), Math.round(800/h));
        // AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        // image = ato.filter(image, scaledImage);

        HashMap<Color,Integer> colors = new HashMap<>();
        for (int x = 0; x <image.getWidth(); x++) {
            for (int y = 0; y< image.getHeight(); y++) {
                int clr = image.getRGB(x, y);
                // int red =   (clr & 0x00ff0000) >> 16;
                // int green = (clr & 0x0000ff00) >> 8;
                // int blue =   clr & 0x000000ff;
                // Color color = new Color(red,green,blue);
                Color color = new Color(clr);
                if (colors.containsKey(color)) {
                    colors.put(color, colors.get(color) + 1);
                }
                else {
                    colors.put(color, 1);    
                }
            }
        }
        List<ColorWeight> colorsByWeight = new ArrayList<>();
        for (Map.Entry<Color, Integer> entry : colors.entrySet()) {
            colorsByWeight.add(new ColorWeight(entry.getKey(), entry.getValue()));
        }
        colorsByWeight.sort(new ByHSB());
        return colorsByWeight;
    }


    
    private static void parseImageHue(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        int hueScore[] = new int[360]; 

        for (int x = 0; x <image.getWidth(); x++) {
            for (int y = 0; y< image.getHeight(); y++) {
                int clr = image.getRGB(x, y);
                // int red =   (clr & 0x00ff0000) >> 16;
                // int green = (clr & 0x0000ff00) >> 8;
                // int blue =   clr & 0x000000ff;
                // Color color = new Color(red,green,blue);
                Color color = new Color(clr);
                float val = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null)[0];
                hueScore[Math.round(val * 360)]++;
            }
        }
        for (int p : hueScore) {
            System.out.println(p);
        }
    }


    /**
     * parses the given image, loading in the colour objects as a list as well as calculating
     * the overall average colour of the image
     */
    private static List<ColorWeight> parseImage1(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        // Image scaled = image.getScaledInstance(image.getWidth()/8, image.getHeight()/8, Image.SCALE_SMOOTH);
        // // ImageIO.write(scaled, "JPG", new File("C:\\Users\\damia\\OneDrive\\Documents\\Code\\yoooo.jpg"));

        // int w = image.getWidth();
        // int h = image.getHeight();
        // BufferedImage scaledImage = new BufferedImage((w * 2),(h * 2), BufferedImage.TYPE_INT_ARGB);
        // AffineTransform at = AffineTransform.getScaleInstance(2.0, 2.0);
        // AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        // scaledImage = ato.filter(image, scaledImage);

        HashMap<Color,Integer> colors = new HashMap<>();
        for (int x = 0; x <image.getWidth(); x++) {
            for (int y = 0; y< image.getHeight(); y++) {
                int clr = image.getRGB(x, y);
                // int red =   (clr & 0x00ff0000) >> 16;
                // int green = (clr & 0x0000ff00) >> 8;
                // int blue =   clr & 0x000000ff;
                // Color color = new Color(red,green,blue);
                Color color = new Color(clr);
                if (colors.containsKey(color)) {
                    colors.put(color, colors.get(color) + 1);
                }
                else {
                    colors.put(color, 1);    
                }
            }
        }
        List<ColorWeight> colorsByWeight = new ArrayList<>();
        for (Map.Entry<Color, Integer> entry : colors.entrySet()) {
            colorsByWeight.add(new ColorWeight(entry.getKey(), entry.getValue()));
        }
        colorsByWeight.sort(new ByHSB());
        return colorsByWeight;
    }

    private static List<ColorWeight> parseImage2(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);

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
        colorsByWeight.sort(new ByHSB());
        return colorsByWeight;
    }


    private static List<ColorWeight> parseImage3(File file) throws IOException {
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
                if (colorsByWeight.contains(colour)) {
                    // if the colour has already been recorded, its weight is adjusted
                    colorsByWeight.get(colorsByWeight.indexOf(colour)).increaseWeight(1);
                }
                else {
                    // adds a new colour with weight 1
                    colorsByWeight.add(new ColorWeight(colour));
                }
            }
        }
        colorsByWeight.sort(new ByHSB());
        return colorsByWeight;
    }
        


    /**
     * Initialises the means and clusters to be used in the kMeansCluster algorithm.
     * Delegates by sorting the colours by hue, and then evenly partitioning it in blocks into clusters.
     * @param colorsByWeight - A list of all the colours in the image to be parsed
     * @param k - the number of clusters to be formed
     * @return - The initialised centroids (means) and their associated clusters
     */
    private HashMap<Centroid, List<ColorWeight>> delegateMeans(int k) {
        // System.out.println("sorting");
        // colorsByWeight.sort(new ByHSB());
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        System.out.println("Initialising");
        // for (i = 0; i<colorsByWeight.size(); i += (colorsByWeight.size()/numOfDom)) {
        // for (int i = 0; i<k; i += 1) {
        //     meansToClusters.put(new Centroid( Arrays.asList(colorsByWeight.get(i * colorsByWeight.size()/ k))), new ArrayList<>());
        // }
        for (int i = 0; i<k; i += 1) {
            List<ColorWeight> currCol = new ArrayList<>();
            for (int p = i*colorsByWeight.size()/k; p<(i+1)*colorsByWeight.size()/k; p += 1) {
                currCol.add(colorsByWeight.get(p));
            }
            meansToClusters.put(new Centroid(currCol), currCol);
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
    private HashMap<Centroid, List<ColorWeight>> partitionAveMeans(int k) {
        // System.out.println("sorting");
        // colorsByWeight.sort(new ByHSB());
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        System.out.println("Initialising");
        for (int i = 0; i<k; i += 1) {
            List<ColorWeight> currCol = new ArrayList<>();
            for (int p = i*colorsByWeight.size()/k; p<(i+1)*colorsByWeight.size()/k; p += 1) {
                currCol.add(colorsByWeight.get(p));
            }
            meansToClusters.put(new Centroid(currCol), currCol);
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
        meansToClusters = this.delegateMeans(k);
        // meansToClusters = randMeansToClusters(colorsByWeight, k);
        // meansToClusters = forgyMeansToClusters(colorsByWeight, k);
        
        // Here, we need initial means to be set
        while (true)
        {
            // Assign clusters
            for (ColorWeight curr: colorsByWeight) {
                float dist = 0;
                Centroid closest = null;
                for(Centroid key : meansToClusters.keySet()) {
                    if(closest == null || key.distFromColor(curr) < dist) {
                        dist = key.distFromColor(curr);
                        closest = key;
                    }
                }
                meansToClusters.get(closest).add(curr);
            }
 
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

    private List<Centroid> kMeansCluster2(int k) {
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        // HashMap<Centroid, List<ColorWeight>> rearrangedClusters = new HashMap<>();
        meansToClusters = this.partitionAveMeans(k);
        // meansToClusters = randMeansToClusters(k);
        // meansToClusters = forgyMeansToClusters(colorsByWeight, k);
        
        // Here, we need initial means to be set
        while (true)
        {
            // Assign clusters
            for (ColorWeight curr: colorsByWeight) {
                float dist = 0;
                Centroid closest = null;
                for(Centroid key : meansToClusters.keySet()) {
                    if(closest == null || key.distFromColor(curr) < dist) {
                        dist = key.distFromColor(curr);
                        closest = key;
                    }
                }
                meansToClusters.get(closest).add(curr);
            }
 
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

    private List<Centroid> kMeansCluster(List<Centroid> centroids) {
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        centroids = Centroid.splitCentroid(centroids);
        for (Centroid centroid : centroids) {
            meansToClusters.put(centroid, new ArrayList<>());
        }
        
        while (true)
        {
            // Assign clusters
            for (ColorWeight curr: colorsByWeight) {
                meansToClusters.get(findClosestCentroid(meansToClusters.keySet(), curr)).add(curr);
            }
 
            boolean noChange = true;
            for (Centroid c : meansToClusters.keySet()) {
                // recalculateCentroid returns true if a change occurs
                if (c.recalculateCentroid(meansToClusters.get(c))) {
                    noChange = false;
                }
                meansToClusters.put(c,new ArrayList<>());
            }
            if (noChange) {
                // kMeansCluster algorithm terminates when no change is made to the centroids over an iteration
                break;
            }
        }
        return new ArrayList<>(meansToClusters.keySet());
    }

    private Centroid findClosestCentroid(Set<Centroid> centroids, Color color) {
        return centroids.stream().min(Comparator.comparingInt(c -> c.distFromColor(color))).get();        
    }

    private void graphColour(int k) {
        
        List<Centroid> centroids = this.kMeansCluster(k);
        List<ColorWeight> means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
        displayColors(means,image);

    }
    private void graphColour2(int k) {
        
        List<Centroid> centroids = this.kMeansCluster2(k);
        List<ColorWeight> means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
        displayColors(means,image);

    }

    private static void displayColors(List<ColorWeight> means, BufferedImage image) {
        DominantRectangle rect = DominantRectangle.getDominantRectangle(means, image);
        JFrame window = new JFrame();
        window.setLayout(new BorderLayout());
        window.add(rect);
        window.setSize(rect.getWidth(),rect.getHeight());
        
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

        window.setVisible(true);
    }

    private void findDominantColors() {
        
        List<Centroid> centroids = kMeansCluster(5);

        List<ColorWeight> means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
        List<ColorWeight> lastMeans = means;
        double dist = centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum(); 
        double lastDistance = 2*dist;
        // for (int k=2; k<12; k++) {
        int k = 6;
        while(lastDistance * 0.95 > dist) {
        // while(lastDistance/ dist > 1.15) {
            lastDistance = dist;
            centroids = kMeansCluster(k);
            lastMeans = means;
            means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
            dist = centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum();
            k++;
        }
        displayColors(lastMeans, image);
    }

    public void findDominantColors2(float sensitivity) {
        List<Centroid> centroids = kMeansCluster(2);
        List<Centroid> lastCentroids = null;

        // List<ColorWeight> means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
        // List<ColorWeight> lastMeans = means;
        double dist = centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum(); 
        double lastDistance = dist*2;
        // System.out.println("k = 4: " + centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum());        
        while(dist/lastDistance < sensitivity) {
            System.out.println("PPPPPPPP" + centroids.size());
            lastDistance = dist;
            lastCentroids = centroids;
            centroids = kMeansCluster(centroids);
            dist = centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum();
            System.out.println(dist);
            List<ColorWeight> means = new ArrayList<>();
            for (Centroid c : lastCentroids) {
                means.add(c.getColorWeight());
            }
            displayColors(means, image);
        }
        System.out.println("k = " + (centroids.size() - 1) + ": " + lastDistance + "sensitivity = " + sensitivity);
        System.out.println("ratio = " + dist/lastDistance);
        List<ColorWeight> means = new ArrayList<>();
        for (Centroid c : lastCentroids) {
            means.add(c.getColorWeight());
        }
        displayColors(means, image);
    }

    // private void graphValuesOfK() {
    //     List<List<Centroid>> centroidsToDistance = new ArrayList<>();
    //     for (int k=1; k<30; k++) {
    //         List<Centroid> centroids = kMeansCluster(k);
    //         centroidsToDistance.add(centroids);
    //     }
    //     List<Double> vals = new ArrayList<>();
    //     for (List<Centroid> curr:centroidsToDistance) {
    //         // System.out.println(curr.stream().mapToDouble(Centroid::sumDistanceFromMean).sum());
    //         vals.add(curr.stream().mapToDouble(Centroid::sumDistanceFromMean).sum());
    //     }
    //     LineChart chart = new LineChart(vals);
    //     chart.pack();
    //     RefineryUtilities.centerFrameOnScreen( chart );
    //     chart.setVisible(true);
    // }

        
    public static void main(String args[]) throws IOException {
        File file = new File("images\\margot.png");
        ColourScanner cs = new ColourScanner(file);
        // parseImageHue(file);
        // System.out.println(Long.MAX_VALUE);
        // long startTime = System.nanoTime();
        // cs.graphColour2(8);
        cs.findDominantColors2(0.95f);
        // long endTime = System.nanoTime();
        // System.out.println("Function 1 done in " +(endTime - startTime)/1000000);  //divide by 1000000 to get milliseconds.

        // cs.findDominantColors();
        // cs.graphValuesOfK();

    }
}
