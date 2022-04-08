package Spells;

import Base.Game;
import Board.Tile;
import Hero.Hero;
import Menu.*;
import Menu.Items.MenuItem;
import Units.Unit;
import View.Colors.Colors;

public class Resurrection extends Spell {
	public Resurrection(Hero hero) {
		super("Feltámasztás", "💉", 120, 6, 50, hero);
	}

	@Override
	public void cast() {
		Menu<Unit> menu = new BasicMenu<>("Feltámasztható egységek: ", Game.menuView);
		for (Unit unit : this.hero.getUnits()) {
			menu.addItem(new MenuItem<>(unit, null,
				Colors.textFromBg(this.hero.COLOR), 
				this.hero.COLOR,
				v -> {
					double heal = v.hero.getSkill("magic").getValue() * this.multiplier;
					v.heal(heal);
				}, "%s", v -> v.icon));
		}

		menu.display();
		this.hero.useManna(this.manna);
	}

	@Override
	public void effect(Tile tile) {
		
	}
}
