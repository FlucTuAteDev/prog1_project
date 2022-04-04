package Menu;

import java.util.ArrayList;
import java.util.List;

import Menu.Items.HeaderItem;
import Menu.Items.MenuItem;

public abstract class Menu {
	protected List<MenuItem> items;
	protected List<HeaderItem> headers;
	protected String name;

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

	public void clearItems() {
		this.items.clear();
	}

	/**
	 * Calculates where put the cursor so that the text is in the middle assuming
	 * the set console width
	 * 
	 * @param s
	 * @return Where to put the cursor
	 */
	
	public abstract void display();
}
