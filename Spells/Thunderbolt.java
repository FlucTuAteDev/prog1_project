package Spells;

import Base.Game;
import Board.Tile;
import Hero.Hero;
import Units.Unit;
import View.Colors.Colors;
import Menu.*;
import Menu.Items.MenuItem;

public class Thunderbolt extends Spell {
	public Thunderbolt(Hero hero) {
		super("Villámcsapás", "🌩", Colors.DAMAGE, 60, 5, 30, hero);
	}
	
	@Override
	public Tile generate() {
		return this.hero.getEnemy().getLowestHpUnit().getTile();
	}

	@Override
	public Tile scan() {
		Menu<Unit> menu = new BasicMenu<>("Támadható egységek: ", Game.menuView);
		Hero enemy = this.hero.getEnemy();
		for (Unit unit : enemy.getAliveUnits()) {
			menu.addItem(new MenuItem<>(unit, null,
				Colors.textFromBg(enemy.COLOR), 
				enemy.COLOR,
				v -> {}, "%s", v -> v.icon));
		}

		return menu.display().getTile();
	}

	@Override
	public void cast(Tile tile) {
		if (!this.hero.useManna(this.manna))
			return;

		Unit unit = tile.getUnit();
		int damage = (int)Math.round(this.getValue());

		Hero hero = this.hero;
		Game.logMessage("%s %s %s: -%d❤ -> -%ddb", 
			hero, this.icon, unit,
			damage, damage / unit.baseHealth);

		unit.getTile().effect(Colors.RED, this.icon);
		unit.takeDamage(damage);
	}
}
