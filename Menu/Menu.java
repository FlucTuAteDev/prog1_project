package Menu;

import java.util.ArrayList;
import java.util.List;

import Menu.Items.HeaderItem;
import Menu.Items.MenuItem;
import View.View;

/**
 * Handles the displaying of the menu and the selection of the menuitems
 */
public abstract class Menu<T> {
	protected List<MenuItem<T>> items;
	protected List<HeaderItem> headers;
	protected String name;
	protected View view;

	public Menu(String name, View view) {
		this.items = new ArrayList<MenuItem<T>>();
		this.headers = new ArrayList<HeaderItem>();
		
		this.name = name;
		this.view = view;
	}

	public void addItem(MenuItem<T> item) {
		items.add(item);
	}

	public void addHeader(HeaderItem item) {
		this.headers.add(item);
	}

	public void clearItems() {
		this.items.clear();
	}

	public abstract T display();
}
