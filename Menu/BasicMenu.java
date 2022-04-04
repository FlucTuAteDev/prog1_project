package Menu;
import Base.Console;
import Base.Console.*;
import Utils.IO;
import Utils.Functions.*;

public class BasicMenu extends Menu {

	public BasicMenu(String name) {
		super(name);
	}
	
	@Override
	public void display(int row) {
		Console.setCursorPosition(row, 0);
		Console.clearBelow();
		Console.resetStyles();
		// Console.clearScreen();

		// Menu header
		// Console.println("-".repeat(Console.WIDTH));

		// Console.setCursorCol(alignCenter(this.name));
		// Console.printlnAligned(Alignment.CENTER, Console.WIDTH, this.name);
		Console.println(this.name);

		// If there are no elements in the list the cursor would stay next to the menu's
		// name
		// if (headers.size() > 0)
		// 	Console.moveCursor(MoveDirection.UP, 1);

		// for (HeaderItem header : headers) {
		// 	Console.setForeground(header.foreground);

		// 	String formattedText = String.format(header.text, header.evaluateArgs());

		// 	Console.setCursorCol(Console.WIDTH - formattedText.length());
		// 	Console.println(formattedText);
		// }

		// Console.resetStyles();
		// Console.println("-".repeat(Console.WIDTH));
		
		// Display items with indices
		int i = 0;
		for (var item : this.items) {
			// Run the item's functions
			String formattedText = String.format(item.text, item.evaluateArgs());

			Console.print("\t");
			Console.setBackground(item.background);
			Console.setForeground(item.foreground);
			Console.println("%d) %s", i + 1, formattedText);
			Console.resetStyles();
			i++;
		}

		int selected = (int) IO.scanAndConvert(String.format("Válasszon [%d - %d]", 1, this.items.size()),
				Converters.convertInt(1, this.items.size() + 1)).get(0) - 1;

		MenuItem selectedItem = this.items.get(selected);
		selectedItem.action.run();
		if (selectedItem.redrawSelf)
			this.display(row);
	}

	public void display() {
		display(0);
	}
}
