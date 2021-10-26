import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.IOException;
import java.util.ArrayList;

public class pdfGen {
    public static void studentPdf(ArrayList<String> headerInfo, ArrayList<String> studentInfo,String Path,PdfDocument pdfDoc) throws IOException{

        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        //PdfWriter writer = new PdfWriter(Path+"students.pdf");
        // Creating a PdfDocument
        //PdfDocument pdfDoc = new PdfDocument(writer);

        //Adding a new page
        pdfDoc.addNewPage();
        //Colours the page
        PdfPage coloured = pdfDoc.getFirstPage();
        PdfCanvas canvas = new PdfCanvas(coloured);
        Rectangle rect = coloured.getPageSize();
        canvas.saveState()
                .setFillColor(ColorConstants.GRAY)
                .rectangle(rect)
                .fillStroke();

        // Creating a Document
        Document document = new Document(pdfDoc);

        document.setFont(font);
        //spacing
        paraLineBreaks(document,1);
        //add heading
        addPara(document);
        addLineBreak(document);

        paraLineBreaks(document,2);
        addInfoTable(document,headerInfo,studentInfo);

        paraLineBreaks(document,2);
        addGradeTable(document,headerInfo,studentInfo);

        for (int i = pdfDoc.getNumberOfPages(); i > 1; i--) {
            pdfDoc.movePage(i, pdfDoc.getNumberOfPages()+1-i);
        }
    }
    public static PdfDocument generatePdf(String Path)throws IOException{


        PdfWriter writer = new PdfWriter(Path+"Main.pdf");
        // Creating a PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addNewPage();

        return pdfDoc;
    }
    public static void addLineBreak(Document d) {
        SolidLine line = new SolidLine(1f);
        LineSeparator ls = new LineSeparator(line);
        d.add(ls);
    }
    public static void addPara (Document d)throws IOException {
        final String TITLE = "MU Computer Science FYP Grading Report 2021";
        Color muOrange = new DeviceRgb(251,190,8);
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Paragraph para1 = new Paragraph(TITLE);
        para1.setTextAlignment(TextAlignment.CENTER);
        para1.setFontSize(20)
                .setBold()
                .setFont(font).setFontColor(muOrange);
        d.add(para1);
    }
    public static void paraLineBreaks(Document d,Integer x){
        for (int i = 0; i < x; ++i){
            d.add(new Paragraph());
        }
    }
    public static void addInfoTable(Document d,ArrayList<String> headerInfo, ArrayList<String> studentInfo){
        //two cells (student number+ name)
        Table table = new Table(UnitValue.createPercentArray(new float[]{5,5}));
        infoTableStyling(table);

        Paragraph paraUpperSN = new Paragraph();
        Text textUpperSN = new Text(headerInfo.get(5));
        textUpperSN.setUnderline();
        paraUpperSN.add(textUpperSN);

        Paragraph paraStudentNumber = new Paragraph();
        Text textStudentNumber = new Text(studentInfo.get(5));
        infoTextStyling(textStudentNumber);
        paraStudentNumber.add(textStudentNumber);

        table.addCell(new Cell().add(paraUpperSN.setTextAlignment(TextAlignment.LEFT)).add(paraStudentNumber));

        Paragraph paraUpperName = new Paragraph();
        Text textUpperName = new Text(headerInfo.get(4));
        textUpperName.setUnderline();
        paraUpperName.add(textUpperName);

        Paragraph paraName = new Paragraph();
        Text textName = new Text(studentInfo.get(4));
        infoTextStyling(textName);
        paraName.add(textName);

        table.addCell(new Cell().add(paraUpperName.setTextAlignment(TextAlignment.LEFT)).add(paraName));

        //one cell (project type)
        Table table2 = new Table(1);
        infoTableStyling(table2);

        Paragraph paraUpperRPT = new Paragraph();
        Text textUpperRPT = new Text(headerInfo.get(2)+" "+headerInfo.get(6));
        paraUpperRPT.add(textUpperRPT).setUnderline();

        Paragraph paraRoleProjectType = new Paragraph();
        Text textRoleProjectType = new Text(studentInfo.get(1)+" "+studentInfo.get(6));
        infoTextStyling(textRoleProjectType);
        paraRoleProjectType.add(textRoleProjectType);

        table2.addCell(new Cell().add(paraUpperRPT.setTextAlignment(TextAlignment.LEFT)).add(paraRoleProjectType));

        //two cells (grader and other grader names)
        Table table3 = new Table(UnitValue.createPercentArray(new float[]{5,5}));
        infoTableStyling(table3);

        Paragraph paraUpperGrader = new Paragraph();
        Text textUpperGrader = new Text(headerInfo.get(0));
        paraUpperGrader.add(textUpperGrader).setUnderline();

        Paragraph paraGrader = new Paragraph();
        Text textGrader = new Text(studentInfo.get(0));
        infoTextStyling(textGrader);
        paraGrader.add(textGrader);

        table3.addCell(new Cell().add(paraUpperGrader.setTextAlignment(TextAlignment.LEFT)).add(paraGrader));

        Paragraph paraUpperOther = new Paragraph();
        Text textUpperOther = new Text(headerInfo.get(3));
        paraUpperOther.add(textUpperOther).setUnderline();

        Paragraph paraOtherGrader= new Paragraph();
        Text textOtherGrader = new Text(studentInfo.get(3));
        infoTextStyling(textOtherGrader);
        paraOtherGrader.add(textOtherGrader);

        table3.addCell(new Cell().add(paraUpperOther.setTextAlignment(TextAlignment.LEFT)).add(paraOtherGrader));

        //one cell (Result)
        Table table4 = new Table(1);
        infoTableStyling(table4);

        Paragraph paraUpperScore = new Paragraph();
        Text textUpperScore = new Text(headerInfo.get(8));
        paraUpperScore.add(textUpperScore).setUnderline();

        Paragraph paraScore= new Paragraph();
        Text textScore = new Text(studentInfo.get(8));
        textScore.setFontColor(ColorConstants.BLACK).setBold().setCharacterSpacing(1);
        paraScore.add(textScore);

        table4.addCell(new Cell().add(paraUpperScore.setTextAlignment(TextAlignment.LEFT)).add(paraScore));

        d.add(table);
        d.add(table2);
        d.add(table3);
        d.add(table4);
    }
    public static void addGradeTable(Document d,ArrayList<String> headerInfo, ArrayList<String> studentInfo){
        Color muOrange = new DeviceRgb(251,190,8);
        Color muGrey = new DeviceRgb(127,127,127);
        //2 cell titles (grader and other grader header)
        Table table = new Table(UnitValue.createPercentArray(new float[]{5,5}));
        table.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(muGrey)
                .setFontColor(muOrange)
                .setTextAlignment(TextAlignment.CENTER)
                .setWidth(500)
                .setUnderline();

        table.addCell(new Cell().add(new Paragraph(headerInfo.get(0)+" ")));
        table.addCell(new Cell().add(new Paragraph(headerInfo.get(3))));

        //4 cell modules (grades+results)
        Table table2 = new Table(UnitValue.createPercentArray(new float[]{3,5,3,5}));
        table2.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(muGrey)
                .setFontColor(muOrange)
                .setTextAlignment(TextAlignment.LEFT)
                .setWidth(500)
                .setFontSize(10);

        for(int i = 9; i < 22; i+=2) {
            //modules
            table2.addCell(new Cell().add(new Paragraph(headerInfo.get(i))));
            table2.addCell(new Cell().add(new Paragraph(studentInfo.get(i) + "/" + studentInfo.get(i+1)).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER)));
            table2.addCell(new Cell().add(new Paragraph(headerInfo.get(i))));
            table2.addCell(new Cell().add(new Paragraph(studentInfo.get(i) + "/" + studentInfo.get(i+1)).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER)));
        }

        //one cell (Score)
        Table table3 = new Table(UnitValue.createPercentArray(new float[]{10}));
        table3.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(muGrey)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setWidth(500);

        table3.addCell(new Cell().add(new Paragraph(headerInfo.get(8)+" = "+studentInfo.get(8))));
        //one cell (Title+ comments)
        Table table4 = new Table(UnitValue.createPercentArray(new float[]{10}));
        table4.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(muGrey)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.LEFT)
                .setWidth(500);

        Paragraph para1 = new Paragraph();
        Text text1 = new Text(headerInfo.get(23));
        text1.setFontColor(muOrange).setUnderline();
        para1.add(text1);

        Paragraph para2 = new Paragraph();
        Text text2 = new Text(studentInfo.get(23));
        para2.add(text2);

        table4.addCell(new Cell().add(para1).add(para2.setTextAlignment(TextAlignment.CENTER)));

        Paragraph para3 = new Paragraph();
        Text text3 = new Text(headerInfo.get(24));
        text3.setFontColor(muOrange).setUnderline();
        para3.add(text3);

        Paragraph para4 = new Paragraph();
        Text text4 = new Text(studentInfo.get(24));
        para4.add(text4);

        table4.addCell(new Cell().add(para3).add(para4.setTextAlignment(TextAlignment.CENTER)));

        d.add(table);
        d.add(table2);
        d.add(table3);
        d.add(table4);

    }
    public static void infoTableStyling(Table t){
        Color muOrange = new DeviceRgb(251,190,8);
        Color muText = new DeviceRgb(157,128,60);
        Color muGrey = new DeviceRgb(217,217,217);

        t.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(muGrey)
                .setFontColor(muText)
                .setTextAlignment(TextAlignment.CENTER)
                .setWidth(500f);
    }
    public static void infoTextStyling(Text infoText){
        infoText.setFontColor(ColorConstants.BLACK)
                .setBold()
                .setCharacterSpacing(1);
    }
}
