package ColourAnalyser;
import java.util.Comparator;
import java.awt.Color;


public class ByHSB implements Comparator<ColorWeight> {

    @Override
    public int compare(ColorWeight o1, ColorWeight o2) {
        float[] i = Color.RGBtoHSB(o1.getRed(), o1.getGreen(), o1.getBlue(), null);
        float[] j = Color.RGBtoHSB(o2.getRed(), o2.getGreen(), o2.getBlue(), null);
        if (i[0] < j[0]) {
            return -1;
        }
        if (i[0]>j[0]) {
            return 1;
        }
        if (i[1] < j[1]) {
            return -1;
        }
        if (i[1]>j[1]) {
            return 1;
        }
        if (i[2] < j[2]) {
            return -1;
        }
        if (i[2]>j[2]) {
            return 1;
        }
        
        return 0;
        
    }
    
}
