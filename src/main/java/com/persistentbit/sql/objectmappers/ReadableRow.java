package com.persistentbit.sql.objectmappers;

/**
 * Represent a readable database table row.<br>
 * @see WritableRow
 */
@FunctionalInterface
public interface ReadableRow {

    /**
     * Get value of the Row column with the provided name
     * @param cls The expected type of the value
     * @param name The case insensitive column name.
     * @return The value of the column or null if not existing
     */
    <T> T read(Class<T> cls,String name);




    static <T> T check(Class<T> cls, String name, T result){

        if(result == null || cls == null || cls.isAssignableFrom(result.getClass())){
            return result;
        }
        if(cls.isPrimitive()){
            if(cls == int.class && result.getClass() == Integer.class){
                return result;
            }
            if(cls == short.class && result.getClass() == Short.class){
                return result;
            }
            if(cls == long.class && result.getClass() == Long.class){
                return result;
            }
            if(cls == float.class && result.getClass() == Float.class){
                return result;
            }
            if(cls == double.class && result.getClass() == Double.class){
                return result;
            }
            if(cls == boolean.class && result.getClass() == Boolean.class){
                return result;
            }
        }
        throw new RuntimeException("Expected " + cls.getName() + ", got " + result.getClass() + " for property '" + name + "' with value " + result);
    }
    static <T> T checkAndConvert(Class<T> cls,String name, T result){
        if(result == null || cls == null || result.getClass().equals(cls)){
            return result;
        }
        if(cls.equals(Integer.class) || cls.equals(int.class)){
            Number num = (Number)result;
            return (T)Integer.valueOf(num.intValue());
        }
        if(cls.equals(long.class) || cls.equals(Long.class)){
            Number num = (Number)result;
            return (T)Long.valueOf(num.longValue());
        }
        if(cls.equals(Short.class) || cls.equals(short.class)){
            Number num = (Number)result;
            return (T)Short.valueOf(num.shortValue());
        }
        if(cls.equals(Float.class) || cls.equals(float.class)){
            Number num = (Number)result;
            return (T)Float.valueOf(num.floatValue());
        }
        if(cls.equals(Double.class) || cls.equals(double.class)){
            Number num = (Number)result;
            return (T)Double.valueOf(num.doubleValue());
        }
        throw new RuntimeException("Expected " + cls.getName() + ", got " + result.getClass() + " for property '" + name + "' with value " + result);
    }

}

