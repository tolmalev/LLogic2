package ru.llogic.core;

/**
 * @author tolmalev
 */
public class Point {
    public final long id;

    public Point(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Point point = (Point) o;

        return id == point.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                '}';
    }
}
