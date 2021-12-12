import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
// import java.awt.*;
import java.awt.Color;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import java.awt.image.BufferedImage;

public class ColourScanner {

    // the number of dominant colours we're looking for
    static final private int numOfDom = 5;

    // Currently only considers hue
    public static float distBetweenColors(Color c1, Color c2) {
        float i = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null)[0];
        float j = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null)[0];
        return Math.abs(i-j);
    }

    // sort and partition
    private static HashMap<ColorWeight, List<ColorWeight>> setMeansToClusters(List<ColorWeight> colorsByWeight, int k) {
        System.out.println("sorting");
        colorsByWeight.sort(new ByHSB());
        HashMap<ColorWeight, List<ColorWeight>> meansToClusters = new HashMap<>();
        System.out.println("Initialising");
        // for (i = 0; i<colorsByWeight.size(); i += (colorsByWeight.size()/numOfDom)) {
        for (int i = 0; i<k; i += 1) {
            // System.out.println(i * colorsByWeight.size()/ numOfDom);
            meansToClusters.put(colorsByWeight.get(i * colorsByWeight.size()/ numOfDom), new ArrayList<>());
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
        
    private static List<ColorWeight> kMeansCluster(List<ColorWeight> colorsByWeight, int k) {
        HashMap<ColorWeight, List<ColorWeight>> meansToClusters = new HashMap<>();
        HashMap<ColorWeight, List<ColorWeight>> rearrangedClusters = new HashMap<>();
        meansToClusters = setMeansToClusters(colorsByWeight, k);
        // meansToClusters = randMeansToClusters(colorsByWeight, k);
        // meansToClusters = forgyMeansToClusters(colorsByWeight, k);
        
        // Here, we need initial means to be set
        while (true)
        {
            // Assign clusters
            System.out.println("Assigning");
            for (ColorWeight curr: colorsByWeight) {
                ColorWeight closest = null;
                float dist = 0;
                // System.out.println("c");
                for(ColorWeight key : meansToClusters.keySet()) {
                    // System.out.println("d");
                    if(closest == null || ColourScanner.distBetweenColors(curr, key) < dist) {
                        // System.out.println("Distance: " + dist);
                        dist = ColourScanner.distBetweenColors(curr, key);
                        // System.out.println("Distance: " + dist);
                        closest = key;
                    }
                }
                meansToClusters.get(closest).add(curr);
            }
            // System.out.println("LEGO " + meansToClusters);
            rearrangedClusters = new HashMap<>();
            System.out.println("newMeans");
            for (ColorWeight key : meansToClusters.keySet()) {  
                // System.out.println("OOOOOOOOOOOOOOOOOOOOOOO");
                // System.out.println(meansToClusters.size());
                
                rearrangedClusters.put(ColorWeight.averageWeights(meansToClusters.get(key)), new ArrayList<>());
            }
            if (rearrangedClusters.keySet().equals(meansToClusters.keySet())) {
                // System.out.println(rearrangedClusters.keySet());
                // System.out.println(meansToClusters.keySet());
                System.out.println("BREAK");
                break;
            }
            meansToClusters = rearrangedClusters;
        }
        return new ArrayList<>(meansToClusters.keySet());
    }

    public static void main(String args[]) throws IOException {

        File file = new File("C:\\Users\\damia\\OneDrive\\Documents\\Code\\lionking2.jpg");
        if (file.exists()) {
            System.out.println("found ya bum");
        }
        // int i = 0;
        // int sumRed = 0;
        // int sumGreen = 0;
        // int sumBlue = 0;
        
        List<ColorWeight> colorsByWeight = parseImage(file);
        
        // mean :: cluster
        // ColorWeight :: List<ColorWeight>
        
        List<ColorWeight> sorted = kMeansCluster(colorsByWeight, numOfDom);
        DominantRectangle rect = new DominantRectangle(sorted);
        JFrame window = new JFrame();
        window.setSize(840, 560);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(rect);
        window.setVisible(true);

    }
}
