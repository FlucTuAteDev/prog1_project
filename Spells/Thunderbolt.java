package Spells;

import Base.Game;
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
	public void cast() {
		if (!this.hero.useManna(this.manna))
			return;

		Menu<Unit> menu = new BasicMenu<>("Támadható egységek: ", Game.menuView);
		Hero enemy = this.hero.getEnemy();
		for (Unit unit : enemy.getAliveUnits()) {
			menu.addItem(new MenuItem<>(unit, null,
				Colors.textFromBg(enemy.COLOR), 
				enemy.COLOR,
				v -> {}, "%s", v -> v.icon));
		}

		Unit selectedUnit = menu.display();
		int damage = (int)Math.round(this.getValue());

		Hero hero = this.hero;
		Game.logMessage("%s %s %s: -%d❤ -> -%ddb", 
			hero, this.icon, selectedUnit,
			damage, damage / selectedUnit.baseHealth);

		selectedUnit.getTile().effect(Colors.RED, this.icon);
		selectedUnit.takeDamage(damage);
	}
}
