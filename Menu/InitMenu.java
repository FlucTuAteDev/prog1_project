package Menu;
import Base.Console;
import Base.Console.*;
import Menu.Items.HeaderItem;
import Menu.Items.MenuItem;
import View.View;

public class InitMenu<T> extends Menu<T> {

	public InitMenu(String name, View view) {
		super(name, view);
	}

	public T display() {
		view.clear();
		// Menu header
		Console.println("-".repeat(Console.WIDTH));

		// Console.setCursorCol(alignCenter(this.name));
		Console.printlnAligned(Alignment.CENTER, Console.WIDTH, this.name);

		// If there are no elements in the list the cursor would stay next to the menu's
		// name
		if (headers.size() > 0)
			Console.moveCursor(MoveDirection.UP, 1);

		for (HeaderItem header : headers) {
			Console.setForeground(header.foreground);

			String formattedText = String.format(header.text, header.evaluateArgs());

			Console.setCursorCol(Console.WIDTH - formattedText.length());
			Console.println(formattedText);
		}

		Console.resetStyles();
		Console.println("-".repeat(Console.WIDTH));

		// Display items with indices
		int i = 0;
		for (var item : this.items) {
			// Run the item's functions
			String formattedText = String.format(item.format, item.evaluateArgs());
			Console.setBackground(item.background);
			Console.setForeground(item.foreground);
			Console.printlnAligned(Alignment.CENTER, Console.WIDTH, "%d - %s", i + 1, formattedText);
			i++;
		}
		Console.resetStyles();
		Console.println("");

		int selected = Console.scanInt("Válasszon", 1, this.items.size()) - 1;

		MenuItem<T> selectedItem = this.items.get(selected);
		selectedItem.action.accept(selectedItem.value);
 
		if (selectedItem.next != null)
			selectedItem.next.display();

		return selectedItem.value;
	}
}
