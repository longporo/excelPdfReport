package service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
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
import org.jfree.chart.ChartUtils;
import org.jfree.data.category.DefaultCategoryDataset;
import pojo.Student;
import util.ChartUtil;
import util.Constant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.*;

/**
 * The Pdf service<br>
 *
 * @param
 * @return
 * @author Zihao Long
 */
public class PdfService {

    /**
     * The orange color
     */
    private static final Color ORANGE_COLOR = new DeviceRgb(251, 190, 8);

    /**
     * The grey color
     */
    private static final Color GREY_COLOR =  new DeviceRgb(127, 127, 127);

    /**
     * Handle student list<br>
     *
     * @param [stuList]
     * @return void
     * @author Zihao Long
     */
    public static List<Student> handleStuList(List<Student> stuList) {
        // key: studentNo, value: { key : role(Supervisor, 2nd Reader, 3rd Reader), value: student}
        Map<String, Map<String, Student>> stuMap = new HashMap<>(16);
        for (Student student : stuList) {
            String studentNo = student.getStudentNo();
            Map<String, Student> stuSubMap = stuMap.get(studentNo);
            if (stuSubMap == null) {
                stuSubMap = new HashMap<>(2);
                stuMap.put(studentNo, stuSubMap);
            }
            stuSubMap.put(student.getRole(), student);
        }

        // extract a student class from the stuMap to record the average grade and set to a student list
        List<Student> setStuList = new ArrayList<>();
        Set<String> keySet = stuMap.keySet();
        for (String key : keySet) {
            Map<String, Student> graderMap = stuMap.get(key);
            Set<String> graderSet = graderMap.keySet();
            BigDecimal totalGrade = BigDecimal.ZERO;
            int count = 0;
            Student tmpStu = null;
            for (String graderKey : graderSet) {
                tmpStu = graderMap.get(graderKey);
                totalGrade = totalGrade.add(tmpStu.getGrade());
                count++;
            }
            BigDecimal avgGrade = totalGrade.divide(new BigDecimal(count), 1, BigDecimal.ROUND_UP);
            tmpStu.setAvgGrade(avgGrade);
            tmpStu.setGraderMap(graderMap);
            setStuList.add(tmpStu);

            // sort by grade desc
            Collections.sort(setStuList, new Comparator<Student>() {
                @Override
                public int compare(Student o1, Student o2) {
                    return o2.getAvgGrade().compareTo(o1.getAvgGrade());
                }
            });
        }
        return setStuList;
    }

    /**
     * Generate Pdf by student list<br>
     *
     * @param [stuList]
     * @return void
     * @author Zihao Long
     */
    public static void generatePdf(List<Student> stuList) throws Exception {
        Constant.logger.info("Generating PDF file...");
        List<Student> handledStuList = handleStuList(stuList);

        PdfWriter writer = new PdfWriter(Constant.TARGET_FILE_PATH);
        // Creating a PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, new PdfEventHandler());

        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Document document = new Document(pdfDoc);
        document.setFont(font);

        // add student list part
        addStuListTable(handledStuList, document);

        // add grade stats part
        addGradeStats(handledStuList, document);

        // add student grade detail part
        for (Student stu : handledStuList) {
            // New page
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            addGradeDetail(stu, document);
        }
        pdfDoc.close();
    }

    /**
     * Add grade detail<br>
     *
     * @param [stu, document]
     * @return void
     * @author Zihao Long
     */
    public static void addGradeDetail(Student stu, Document document) throws Exception {
        //spacing
        paraLineBreaks(document, 1);
        //add heading
        addPara(document);
        addLineBreak(document);

        // add info table
        paraLineBreaks(document, 2);
        addInfoTable(document, stu);

        // add grade table
        paraLineBreaks(document, 2);
        addGradeTable(document, stu);
    }

    public static void addLineBreak(Document d) {
        SolidLine line = new SolidLine(1f);
        LineSeparator ls = new LineSeparator(line);
        d.add(ls);
    }

