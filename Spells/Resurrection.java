package Spells;

import Base.Game;
import Hero.Hero;
import Menu.*;
import Menu.Items.MenuItem;
import Units.Unit;
import Utils.Colors;

public class Resurrection extends Spell {
	public Resurrection(Hero hero) {
		super("Feltámasztás", 120, 6, 50, hero);
	}

	@Override
	public void cast() {
		Menu menu = new BasicMenu("Feltámasztható egységek: ");
		for (Unit unit : this.hero.getUnits()) {
			menu.addItem(new MenuItem(
				Colors.textFromBg(this.hero.COLOR), 
				this.hero.COLOR,
				() -> {
					double heal = this.hero.getSkill("magic").getValue() * this.multiplier;
					unit.heal(heal);
					Game.board.redrawUnit(unit);
				}, false, String.format("%s", unit.icon)));
		}

		menu.display();
	}
}
