package ColourAnalyser;
import org.jfree.chart.ChartPanel;

import java.util.List;



import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class LineChart extends ApplicationFrame {

   public LineChart( List<Double> vals) {
      super("HI");
      JFreeChart lineChart = ChartFactory.createLineChart(
         "chart<3",
         "k","Number of Colours",
         createDataset(vals),
         PlotOrientation.VERTICAL,
         true,true,false);
         
      // Create an NumberAxis
        // NumberAxis xAxis = new NumberAxis();
        // xAxis.setTickUnit(new NumberTickUnit(1000));

        // // Assign it to the chart
        // XYPlot plot = (XYPlot) lineChart.getPlot();
        // plot.setDomainAxis(xAxis);
        System.out.println(vals);
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );
   }

   public DefaultCategoryDataset createDataset(List<Double> vals ) {
      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
      int k = 1;
      for (double val : vals) {
        dataset.addValue( val , "k" , Integer.toString(k));
        k++;
    }
       return dataset;
   }
}