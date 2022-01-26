package pojo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * The Student class<br>
 *
 * @param
 * @return
 * @author Zihao Long
 */
public class Student {

    private String grader = "";

    private String projectNo = "";

    private String role = "";

    private String otherGrader = "";

    private String studentName = "";

    private String studentNo = "";

    private String projectType = "";

    private BigDecimal credit;

    private BigDecimal grade;

    private BigDecimal avgGrade;

    private BigDecimal abstractScr;
    private BigDecimal abstractScrX;

    private BigDecimal motivation;
    private BigDecimal motivationX;

    private BigDecimal background;
    private BigDecimal backgroundX;

    private BigDecimal problem;
    private BigDecimal problemX;

    private BigDecimal solution;
    private BigDecimal solutionX;

    private BigDecimal cte;
    private BigDecimal cteX;

    private BigDecimal presentation;
    private BigDecimal presentationX;

    private String comment = "";

    private String title = "";

    private Map<String, Student> graderMap;

    public BigDecimal getAvgGrade() {
        return avgGrade;
    }

    public void setAvgGrade(BigDecimal avgGrade) {
        this.avgGrade = avgGrade;
    }


    public Map<String, Student> getGraderMap() {
        return graderMap;
    }

    public void setGraderMap(Map<String, Student> graderMap) {
        this.graderMap = graderMap;
    }

    public String getGrader() {
        return grader;
    }

    public void setGrader(String grader) {
        this.grader = grader;
    }

    public String getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOtherGrader() {
        return otherGrader;
    }

    public void setOtherGrader(String otherGrader) {
        this.otherGrader = otherGrader;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }

    public BigDecimal getAbstractScr() {
        return abstractScr;
    }

    public void setAbstractScr(BigDecimal abstractScr) {
        this.abstractScr = abstractScr;
    }

    public BigDecimal getAbstractScrX() {
        return abstractScrX;
    }

    public void setAbstractScrX(BigDecimal abstractScrX) {
        this.abstractScrX = abstractScrX;
    }

    public BigDecimal getMotivation() {
        return motivation;
    }

    public void setMotivation(BigDecimal motivation) {
        this.motivation = motivation;
    }

    public BigDecimal getMotivationX() {
        return motivationX;
    }

    public void setMotivationX(BigDecimal motivationX) {
        this.motivationX = motivationX;
    }

    public BigDecimal getBackground() {
        return background;
    }

    public void setBackground(BigDecimal background) {
        this.background = background;
    }

    public BigDecimal getBackgroundX() {
        return backgroundX;
    }

    public void setBackgroundX(BigDecimal backgroundX) {
        this.backgroundX = backgroundX;
    }

    public BigDecimal getProblem() {
        return problem;
    }

    public void setProblem(BigDecimal problem) {
        this.problem = problem;
    }

    public BigDecimal getProblemX() {
        return problemX;
    }

    public void setProblemX(BigDecimal problemX) {
        this.problemX = problemX;
    }

    public BigDecimal getSolution() {
        return solution;
    }

    public void setSolution(BigDecimal solution) {
        this.solution = solution;
    }

    public BigDecimal getSolutionX() {
        return solutionX;
    }

    public void setSolutionX(BigDecimal solutionX) {
        this.solutionX = solutionX;
    }

    public BigDecimal getCte() {
        return cte;
    }

    public void setCte(BigDecimal cte) {
        this.cte = cte;
    }

    public BigDecimal getCteX() {
        return cteX;
    }

    public void setCteX(BigDecimal cteX) {
        this.cteX = cteX;
    }

    public BigDecimal getPresentation() {
        return presentation;
    }

    public void setPresentation(BigDecimal presentation) {
        this.presentation = presentation;
    }

    public BigDecimal getPresentationX() {
        return presentationX;
    }

    public void setPresentationX(BigDecimal presentationX) {
        this.presentationX = presentationX;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
