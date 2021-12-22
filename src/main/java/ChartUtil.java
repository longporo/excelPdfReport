import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * JfreeChart Util<br>
 *
 * @param 
 * @return 
 * @author Zihao Long
 */
public class ChartUtil{
    private static final Color[] BAR_COLORS = new Color[]{
            new Color(79,129,189),
            new Color(192, 80, 77),
            new Color(155, 187, 89),
    };

    private static final Color[] PIE_COLORS = new Color[]{
            new Color(75, 172, 198),
            new Color(128, 100, 162),
            new Color(155, 187, 89),
            new Color(192, 80, 77),
            new Color(79, 129, 189),
            new Color(44, 77, 117),
            new Color(247, 150, 70),
            new Color(165, 165, 165),
    };


    /**
     * Init chart theme<br>
     *
     * @param []
     * @return org.jfree.chart.StandardChartTheme
     * @author Zihao Long
     */
    private static StandardChartTheme initChartTheme(){
        StandardChartTheme currentTheme = new StandardChartTheme("JFree");
        // background color
        currentTheme.setPlotBackgroundPaint(new Color(255, 255, 204, 0));
        // outline color
        currentTheme.setPlotOutlinePaint(new Color(0, 0, 0, 0));
        // range grid line color
        currentTheme.setRangeGridlinePaint(new Color(78, 74, 74));
        return currentTheme;
    }

    /**
     * Generate a line chart<br>
     *
     * @param [title, categoryAxisLabel, valueAxisLabel, dataset]
     * @return org.jfree.chart.JFreeChart
     * @author Zihao Long
     */
    public static JFreeChart lineChart(String title, String categoryAxisLabel, String valueAxisLabel, DefaultCategoryDataset dataset){
        ChartFactory.setChartTheme(initChartTheme());

        JFreeChart chart = ChartFactory.createLineChart(
                title,
                categoryAxisLabel,
                valueAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        // 折现点显示数值
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        return chart;
    }

    /**
     * Gernerate a bar chart<br>
     *
     * @param [title, categoryAxisLabel, valueAxisLabel, dataset]
     * @return org.jfree.chart.JFreeChart
     * @author Zihao Long
     */
    public static JFreeChart barChart(String title, String categoryAxisLabel, String valueAxisLabel, DefaultCategoryDataset dataset){
        ChartFactory.setChartTheme(initChartTheme());

        JFreeChart chart = ChartFactory.createBarChart(
                title,
                categoryAxisLabel,
                valueAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        // solid color
        renderer.setBarPainter(new StandardBarPainter());
        // display number on bar
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setPositiveItemLabelPositionFallback(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
        renderer.setDrawBarOutline(false);
        // bar margin
        renderer.setItemMargin(0.0);
        // bar max width
        renderer.setMaximumBarWidth(0.05);
        // bar color
        renderer.setSeriesPaint(0, BAR_COLORS[0]);
        return chart;
    }

    /**
     * Generate a pie chart<br>
     *
     * @param [title, dataset]
     * @return org.jfree.chart.JFreeChart
     * @author Zihao Long
     */
    public static JFreeChart pieChart(String title, DefaultPieDataset dataset){
        ChartFactory.setChartTheme(initChartTheme());

        JFreeChart chart = ChartFactory.createPieChart(
                title,
                dataset,
                true,
                true,
                false
        );
        PiePlot plot = (PiePlot) chart.getPlot();
        // pie color
        for (int i = 0; i < dataset.getKeys().size(); i++) {
            plot.setSectionPaint(dataset.getKey(i), PIE_COLORS[i]);
        }
        // pie outline color
        plot.setDefaultSectionOutlinePaint(new Color(255, 255, 255));
        // pie outline stroke width
        plot.setDefaultSectionOutlineStroke(new BasicStroke(3));
        // label color
        plot.setLabelLinkPaint(new Color(255,255,255, 0));
        // label background color
        plot.setLabelBackgroundPaint(new Color(255, 255, 255,0));
        // label outline color
        plot.setLabelOutlinePaint(new Color(255, 255, 255, 0));
        // label shadow color
        plot.setLabelShadowPaint(new Color(255, 255, 255, 0));
        // pie shadow color
        plot.setShadowPaint(new Color(255, 255, 255, 0));
        // express label as a percentage
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(("{0}{2}"), NumberFormat.getNumberInstance(),new DecimalFormat("0.00%")));
        return chart;
    }
}
