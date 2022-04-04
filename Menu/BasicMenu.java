package Menu;
import Base.Console;
import Menu.Items.MenuItem;
import Utils.IO;
import Utils.Functions.*;

public class BasicMenu extends Menu {

	public BasicMenu(String name) {
		super(name);
	}
	
	@Override
	public void display() {
		Console.println(this.name);

		// Display items with indices
		int i = 0;
		for (var item : this.items) {
			// Run the item's functions
			String formattedText = String.format(item.text, item.evaluateArgs());

			Console.print("\t%d) ", i + 1);
			Console.setBackground(item.background);
			Console.setForeground(item.foreground);
			Console.print(formattedText);
			Console.resetStyles();
			Console.println("");
			i++;
		}

		int selected = (int) IO.scanAndConvert(String.format("Válasszon [%d - %d]", 1, this.items.size()),
				Converters.convertInt(1, this.items.size() + 1)).get(0) - 1;

		MenuItem selectedItem = this.items.get(selected);
		selectedItem.action.run();
		if (selectedItem.redrawSelf)
			this.display();
	}
}
