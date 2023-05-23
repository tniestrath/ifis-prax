package com.analysetool.modells;
import jakarta.persistence.*;
import org.springframework.data.annotation.Id;


@Entity
@Table(name = "sys_var")
public class SysVar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "dayinweek")
    private int dayInWeek;

    @Column(name = "dayinmonth")
    private int dayInMonth;

    @Column(name = "dayinyear")
    private int dayInYear;

    @Column(name = "lastlinecount")
    private int lastLineCount;

    @Column(name = "lastline", length = 500)
    private String lastLine;

    // Konstruktor, Getter und Setter

    public SysVar() {
    }

    public SysVar(int dayInWeek, int dayInMonth, int dayInYear, int lastLineCount, String lastLine) {
        this.dayInWeek = dayInWeek;
        this.dayInMonth = dayInMonth;
        this.dayInYear = dayInYear;
        this.lastLineCount = lastLineCount;
        this.lastLine = lastLine;
    }

    // Getter und Setter für die Attribute

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDayInWeek() {
        return dayInWeek;
    }

    public void setDayInWeek(int dayInWeek) {
        this.dayInWeek = dayInWeek;
    }

    public int getDayInMonth() {
        return dayInMonth;
    }

    public void setDayInMonth(int dayInMonth) {
        this.dayInMonth = dayInMonth;
    }

    public int getDayInYear() {
        return dayInYear;
    }

    public void setDayInYear(int dayInYear) {
        this.dayInYear = dayInYear;
    }

    public int getLastLineCount() {
        return lastLineCount;
    }

    public void setLastLineCount(int lastLineCount) {
        this.lastLineCount = lastLineCount;
    }

    public String getLastLine() {
        return lastLine;
    }

    public void setLastLine(String lastLine) {
        this.lastLine = lastLine;
    }
}
