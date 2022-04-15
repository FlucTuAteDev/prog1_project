package Menu.Items;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

import Menu.Menu;
import View.Colors.RGB;

/**
 * Stores the attributes of a menu's item.
 */
public class MenuItem<T> {
	public final T value;
	public final Menu<?> next;
	public final RGB foreground;
	public final RGB background;
	public final Consumer<T> action;
	public final String format;
	public final Function<T, ?>[] args;

	/**
	 * @param value The value to be returned from {@link Menu#display()} when selecting this item
	 * @param next The menu to open when selecting this item (can be {@code null})
	 * @param foreground The color of the text
	 * @param background
	 * @param action Function to be run when selecting this item
	 * @param format
	 * @param args
	 */
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

	/**
	 * {@link #MenuItem(Object, Menu, RGB, RGB, Consumer, String, Function...)}
	 */
	@SafeVarargs
	public MenuItem(T value, Menu<?> next, RGB foreground, Consumer<T> action, String format, Function<T, ?>... args) {
		this(value, next, foreground, null, action, format, args);
	}

	/**
	 * {@link #MenuItem(Object, Menu, RGB, RGB, Consumer, String, Function...)}
	 */
	@SafeVarargs
	public MenuItem(T value, Menu<?> next, Consumer<T> action, String format, Function<T, ?>... args) {
		this(value, next, null, null, action, format, args);
	}

	/**
	 * Evaluates all the argument functions and returns them as an Object[]
	 */
	public Object[] evaluateArgs() {
		return Arrays.stream(this.args).map(x -> x.apply(value).toString()).toArray();
	}
}
