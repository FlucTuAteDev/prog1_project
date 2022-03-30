package Utils.Functions;

@FunctionalInterface
public interface ConverterFunction<T, R> {
	R apply(T arg) throws Exception;
}