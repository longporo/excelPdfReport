import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.*;

public class PdfGen {


    public static void generatePdf(List<Student> stuList) throws IOException {
        PdfWriter writer = new PdfWriter(Frame.PDF_FILE_PATH);
        // Creating a PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new PdfEventhandler());

        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Document document = new Document(pdfDoc);
        document.setFont(font);

        // sort by grade desc
        Collections.sort(stuList, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return o2.getGrade().compareTo(o1.getGrade());
            }
        });

        // add student list
        addStuListTable(stuList, pdfDoc, document);

        // add grade stats
        // New page
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        addGradeStats(stuList, pdfDoc, document);

        // add student grade detail
        for (Student stu : stuList) {
            // New page
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            studentPdf(stu, pdfDoc, document);
        }
        pdfDoc.close();
    }

    public static void studentPdf(Student stu, PdfDocument pdfDoc, Document document) throws IOException{
        //spacing
        paraLineBreaks(document,1);
        //add heading
        addPara(document);
        addLineBreak(document);

        paraLineBreaks(document,2);
        addInfoTable(document, stu);

        paraLineBreaks(document,2);
        addGradeTable(document, stu);
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
    public static void addInfoTable(Document d, Student student){
        //two cells (student number+ name)
        Table table = new Table(UnitValue.createPercentArray(new float[]{5,5}));
        infoTableStyling(table);

        Paragraph paraUpperSN = new Paragraph();
        Text textUpperSN = new Text("Student Number");
        textUpperSN.setUnderline();
        paraUpperSN.add(textUpperSN);

        Paragraph paraStudentNumber = new Paragraph();
        Text textStudentNumber = new Text(student.getStudentNo());
        infoTextStyling(textStudentNumber);
        paraStudentNumber.add(textStudentNumber);

        table.addCell(new Cell().add(paraUpperSN.setTextAlignment(TextAlignment.LEFT)).add(paraStudentNumber));

        Paragraph paraUpperName = new Paragraph();
        Text textUpperName = new Text("Student Name");
        textUpperName.setUnderline();
        paraUpperName.add(textUpperName);

        Paragraph paraName = new Paragraph();
        Text textName = new Text(student.getStudentName());
        infoTextStyling(textName);
        paraName.add(textName);

        table.addCell(new Cell().add(paraUpperName.setTextAlignment(TextAlignment.LEFT)).add(paraName));

        //one cell (project type)
        Table table2 = new Table(1);
        infoTableStyling(table2);

        Paragraph paraUpperRPT = new Paragraph();
        Text textUpperRPT = new Text("Project Number and Title");
        paraUpperRPT.add(textUpperRPT).setUnderline();

        Paragraph paraRoleProjectType = new Paragraph();
        Text textRoleProjectType = new Text(student.getProjectNo() + "-" + student.getTitle());
        infoTextStyling(textRoleProjectType);
        paraRoleProjectType.add(textRoleProjectType);

        table2.addCell(new Cell().add(paraUpperRPT.setTextAlignment(TextAlignment.LEFT)).add(paraRoleProjectType));

        //two cells (grader and other grader names)
        Table table3 = new Table(UnitValue.createPercentArray(new float[]{5,5}));
        infoTableStyling(table3);

        Paragraph paraUpperGrader = new Paragraph();
        Text textUpperGrader = new Text("Supervisor");
        paraUpperGrader.add(textUpperGrader).setUnderline();

        Paragraph paraGrader = new Paragraph();
        Text textGrader = new Text(student.getGrader());
        infoTextStyling(textGrader);
        paraGrader.add(textGrader);

        table3.addCell(new Cell().add(paraUpperGrader.setTextAlignment(TextAlignment.LEFT)).add(paraGrader));

        Paragraph paraUpperOther = new Paragraph();
        Text textUpperOther = new Text("Second Reader");
        paraUpperOther.add(textUpperOther).setUnderline();

        Paragraph paraOtherGrader= new Paragraph();
        Text textOtherGrader = new Text(student.getOtherGrader());
        infoTextStyling(textOtherGrader);
        paraOtherGrader.add(textOtherGrader);

        table3.addCell(new Cell().add(paraUpperOther.setTextAlignment(TextAlignment.LEFT)).add(paraOtherGrader));

        //one cell (Result)
        Table table4 = new Table(1);
        infoTableStyling(table4);

        Paragraph paraUpperScore = new Paragraph();
        Text textUpperScore = new Text("Grade Awarded");
        paraUpperScore.add(textUpperScore).setUnderline();

        Paragraph paraScore= new Paragraph();
        Text textScore = new Text(decimalToString(student.getGrade()));
        textScore.setFontColor(ColorConstants.BLACK).setBold().setCharacterSpacing(1);
        paraScore.add(textScore);

        table4.addCell(new Cell().add(paraUpperScore.setTextAlignment(TextAlignment.LEFT)).add(paraScore));

        d.add(table);
        d.add(table2);
        d.add(table3);
        d.add(table4);
    }
    public static void addGradeTable(Document d, Student student){
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

        table.addCell(new Cell().add(new Paragraph("Supervisor")));
        table.addCell(new Cell().add(new Paragraph("Second Reader")));

        //4 cell modules (grades+results)
        Table table2 = new Table(UnitValue.createPercentArray(new float[]{3,5,3,5}));
        table2.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(muGrey)
                .setFontColor(muOrange)
                .setTextAlignment(TextAlignment.LEFT)
                .setWidth(500)
                .setFontSize(10);

        addValToGradeTable(table2, "Abstract", student.getAbstractScr(), student.getAbstractScrX());
        addValToGradeTable(table2, "Motivation", student.getMotivation(), student.getMotivationX());
        addValToGradeTable(table2, "Background", student.getBackground(), student.getBackgroundX());
        addValToGradeTable(table2, "Problem", student.getProblem(), student.getProblemX());
        addValToGradeTable(table2, "Solution", student.getSolution(), student.getSolutionX());
        addValToGradeTable(table2, "Conclusion or Testing and Evaluation", student.getCte(), student.getCteX());
        addValToGradeTable(table2, "Presentation", student.getPresentation(), student.getPresentationX());

        //one cell (Score)
        Table table3 = new Table(UnitValue.createPercentArray(new float[]{10}));
        table3.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(muGrey)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setWidth(500);

        table3.addCell(new Cell().add(new Paragraph("Grade Average = "+ decimalToString(student.getGrade()))));
        //one cell (Title+ comments)
        Table table4 = new Table(UnitValue.createPercentArray(new float[]{10}));
        table4.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(muGrey)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.LEFT)
                .setWidth(500);

        Paragraph para1 = new Paragraph();
        Text text1 = new Text("Supervisor Comments:");
        text1.setFontColor(muOrange).setUnderline();
        para1.add(text1);

        Paragraph comment = new Paragraph(student.getComment());

        table4.addCell(new Cell().add(para1).add(comment.setTextAlignment(TextAlignment.CENTER)));

        Paragraph para3 = new Paragraph();
        Text text3 = new Text("Second Reader Comments:");
        text3.setFontColor(muOrange).setUnderline();
        para3.add(text3);

        Paragraph title = new Paragraph(student.getTitle());

        table4.addCell(new Cell().add(para3).add(title.setTextAlignment(TextAlignment.CENTER)));

        d.add(table);
        d.add(table2);
        d.add(table3);
        d.add(table4);

    }

    /**
     * Add value to grade table<br>
     *
     * @param [table, key, val, valX]
     * @return void
     * @author Zihao Long
     */
    private static void addValToGradeTable(Table table, String key, BigDecimal val, BigDecimal valX) {
        table.addCell(new Cell().add(new Paragraph(key)));
        table.addCell(new Cell().add(new Paragraph(decimalToString(val) + "/" + decimalToString(valX)).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER)));
        table.addCell(new Cell().add(new Paragraph(key)));
        table.addCell(new Cell().add(new Paragraph(decimalToString(val) + "/" + decimalToString(valX)).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER)));
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

    /**
     * Add grade stats to the first page<br>
     *
     * @param [stuList, mainPdf]
     * @param stuList
     * @param document
     * @return void
     * @author Zihao Long
     */
    public static void addGradeStats(List<Student> stuList, PdfDocument mainPdf, Document document) throws IOException {
        // calculate stats
        // Median
        BigDecimal median;
        int size = stuList.size();
        if(size % 2 == 1){
            median = stuList.get((size - 1) / 2).getGrade();
        } else {
            median = stuList.get(size / 2 - 1).getGrade().add(stuList.get(size / 2).getGrade()).divide(new BigDecimal(2));
        }

        // Average
        BigDecimal totalScore = BigDecimal.ZERO;
        for (Student stu : stuList) {
            totalScore = totalScore.add(stu.getGrade());
        }
        BigDecimal average = totalScore.divide(new BigDecimal(stuList.size()), 5, BigDecimal.ROUND_UP);

        // Standard Deviation
        double standardDeviation = 0.0;
        for(Student stu: stuList) {
            standardDeviation += Math.pow(stu.getGrade().subtract(average).doubleValue(), 2);
        }
        standardDeviation = Math.sqrt(standardDeviation / size);

        // max and min
        BigDecimal minScore = stuList.get(size - 1).getGrade();
        BigDecimal maxScore = stuList.get(0).getGrade();

        // count of grades
        Map<String, Integer> gradeMap = new LinkedHashMap<>();
        // init grade map
        gradeMap.put(">=70", 0);
        gradeMap.put("60-69", 0);
        gradeMap.put("50-59", 0);
        gradeMap.put("40-49", 0);
        gradeMap.put("35-39", 0);
        gradeMap.put("30-34", 0);
        gradeMap.put("20-29", 0);
        gradeMap.put(">=10-19", 0);
        gradeMap.put("<10", 0);

        for (Student stu : stuList) {
            int score = stu.getGrade().intValue();
            String key = null;
            if (score < 10) {
                key = "<10";
            } else if (score >= 10 && score <= 19) {
                key = ">=10-19";
            } else if (score >= 20 && score <= 29) {
                key = "20-29";
            } else if (score >= 30 && score <= 34) {
                key = "30-34";
            } else if (score >= 35 && score <= 39) {
                key = "35-39";
            } else if (score >= 40 && score <= 49) {
                key = "40-49";
            } else if (score >= 50 && score <= 59) {
                key = "50-59";
            } else if (score >= 60 && score <= 69) {
                key = "60-69";
            } else if (score >= 70) {
                key = ">=70";
            }
            gradeMap.put(key, gradeMap.get(key) + 1);
        }

        // add values to table
        Color muOrange = new DeviceRgb(251,190,8);
        Color muGrey = new DeviceRgb(127,127,127);
        // 2 columns table
        Table table = new Table(UnitValue.createPercentArray(new float[]{5,5}));
        table.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(muGrey)
                .setFontColor(muOrange)
                .setTextAlignment(TextAlignment.CENTER)
                .setWidth(500)
                .setPageNumber(2);

        // add table header
        Cell titleCell = new Cell(1, 10).add(new Paragraph("Grade Stats"));
        titleCell.setPaddingTop(10f);
        titleCell.setPaddingBottom(10f);
        titleCell.setFontSize(15f);
        table.addCell(titleCell);

        // add content
        addCellsTo2ColumnsTable(table, "Median", decimalToString(median));
        addCellsTo2ColumnsTable(table, "Average", decimalToString(average));
        addCellsTo2ColumnsTable(table, "Standard Deviation", decimalToString(new BigDecimal(standardDeviation)));
        addCellsTo2ColumnsTable(table, "Max", decimalToString(maxScore));
        addCellsTo2ColumnsTable(table, "Min", decimalToString(minScore));
        addCellsTo2ColumnsTable(table, "\n", "");
        addCellsTo2ColumnsTable(table, "Count of grades:", String.valueOf(size));
        Set<String> gradeKeySet = gradeMap.keySet();
        for (String key : gradeKeySet) {
            addCellsTo2ColumnsTable(table, key, gradeMap.get(key).toString());
        }

        //spacing
        paraLineBreaks(document,1);
        //add heading
        addPara(document);
        addLineBreak(document);
        paraLineBreaks(document,2);
        document.add(table);
    }

    /**
     * Set bigDecimal to string by scale<br>
     *
     * @param [decimal, scale]
     * @return java.lang.String
     * @author Zihao Long
     */
    public static String decimalToString(BigDecimal decimal, int...scaleArr) {
        if (decimal == null) {
            return " - ";
        }
        int scale = 1;
        if (scaleArr.length != 0) {
            scale = scaleArr[0];
        }
        return decimal.setScale(scale, BigDecimal.ROUND_UP).toString();
    }

    /**
     * Add cells to the two columns table<br>
     *
     * @param [key, value]
     * @param table
     * @return void
     * @author Zihao Long
     */
    private static void addCellsTo2ColumnsTable(Table table, String key, String value) {
        table.addCell(new Cell().add(new Paragraph(key)));

        Paragraph valuePg = new Paragraph(value);
        valuePg.setFontColor(ColorConstants.WHITE);
        table.addCell(new Cell().add(valuePg));
    }

    /**
     * Add student list table<br>
     *
     * @param [stuList, mainPdf]
     * @param document
     * @return void
     * @author Zihao Long
     */
    public static void addStuListTable(List<Student> stuList, PdfDocument mainPdf, Document document) throws IOException {
        // add values to table
        Color muOrange = new DeviceRgb(251,190,8);
        Color muGrey = new DeviceRgb(127,127,127);
        // 2 columns table
        Table table = new Table(UnitValue.createPercentArray(new float[]{2.5f, 2.5f, 2.5f, 2.5f}));
        table.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(muGrey)
                .setFontColor(muOrange)
                .setTextAlignment(TextAlignment.CENTER)
                .setWidth(500);

        // add table header
        addStuListTableHeader(table, "Project No.");
        addStuListTableHeader(table, "Student Name");
        addStuListTableHeader(table, "Student No.");
        addStuListTableHeader(table, "Grade");

        // add student list
        for (Student student : stuList) {
            addStuListTableBody(table, student.getProjectNo());
            addStuListTableBody(table, student.getStudentName());
            addStuListTableBody(table, student.getStudentNo());
            addStuListTableBody(table, decimalToString(student.getGrade()));
        }

        //spacing
        paraLineBreaks(document,1);
        //add heading
        addPara(document);
        addLineBreak(document);
        paraLineBreaks(document,2);
        document.add(table);
    }

    /**
     * Add student list table header<br>
     *
     * @param [table, headerStr]
     * @return void
     * @author Zihao Long
     */
    private static void addStuListTableHeader(Table table, String headerStr) {
        Cell header = new Cell().add(new Paragraph(headerStr));
        header.setFontSize(15f);
        table.addCell(header);
    }

    /**
     * Add student list table body<br>
     *
     * @param [table, bodyStr]
     * @return void
     * @author Zihao Long
     */
    private static void addStuListTableBody(Table table, String bodyStr) {
        Cell header = new Cell().add(new Paragraph(bodyStr).setFontColor(ColorConstants.WHITE));
        table.addCell(header);
    }

}
