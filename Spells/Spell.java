package Spells;

public abstract class Spell {
	private String name;
	private int price;
	private int manna;
	private boolean active = false;

	public Spell(String name, int price, int manna) {
		this.name = name;
		this.price = price;
		this.manna = manna;
	}

	public String getName() { return this.name; }
	public int getPrice() { return this.price; }
	public int getManna() { return this.manna; }
	public boolean isActive() { return this.active; }
	public void setActive() { this.active = true; }
}
