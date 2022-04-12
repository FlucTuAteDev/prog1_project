package Spells;

import Base.Game;
import Board.Tile;
import Hero.Hero;
import Menu.*;
import Menu.Items.MenuItem;
import Units.Unit;
import Utils.ThreadHelper;
import View.Colors.Colors;

public class Resurrection extends Spell {
	public Resurrection(Hero hero) {
		super("Feltámasztás", "💉", Colors.HEAL, 120, 6, 50, hero);
	}

	@Override
	public void use(Unit unit) {
		int heal = (int)Math.round(this.getValue());

		int prevCount = unit.getCount();
		unit.heal(heal);
		Game.logMessage("%s %s %s: +%d❤ -> +%ddb", 
			hero, this.icon, unit,
			heal, unit.getCount() - prevCount);
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
				v -> this.use(v), "%s", v -> v.icon));
		}

		Tile selectedTile = menu.display().getTile();
		this.effect(selectedTile);
		ThreadHelper.sleep(Spell.EFFECT_TIME);
		selectedTile.draw();
	}
}
