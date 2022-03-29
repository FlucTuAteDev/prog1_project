package Menu;
import java.util.ArrayList;
import java.util.List;

import Base.Console;
import Base.Console.MoveDirection;
import Utils.IO;

public class Menu {
	private List<MenuItem> items;
	private List<HeaderItem> headers;
	private String name;

	public Menu(String name) { 
		this.name = name;
		this.items = new ArrayList<MenuItem>();
		this.headers = new ArrayList<HeaderItem>();
	}

	public void addItem(MenuItem item) {
		items.add(item);
	}

	public void addHeader(HeaderItem item) {
		this.headers.add(item);
	}
	
	/**
	 * Calculates where put the cursor so that the text is in the middle assuming
	 * the set console width
	 * @param s
	 * @return Where to put the cursor
	 */
	private int alignCenter(String s) {
		return Console.WIDTH / 2 - s.length() / 2;
	}

	private int alignRight(String s) {
		return Console.WIDTH - s.length();
	}

	public void display() {
		Console.clearScreen();

		// Menu header
		Console.setBold();
		Console.println("-".repeat(Console.WIDTH));

		Console.setCursorCol(alignCenter(this.name));
		Console.println(this.name);

		Console.setNormal();
		if (headers.size() > 0) Console.moveCursor(MoveDirection.UP, 1);
		for (HeaderItem header : headers) {
			Console.setForeground(header.foreground);
			String formattedText = String.format(header.text, header.textArgs);
			Console.setCursorCol(alignRight(formattedText));
			Console.println(formattedText);
		}
		Console.resetColors();
		Console.setBold();
		Console.println("-".repeat(Console.WIDTH));
		
		Console.setNormal();
		// Display items with indices
		int i = 0;
		for ( var item : this.items ) {
			String formattedText = String.format(item.text, item.textArgs);
			String text = String.format("%d - %s", i + 1, formattedText);
			// Console.setBackground(item.background);
			Console.setForeground(item.foreground);
			Console.setCursorCol(alignCenter(text));
			Console.println(text);
			i++;
		}
		Console.resetColors();
		Console.println("");

		int selected = IO.scanInt("Válasszon", 1, this.items.size()) - 1;
		MenuItem selectedItem = this.items.get(selected);
		selectedItem.action.run();
		if (selectedItem.redrawSelf) this.display();
	}
}
