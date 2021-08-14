package me.plytki.supervision.utils;

import me.plytki.supervision.objects.Report;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class Compare implements Comparator<Report> {

    @Override
    public int compare(Report o1, Report o2) {
        return Integer.compare(o1.getReportID(), o2.getReportID());
    }

    @Override
    public Comparator<Report> reversed() {
        return null;
    }

    @Override
    public Comparator<Report> thenComparing(Comparator<? super Report> other) {
        return null;
    }

    @Override
    public <U> Comparator<Report> thenComparing(Function<? super Report, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        return null;
    }

    @Override
    public <U extends Comparable<? super U>> Comparator<Report> thenComparing(Function<? super Report, ? extends U> keyExtractor) {
        return null;
    }

    @Override
    public Comparator<Report> thenComparingInt(ToIntFunction<? super Report> keyExtractor) {
        return null;
    }

    @Override
    public Comparator<Report> thenComparingLong(ToLongFunction<? super Report> keyExtractor) {
        return null;
    }

    @Override
    public Comparator<Report> thenComparingDouble(ToDoubleFunction<? super Report> keyExtractor) {
        return null;
    }

}
