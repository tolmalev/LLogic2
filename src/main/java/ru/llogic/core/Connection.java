package ru.llogic.core;

/**
 * @author tolmalev
 */
public class Connection {
    private final Point from;
    private final Point to;

    Connection(Point from, Point to) {
        if (from == null || to == null) {
            throw new NullPointerException("Points can't be null");
        }
        if (from.equals(to)) {
            throw new IllegalArgumentException("Can't connect point to itself");
        }
        this.from = from.id < to.id ? from : to;
        this.to = from.id < to.id ? to : from;
    }

    public Point getFrom() {
        return from;
    }

    public Point getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Connection that = (Connection) o;

        if (from != null ? !from.equals(that.from) : that.from != null) {
            return false;
        }
        return to != null ? to.equals(that.to) : that.to == null;
    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }
}
