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
				v -> {
					v.heal(this);
				}, "%s", v -> v.icon));
		}

		Tile selectedTile = menu.display().getTile();
		this.effect(selectedTile);
		try {
			Thread.sleep(Spell.EFFECT_TIME);
		} catch (Exception e) { System.exit(1); }
		selectedTile.draw();
	}
}
