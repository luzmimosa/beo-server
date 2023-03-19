package com.fadedbytes.beo.api.level.world.space;


import com.fadedbytes.beo.api.exception.UnexistingDimensionException;

/**
 * Represents a vector in a BEO level
 * @param values the values of the vector
 */
public record Vector(
        Double... values
) {

    /**
     * Dimension 0
     */
    public static int X = 0;

    /**
     * Dimension 1
     */
    public static int Y = 1;

    /**
     * Dimension 2
     */
    public static int Z = 2;

    /**
     * Dimension 3
     */
    public static int W = 3;

    /**
     * @return the number of dimensions of the vector
     */
    public int dimensionCount() {
        return values.length;
    }

    /**
     * @param dimension the dimension to get the value from
     * @return the value of the vector in the specified dimension
     * @throws UnexistingDimensionException if the dimension is out of bounds
     */
    public Double getValueAt(int dimension) throws UnexistingDimensionException {
        if (isDimensionOutOfRange(dimension)) {
            throw new UnexistingDimensionException("The dimension does not exist for this vector (dimension " + dimension + " is out of range for a " + values.length + "-dimensional vector)");
        }

        return values[dimension];
    }

    /**
     * @param dimension the dimension to set the value to
     * @param value the value to set the value to
     * @return a new vector with the specified value set to the specified value
     * @throws UnexistingDimensionException if the dimension is out of bounds
     */
    public Vector withValueAt(int dimension, Double value) throws UnexistingDimensionException {
        if (isDimensionOutOfRange(dimension)) {
            throw new UnexistingDimensionException("The dimension does not exist for this vector (dimension " + dimension + " is out of range for a " + values.length + "-dimensional vector)");
        }

        Double[] newValues = new Double[values.length];
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[dimension] = value;

        return new Vector(newValues);
    }

    /**
     * Clones the vector with the specified count of dimensions.
     * If the vector has more dimensions than the specified count, the extra dimensions are discarded.
     * If the vector has fewer dimensions than the specified count, the missing dimensions are filled with 0.
     * @param dimensionCount the number of dimensions to clone the vector to
     * @return a new vector with the specified number of dimensions
     */
    public Vector withDimensionCount(int dimensionCount) {
        Double[] newValues = new Double[dimensionCount];
        System.arraycopy(values, 0, newValues, 0, Math.min(values.length, dimensionCount));

        return new Vector(newValues);
    }

    /**
     * Checks if the specified dimension is out of range for this vector
     * @param dimension the dimension to check
     * @return true if the dimension is out of range, false otherwise
     */
    private boolean isDimensionOutOfRange(int dimension) {
        return dimension < 0 || dimension >= values.length;
    }

    // operators

    public Vector add(Vector other) {
        if (dimensionCount() != other.dimensionCount()) {
            throw new IllegalArgumentException("Cannot add vectors of different dimensions");
        }

        Double[] newValues = new Double[dimensionCount()];
        for (int i = 0; i < dimensionCount(); i++) {
            newValues[i] = values[i] + other.values[i];
        }

        return new Vector(newValues);
    }

    public Vector subtract(Vector other) {
        if (dimensionCount() != other.dimensionCount()) {
            throw new IllegalArgumentException("Cannot subtract vectors of different dimensions");
        }

        Double[] newValues = new Double[dimensionCount()];
        for (int i = 0; i < dimensionCount(); i++) {
            newValues[i] = values[i] - other.values[i];
        }

        return new Vector(newValues);
    }

    public Vector multiply(Vector other) {
        if (dimensionCount() != other.dimensionCount()) {
            throw new IllegalArgumentException("Cannot multiply vectors of different dimensions");
        }

        Double[] newValues = new Double[dimensionCount()];
        for (int i = 0; i < dimensionCount(); i++) {
            newValues[i] = values[i] * other.values[i];
        }

        return new Vector(newValues);
    }

    public Vector divide(Vector other) {
        if (dimensionCount() != other.dimensionCount()) {
            throw new IllegalArgumentException("Cannot divide vectors of different dimensions");
        }

        Double[] newValues = new Double[dimensionCount()];
        for (int i = 0; i < dimensionCount(); i++) {
            newValues[i] = values[i] / other.values[i];
        }

        return new Vector(newValues);
    }

    public Vector pow(Vector other) {
        if (dimensionCount() != other.dimensionCount()) {
            throw new IllegalArgumentException("Cannot raise vectors to different powers");
        }

        Double[] newValues = new Double[dimensionCount()];
        for (int i = 0; i < dimensionCount(); i++) {
            newValues[i] = Math.pow(values[i], other.values[i]);
        }

        return new Vector(newValues);
    }

    public double length() {
        double sum = 0;
        for (Double value : values) {
            sum += Math.pow(value, 2);
        }

        return Math.sqrt(sum);
    }

    public Vector normalize() {
        double length = length();
        Double[] newValues = new Double[dimensionCount()];
        for (int i = 0; i < dimensionCount(); i++) {
            newValues[i] = values[i] / length;
        }

        return new Vector(newValues);
    }

    public double dot(Vector other) {
        if (dimensionCount() != other.dimensionCount()) {
            throw new IllegalArgumentException("Cannot dot vectors of different dimensions");
        }

        double sum = 0;
        for (int i = 0; i < dimensionCount(); i++) {
            sum += values[i] * other.values[i];
        }

        return sum;
    }



    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector vector) {
            if (vector.dimensionCount() != dimensionCount()) {
                return false;
            }

            for (int i = 0; i < dimensionCount(); i++) {
                try {
                    if (!vector.getValueAt(i).equals(getValueAt(i))) {
                        return false;
                    }
                } catch (UnexistingDimensionException e) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}