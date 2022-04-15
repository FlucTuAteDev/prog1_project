package Spells;

import Base.Game;
import Hero.Hero;
import Menu.*;
import Menu.Items.MenuItem;
import Units.Unit;
import View.Colors.Colors;

public class Resurrection extends Spell {
	public Resurrection(Hero hero) {
		super("Feltámasztás", "💉", Colors.HEAL, 120, 6, 50, hero);
	}

	@Override
	public void cast() {
		if (!this.hero.useManna(this.manna))
			return;

		Menu<Unit> menu = new BasicMenu<>("Feltámasztható egységek: ", Game.menuView);
		for (Unit unit : this.hero.getUnits()) {
			menu.addItem(new MenuItem<>(unit, null,
				Colors.textFromBg(this.hero.COLOR), 
				this.hero.COLOR,
				v -> {}, "%s", v -> v.icon));
		}

		Unit selected = menu.display();
		int heal = (int)Math.round(this.getValue());

		int prevCount = selected.getCount();
		selected.heal(heal);
		selected.getTile().effect(Colors.GREEN, this.icon);

		Game.logMessage("%s %s %s: +%d❤ -> +%ddb", 
			hero, this.icon, selected,
			heal, selected.getCount() - prevCount);
	}
}
