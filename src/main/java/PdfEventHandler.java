import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * The PDF even handler<br>
 * Set gray color to every page<br>
 *
 * @author Zihao Long
 * @version 1.0, 2021-10-29 23:32
 * @since ExcelPDFReports 0.0.1
 */
public class PdfEventHandler implements IEventHandler {

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfPage page = docEvent.getPage();

        PdfCanvas canvas = new PdfCanvas(page);
        Rectangle rect = page.getPageSize();
        canvas.saveState()
                .setFillColor(ColorConstants.GRAY)
                .rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight())
                .fillStroke()
                .restoreState();
    }
}
