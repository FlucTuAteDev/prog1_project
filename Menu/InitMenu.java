package Menu;
import Base.Console;
import Base.Console.*;
import Menu.Items.HeaderItem;
import Menu.Items.MenuItem;
import Utils.IO;
import Utils.Functions.*;

public class InitMenu extends Menu {
	public InitMenu(String name) {
		super(name);
	}

	public void display() {
		Console.clearScreen();
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
			String formattedText = String.format(item.text, item.evaluateArgs());
			Console.setForeground(item.foreground);
			Console.printlnAligned(Alignment.CENTER, Console.WIDTH, "%d - %s", i + 1, formattedText);
			i++;
		}
		Console.resetStyles();
		Console.println("");

		int selected = (int) IO.scanAndConvert(String.format("Válasszon [%d - %d]", 1, this.items.size()),
				Converters.convertInt(1, this.items.size() + 1)).get(0) - 1;

		MenuItem selectedItem = this.items.get(selected);
		selectedItem.action.run();
		if (selectedItem.redrawSelf)
			this.display();
	}
}