    public static void addPara(Document d) throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Paragraph para1 = new Paragraph(Constant.GRADING_REPORT_PAGE_TITLE);
        para1.setTextAlignment(TextAlignment.CENTER);
        para1.setFontSize(20)
                .setBold()
                .setFont(font).setFontColor(ORANGE_COLOR);
        d.add(para1);
    }

    public static void paraLineBreaks(Document d, Integer x) {
        for (int i = 0; i < x; ++i) {
            d.add(new Paragraph());
        }
    }

    public static void addInfoTable(Document d, Student student) {
        //two cells (student number+ name)
        Table table = new Table(UnitValue.createPercentArray(new float[]{5, 5}));
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

        // graders info
        float[] tableColumn = MyService.getGraderTableColumn(student);
        Table table3 = new Table(UnitValue.createPercentArray(tableColumn));
        infoTableStyling(table3);
        genGraderInfoByRole(table3, student, "Supervisor", "Supervisor");
        genGraderInfoByRole(table3, student, "2nd Reader", "Second Reader");
        genGraderInfoByRole(table3, student, "3rd Reader", "Third Reader");

        //one cell (Result)
        Table table4 = new Table(1);
        infoTableStyling(table4);

        Paragraph paraUpperScore = new Paragraph();
        Text textUpperScore = new Text("Grade Awarded");
        paraUpperScore.add(textUpperScore).setUnderline();

        Paragraph paraScore = new Paragraph();
        Text textScore = new Text(decimalToString(student.getAvgGrade()));
        textScore.setFontColor(ColorConstants.BLACK).setBold().setCharacterSpacing(1);
        paraScore.add(textScore);

        table4.addCell(new Cell().add(paraUpperScore.setTextAlignment(TextAlignment.LEFT)).add(paraScore));

        d.add(table);
        d.add(table2);
        d.add(table3);
        d.add(table4);
    }

    /**
     * Generate grader information by role<br>
     *
     * @param [table, student, role]
     * @return void
     * @author Zihao Long
     */
    private static void genGraderInfoByRole(Table table, Student student, String role, String titleStr) {
        Map<String, Student> graderMap = student.getGraderMap();
        Student grader = graderMap.get(role);
        if (grader == null) {
            return;
        }

        Paragraph title = new Paragraph();
        Text titleText = new Text(titleStr);
        title.add(titleText).setUnderline();

        Paragraph graderName = new Paragraph();
        Text graderNameText = new Text(grader.getGrader());
        infoTextStyling(graderNameText);
        graderName.add(graderNameText);

        table.addCell(new Cell().add(title.setTextAlignment(TextAlignment.LEFT)).add(graderName));
    }

    public static void addGradeTable(Document d, Student student) throws Exception {
        float[] graderTableColumn = MyService.getGraderTableColumn(student);
        Table table = new Table(UnitValue.createPercentArray(graderTableColumn));
        table.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(GREY_COLOR)
                .setFontColor(ORANGE_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setWidth(500)
                .setUnderline();
        addCellByRole(table, student, "Supervisor", "Supervisor");
        addCellByRole(table, student, "2nd Reader", "Second Reader");
        addCellByRole(table, student, "3rd Reader", "Third Reader");

        //4 cell modules (grades+results)
        float[] gradeTableColumn = MyService.getGradeTableColumn(student);
        Table table2 = new Table(UnitValue.createPercentArray(gradeTableColumn));
        table2.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(GREY_COLOR)
                .setFontColor(ORANGE_COLOR)
                .setTextAlignment(TextAlignment.LEFT)
                .setWidth(500)
                .setFontSize(10);

        addValToGradeTable(table2, student, "Abstract", "abstractScr", "abstractScrX");
        addValToGradeTable(table2, student, "Motivation", "motivation", "motivationX");
        addValToGradeTable(table2, student, "Background", "background", "backgroundX");
        addValToGradeTable(table2, student, "Problem", "problem", "problemX");
        addValToGradeTable(table2, student, "Solution", "solution", "solutionX");
        addValToGradeTable(table2, student, "Conclusion or Testing and Evaluation", "cte", "cteX");
        addValToGradeTable(table2, student, "Presentation", "presentation", "presentationX");

        //one cell (Score)
        Table table3 = new Table(UnitValue.createPercentArray(new float[]{10}));
        table3.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(GREY_COLOR)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setWidth(500);

        table3.addCell(new Cell().add(new Paragraph("Grade Average = " + decimalToString(student.getAvgGrade()))));
        //one cell (Title+ comments)
        Table table4 = new Table(UnitValue.createPercentArray(new float[]{10}));
        table4.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(GREY_COLOR)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.LEFT)
                .setWidth(500);

        addCommentsByRole(table4, student, "Supervisor Comments:", "Supervisor");
        addCommentsByRole(table4, student, "Second Reader Comments:", "2nd Reader");
        addCommentsByRole(table4, student, "Third Reader Comments:", "3rd Reader");

        d.add(table);
        d.add(table2);
        d.add(table3);
        d.add(table4);

    }

    /**
     * Add comments by role<br>
     *
     * @param [table, student, titleStr, role]
     * @return void
     * @author Zihao Long
     */
    private static void addCommentsByRole(Table table, Student student, String titleStr, String role) {
        Student grader = student.getGraderMap().get(role);
        if (grader == null) {
            return;
        }
        Paragraph paragraph = new Paragraph();
        Text text = new Text(titleStr);
        text.setFontColor(ORANGE_COLOR).setUnderline();
        paragraph.add(text);

        Paragraph comments = new Paragraph(grader.getComment());
        comments.setTextAlignment(TextAlignment.CENTER);
        table.addCell(new Cell().add(paragraph).add(comments));
    }

    /**
     * Add value to grade table<br><br>
     *
     * @param [table, student, title, firstFieldKey, secondFieldKey]
     * @return void
     * @author Zihao Long
     */
    private static void addValToGradeTable(Table table, Student student, String title, String firstFieldKey, String secondFieldKey) throws Exception {
        Map<String, Student> graderMap = student.getGraderMap();
        Student[] graders = new Student[]{graderMap.get("Supervisor"), graderMap.get("2nd Reader"), graderMap.get("3rd Reader")};
        for (Student grader : graders) {
            if (grader == null) {
                continue;
            }
            table.addCell(new Cell().add(new Paragraph(title)));
            BigDecimal fistVal = (BigDecimal) getValByFieldName(grader, firstFieldKey);
            BigDecimal secondVal = (BigDecimal) getValByFieldName(grader, secondFieldKey);
            table.addCell(new Cell().add(new Paragraph(decimalToString(fistVal) + "/" + decimalToString(secondVal)).setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER)));
        }
    }

    /**
     * Get value by field name<br>
     *
     * @param [student, fieldName]
     * @return java.lang.Object
     * @author Zihao Long
     */
    private static Object getValByFieldName(Student student, String fieldName) throws Exception {
        Field field = student.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(student);
    }

    /**
     * Add cell by role, if no grader with specified role, then do nothing<br>
     *
     * @param [table, student, role, titleStr]
     * @return void
     * @author Zihao Long
     */
    private static void addCellByRole(Table table, Student student, String role, String titleStr) {
        Map<String, Student> graderMap = student.getGraderMap();
        if (graderMap.get(role) == null) {
            return;
        }
        table.addCell(new Cell().add(new Paragraph(titleStr)));
    }

    public static void infoTableStyling(Table t) {
        Color muText = new DeviceRgb(157, 128, 60);
        Color GREY_COLOR = new DeviceRgb(217, 217, 217);

        t.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(GREY_COLOR)
                .setFontColor(muText)
                .setTextAlignment(TextAlignment.CENTER)
                .setWidth(500f);
    }

    public static void infoTextStyling(Text infoText) {
        infoText.setFontColor(ColorConstants.BLACK)
                .setBold()
                .setCharacterSpacing(1);
    }

    /**
     * Add grade stats<br>
     *
     * @param [stuList, mainPdf]
     * @param stuList
     * @param document
     * @return void
     * @author Zihao Long
     */
    public static void addGradeStats(List<Student> stuList, Document document) throws IOException {

        // calculate stats
        // Median
        BigDecimal median;
        int size = stuList.size();
        if (size % 2 == 1) {
            median = stuList.get((size - 1) / 2).getAvgGrade();
        } else {
            median = stuList.get(size / 2 - 1).getAvgGrade().add(stuList.get(size / 2).getAvgGrade()).divide(new BigDecimal(2));
        }

        // Average
        BigDecimal totalScore = BigDecimal.ZERO;
        for (Student stu : stuList) {
            totalScore = totalScore.add(stu.getAvgGrade());
        }
        BigDecimal average = totalScore.divide(new BigDecimal(stuList.size()), 1, BigDecimal.ROUND_UP);

        // Standard Deviation
        double standardDeviation = 0.0;
        for (Student stu : stuList) {
            standardDeviation += Math.pow(stu.getAvgGrade().subtract(average).doubleValue(), 2);
        }
        standardDeviation = Math.sqrt(standardDeviation / size);
        BigDecimal sdDecimal = new BigDecimal(standardDeviation);

        // max and min
        BigDecimal minScore = stuList.get(size - 1).getAvgGrade();
        BigDecimal maxScore = stuList.get(0).getAvgGrade();

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
            int score = stu.getAvgGrade().intValue();
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

        // set 1 scale
        median = MyService.setDecimalScale(median);
        average = MyService.setDecimalScale(average);
        sdDecimal = MyService.setDecimalScale(sdDecimal);
        maxScore = MyService.setDecimalScale(maxScore);
        minScore = MyService.setDecimalScale(minScore);

        // add statistical table
        newPageWithHeader(document);
        addStatsTable(median, average, sdDecimal, maxScore, minScore, gradeMap, stuList, document);

        // add statistical graphs
        newPageWithHeader(document);
        addStatsGraph(median, average, sdDecimal, maxScore, minScore, gradeMap, stuList, document);
    }

    /**
     * Add statistical table in text format<br>
     *
     * @param [median, average, standardDeviation, maxScore, minScore, gradeMap, stuList, document]
     * @return void
     * @author Zihao Long
     */
    private static void addStatsTable(BigDecimal median, BigDecimal average, BigDecimal standardDeviation, BigDecimal maxScore, BigDecimal minScore, Map<String, Integer> gradeMap, List<Student> stuList, Document document) throws IOException {
        // 2 columns table
        Table table = new Table(UnitValue.createPercentArray(new float[]{5, 5}));
        table.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(GREY_COLOR)
                .setFontColor(ORANGE_COLOR)
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
        addCellsTo2ColumnsTable(table, "Standard Deviation", decimalToString(standardDeviation));
        addCellsTo2ColumnsTable(table, "Max", decimalToString(maxScore));
        addCellsTo2ColumnsTable(table, "Min", decimalToString(minScore));
        addCellsTo2ColumnsTable(table, "\n", "");
        addCellsTo2ColumnsTable(table, "Count of grades:", String.valueOf(stuList.size()));
        Set<String> gradeKeySet = gradeMap.keySet();
        for (String key : gradeKeySet) {
            addCellsTo2ColumnsTable(table, key, gradeMap.get(key).toString());
        }
        document.add(table);
    }

    /**
     * Add a new page with header<br>
     *
     * @param [document]
     * @return void
     * @author Zihao Long
     */
    private static void newPageWithHeader(Document document) throws IOException {
        // New page
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        //spacing
        paraLineBreaks(document, 1);
        //add heading
        addPara(document);
        addLineBreak(document);
        paraLineBreaks(document, 2);
    }

    /**
     * Add statistical graphs<br>
     *
     * @param [median, average, standardDeviation, maxScore, minScore, gradeMap, stuList, document]
     * @return void
     * @author Zihao Long
     */
    private static void addStatsGraph(BigDecimal median, BigDecimal average, BigDecimal standardDeviation, BigDecimal maxScore, BigDecimal minScore, Map<String, Integer> gradeMap, List<Student> stuList, Document document) throws IOException {
        // add barChart for grade stats
        String gradeRowKey = "Grade";
        ByteArrayOutputStream gradeBos = new ByteArrayOutputStream();
        DefaultCategoryDataset gradeDataset = new DefaultCategoryDataset();
        gradeDataset.addValue(median, gradeRowKey,"Median");
        gradeDataset.addValue(average, gradeRowKey,"Average");
        gradeDataset.addValue(standardDeviation, gradeRowKey,"S.D.");
        gradeDataset.addValue(maxScore, gradeRowKey,"Max");
        gradeDataset.addValue(minScore, gradeRowKey,"Min");
        ChartUtils.writeChartAsJPEG(gradeBos, ChartUtil.barChart("Grade Stats", "", "", gradeDataset), 850, 430);
        Image gradeImg = new Image(ImageDataFactory.create(gradeBos.toByteArray()));
        document.add(gradeImg);

        // split
        paraLineBreaks(document, 1);

        // add lineChart for counts stats
        ByteArrayOutputStream countBos = new ByteArrayOutputStream();
        DefaultCategoryDataset categoryDataset1 = new DefaultCategoryDataset();
        Set<String> gradeKeySet = gradeMap.keySet();
        String countRowKey = "Total Counts: " + stuList.size();
        for (String key : gradeKeySet) {
            categoryDataset1.addValue(gradeMap.get(key), countRowKey, key);
        }
        ChartUtils.writeChartAsJPEG(countBos, ChartUtil.lineChart("Count Stats", "", "", categoryDataset1, true), 850, 430);
        Image countImg = new Image(ImageDataFactory.create(countBos.toByteArray()));
        document.add(countImg);
    }

    /**
     * Set bigDecimal to string by scale<br>
     *
     * @param [decimal, scale]
     * @return java.lang.String
     * @author Zihao Long
     */
    public static String decimalToString(BigDecimal decimal, int... scaleArr) {
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
    public static void addStuListTable(List<Student> stuList, Document document) throws IOException {
        // 2 columns table
        Table table = new Table(UnitValue.createPercentArray(new float[]{2.5f, 2.5f, 2.5f, 2.5f}));
        table.setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(GREY_COLOR)
                .setFontColor(ORANGE_COLOR)
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
            addStuListTableBody(table, decimalToString(student.getAvgGrade()));
        }

        //spacing
        paraLineBreaks(document, 1);
        //add heading
        addPara(document);
        addLineBreak(document);
        paraLineBreaks(document, 2);
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
