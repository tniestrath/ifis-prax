package com.analysetool.modells;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
//import org.springframework.data.annotation.Id;


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
    @Column(name = "date")
    private LocalDateTime date;

    public LocalDateTime getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDateTime logDate) {
        this.logDate = logDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SysVar sysVar)) return false;
        return getId() == sysVar.getId() && getDayInWeek() == sysVar.getDayInWeek() && getDayInMonth() == sysVar.getDayInMonth() && getDayInYear() == sysVar.getDayInYear() && getLastLineCount() == sysVar.getLastLineCount() && Objects.equals(getLastLine(), sysVar.getLastLine()) && Objects.equals(getDate(), sysVar.getDate()) && Objects.equals(getLogDate(), sysVar.getLogDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDayInWeek(), getDayInMonth(), getDayInYear(), getLastLineCount(), getLastLine(), getDate(), getLogDate());
    }

    @Column(name="log_date")
    private LocalDateTime logDate;

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "SysVar{" +
                "id=" + id +
                ", dayInWeek=" + dayInWeek +
                ", dayInMonth=" + dayInMonth +
                ", dayInYear=" + dayInYear +
                ", lastLineCount=" + lastLineCount +
                ", lastLine='" + lastLine + '\'' +
                ", date=" + date +
                ", logDate=" + logDate +
                '}';
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
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
