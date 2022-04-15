package Utils.Functions;

/**
 * Defines a {@link java.util.function.Function Function} that can throw an exception
 */
@FunctionalInterface
public interface ConverterFunction<T, R>{
	R apply(T arg) throws Exception;
}