package Menu;
import Base.Console;
import Menu.Items.MenuItem;
import View.View;

public class BasicMenu<T> extends Menu<T> {
	public BasicMenu(String name, View view) {
		super(name, view);
	}

	@Override
	public T display() {
		view.clear();
		Console.println(this.name);

		// Display items with indices
		int i = 0;
		for (var item : this.items) {
			// Run the item's functions
			String formattedText = String.format(item.format, item.evaluateArgs());

			Console.print("\t%d) ", i + 1);
			Console.setBackground(item.background);
			Console.setForeground(item.foreground);
			Console.print(formattedText);
			Console.resetStyles();
			Console.println("");
			i++;
		}

		int selected = Console.scanInt("Válasszon", 1, this.items.size()) - 1;

		MenuItem<T> selectedItem = this.items.get(selected);
		selectedItem.action.accept(selectedItem.value);
		
		if (selectedItem.next != null)
			selectedItem.next.display();

		return selectedItem.value;
	}
}
