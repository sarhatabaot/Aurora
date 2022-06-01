package com.zenya.aurora.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.concurrent.ThreadLocalRandom;

public class RandomNumber<T extends Number & Comparable<T>> {

    private T lowerBound;
    private T upperBound;

    public RandomNumber(T num) {
        this(num, num);
    }

    public RandomNumber(T num1, T num2) {
        lowerBound = num1.compareTo(num2) < 0 ? num1 : num2;
        upperBound = num1.compareTo(num2) > 0 ? num1 : num2;
    }

    public RandomNumber<T> withLowerBound(T lowerBound) {
        this.lowerBound = lowerBound;
        return this;
    }

    public RandomNumber<T> withUpperBound(T upperBound) {
        this.upperBound = upperBound;
        return this;
    }

    public RandomNumber<T> withBounds(T lowerBound, T upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        return this;
    }

    public Long generateLong() {
        if (lowerBound.equals(upperBound)) {
            return lowerBound.longValue();
        }
        return ThreadLocalRandom.current().nextLong(lowerBound.longValue(), upperBound.longValue() + 1);
    }

    public Double generateDouble() {
        if (lowerBound.equals(upperBound)) {
            return lowerBound.doubleValue();
        }
        return ThreadLocalRandom.current().nextDouble(lowerBound.doubleValue(), upperBound.doubleValue() + Double.MIN_VALUE);
    }

    public Integer generateInt() {
        return generateLong().intValue();
    }

    public Float generateFloat() {
        return generateDouble().floatValue();
    }

    public static class RandomNumberDeserializer implements JsonDeserializer<RandomNumber> {

        @Override
        public RandomNumber deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                if (jsonElement.isJsonArray()) {
                    //[num, ...]
                    JsonArray jsonArray = jsonElement.getAsJsonArray();
                    if (jsonArray.size() == 2) {
                        //[min, max]
                        if (jsonArray.get(0).getAsDouble() != jsonArray.get(0).getAsLong() || jsonArray.get(1).getAsDouble() != jsonArray.get(1).getAsLong()) {
                            //At least 1 double
                            return new RandomNumber(jsonArray.get(0).getAsDouble(), jsonArray.get(1).getAsDouble());
                        } else {
                            //All long
                            return new RandomNumber(jsonArray.get(0).getAsLong(),jsonArray.get(1).getAsLong());
                        }
                    } else {
                        //[value]
                        if (jsonArray.get(0).getAsDouble() != jsonArray.get(0).getAsLong()) {
                            //double
                            return new RandomNumber(jsonArray.get(0).getAsDouble());
                        } else {
                            //long
                            return new RandomNumber(jsonArray.get(0).getAsLong());
                        }
                    }
                } else {
                    //num
                    if (jsonElement.getAsDouble() != jsonElement.getAsLong()) {
                        //double
                        return new RandomNumber(jsonElement.getAsDouble());
                    } else {
                        //long
                        return new RandomNumber(jsonElement.getAsLong());
                    }
                }
            } catch (Exception exc) {
                exc.printStackTrace();
                return new RandomNumber(0);
            }
        }
    }

    public T getLowerBound() {
        return lowerBound;
    }

    public T getUpperBound() {
        return upperBound;
    }

}
