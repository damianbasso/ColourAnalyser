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


public class ColourScanner {

    // the number of dominant colours we're looking for
    static final private int numOfDom = 8;

    // public static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
    //     Image scaledImage = img.getScaledInstance( width, height, Image.SCALE_SMOOTH);
    //     BufferedImage imageBuff = new BufferedImage(width, scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
    //     Graphics g = imageBuff.createGraphics();
    //     g.drawImage(scaledImage, 0, 0, new Color(0, 0, 0), null);
    //     g.dispose();
    //     ImageIO.write(imageBuff, "jpg", newFile);
    // }

    // Currently only considers hue
    public static float distBetweenColors(Color c1, Color c2) {
        float i = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null)[0];
        float j = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null)[0];
        return Math.abs(i-j);
    }

    // sort and partition
    private static HashMap<Centroid, List<ColorWeight>> setMeansToClusters(List<ColorWeight> colorsByWeight, int k) {
        System.out.println("sorting");
        colorsByWeight.sort(new ByHSB());
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        System.out.println("Initialising");
        // for (i = 0; i<colorsByWeight.size(); i += (colorsByWeight.size()/numOfDom)) {
        for (int i = 0; i<k; i += 1) {
            // System.out.println(i * colorsByWeight.size()/ numOfDom);
            meansToClusters.put(new Centroid( Arrays.asList(colorsByWeight.get(i * colorsByWeight.size()/ numOfDom))), new ArrayList<>());
            // System.out.println("Color is:" + colorsByWeight.get(i));
        }
        return meansToClusters;
    }

    private static HashMap<ColorWeight, List<ColorWeight>> randMeansToClusters(List<ColorWeight> colorsByWeight, int k) {
        
        HashMap<ColorWeight, List<ColorWeight>> meansToClusters = new HashMap<>();
        for (int i =0; i<k; i++) {
            meansToClusters.put(ColorWeight.averageWeights(colorsByWeight.subList(i * colorsByWeight.size()/k, (i + 1) * colorsByWeight.size()/k)), new ArrayList<>());
        }
        return meansToClusters;
    }

    private static HashMap<ColorWeight, List<ColorWeight>> forgyMeansToClusters(List<ColorWeight> colorsByWeight, int k) {
        
        HashMap<ColorWeight, List<ColorWeight>> meansToClusters = new HashMap<>();
        Random rand = new Random();
        for (int i =0; i<k; i++) {
            meansToClusters.put(colorsByWeight.get(rand.nextInt(colorsByWeight.size())), new ArrayList<>());
        }
        return meansToClusters;
    }

    // private static HashMap<ColorWeight, List<ColorWeight>> forgyMeansToClusters(List<ColorWeight> colorsByWeight, int k) {

    private static List<ColorWeight> parseImage(File file) throws IOException {
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
        return colorsByWeight;
    }
        
    private static List<Centroid> kMeansCluster(List<ColorWeight> colorsByWeight, int k) {
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        HashMap<Centroid, List<ColorWeight>> rearrangedClusters = new HashMap<>();
        meansToClusters = setMeansToClusters(colorsByWeight, k);
        // meansToClusters = randMeansToClusters(colorsByWeight, k);
        // meansToClusters = forgyMeansToClusters(colorsByWeight, k);
        
        // Here, we need initial means to be set
        while (true)
        {
            // Assign clusters
            // System.out.println("Assigning");
            
            for (ColorWeight curr: colorsByWeight) {
                Centroid closest = null;
                float dist = 0;
                // System.out.println("c");
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
            rearrangedClusters = new HashMap<>();
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

    public static void main(String args[]) throws IOException {

        File file = new File("C:\\Users\\damia\\Documents\\code\\ColourAnalyser\\scar.jpg");
        if (file.exists()) {
            System.out.println("found ya bum");
        }
        List<ColorWeight> colorsByWeight = parseImage(file);
        // System.out.println("cheeese");
        // System.out.println(colorsByWeight.size());
        // return;
        // mean :: cluster
        // ColorWeight :: List<ColorWeight>
        for(int k = 3; k< 10; k++ ) {        
            List<Centroid> centroids = kMeansCluster(colorsByWeight, k);
            List<ColorWeight> means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
            DominantRectangle rect = new DominantRectangle(means);
            JFrame window = new JFrame();
            window.setSize(840, 560);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.add(rect);

            window.setVisible(true);
        }
    }
}
