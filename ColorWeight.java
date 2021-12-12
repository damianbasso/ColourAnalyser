import java.awt.Color;
import java.util.List;

public class ColorWeight extends Color implements Comparable<Color> {
    
    private Color colour;
    private int weight;
    
    public ColorWeight(Color colour) {
        super(colour.getRGB());
        this.weight = 1;
    }

    public ColorWeight(int colour) {
        super(colour);
        this.weight = 1;
    }

    public ColorWeight(int colour, int weight) {
        super(colour);
        this.weight = weight;
    }

    public ColorWeight(Color colour, int weight) {
        super(colour.getRGB());
        this.weight = weight;
    }

    // public void joinColours(ColorWeight cw) {
    //     int newWeight = this.getWeight() + cw.getWeight();
    //     int newRed = (cw.getRed() * cw.getWeight() + this.getRed() * this.getWeight())/newWeight; 
    //     int newBlue = (cw.getBlue() * cw.getWeight() + this.getBlue() * this.getWeight())/newWeight; 
    //     int newGreen = (cw.getGreen() * cw.getWeight() + this.getGreen() * this.getWeight())/newWeight; 
    //     return new ColorWeight(new Color(newRed, newBlue, newGreen), .getWeight() + cw2.getWeight());
    // }

    public static ColorWeight joinColours(ColorWeight cw1, ColorWeight cw2) {
        int newWeight = cw1.getWeight() + cw2.getWeight();
        int newRed = (cw1.getRed() * cw1.getWeight() + cw2.getRed() * cw2.getWeight())/newWeight; 
        int newBlue = (cw1.getBlue() * cw1.getWeight() + cw2.getBlue() * cw2.getWeight())/newWeight; 
        int newGreen = (cw1.getGreen() * cw1.getWeight() + cw2.getGreen() * cw2.getWeight())/newWeight; 
        return new ColorWeight(new Color(newRed, newBlue, newGreen), newWeight);
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ColorWeight)) {
            return false;
        }
        Color other = (Color)obj;
        return this.getRGB() == other.getRGB();
    }

    public static ColorWeight averageWeights(List<ColorWeight> colors) {
        long red = 0;
        long green = 0;
        long blue = 0;
        int weight = 0;
        for (ColorWeight curr : colors) {
            // float[] hsb = ColorWeight.RGBtoHSB(curr.getRed(), curr.getGreen(), curr.getBlue(), null);
            red += curr.getRed() * curr.getWeight();
            green += curr.getGreen() * curr.getWeight();
            blue += curr.getBlue() * curr.getWeight();
            weight += curr.getWeight();
        }

        
        Color balancedColor = new Color(Math.round(red/weight), Math.round(green/weight), Math.round(blue/weight));

        return new ColorWeight(balancedColor, weight);
    }

    @Override
    public int compareTo(Color o) {
        float i = Color.RGBtoHSB(this.getRed(), this.getGreen(), this.getBlue(), null)[0];
        float j = Color.RGBtoHSB(o.getRed(), o.getGreen(), o.getBlue(), null)[0];
        if (i<j) {
            return -1;
        }
        if (i>j) {
            return 1;
        }
        return 0;
    }

    
}
