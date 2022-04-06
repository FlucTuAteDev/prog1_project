package Menu.Items;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

import Menu.Menu;
import Utils.RGB;

public class MenuItem<T> {
	public final T value;
	public final Menu<?> next;
	public final RGB foreground;
	public final RGB background;
	public final Consumer<T> action;
	public final String format;
	public final Function<T, ?>[] args;

	@SafeVarargs
	public MenuItem(T value, Menu<?> next, RGB foreground, RGB background, Consumer<T> action, String format, Function<T, ?>... args) {
		this.value = value;
		this.next = next;
		this.background = background;
		this.foreground = foreground;
		this.action = action;
		this.format = format;
		this.args = args;
	}

	@SafeVarargs
	public MenuItem(T value, Menu<?> next, RGB foreground, Consumer<T> action, String format, Function<T, ?>... args) {
		this(value, next, foreground, null, action, format, args);
	}

	@SafeVarargs
	public MenuItem(T value, Menu<?> next, Consumer<T> action, String format, Function<T, ?>... args) {
		this(value, next, null, null, action, format, args);
	}

	public Object[] evaluateArgs() {
		return Arrays.stream(this.args).map(x -> x.apply(value).toString()).toArray();
	}
}
