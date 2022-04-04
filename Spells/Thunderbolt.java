package Spells;

import Base.Game;
import Hero.Hero;
import Units.Unit;
import Utils.Colors;

import Menu.*;
import Menu.Items.MenuItem;

public class Thunderbolt extends Spell {
	public Thunderbolt(Hero hero) {
		super("Villámcsapás", 60, 5, 30, hero);
	}

	@Override
	public void cast() {
		Menu menu = new BasicMenu("Támadható egységek: ");
		Hero enemy = this.hero == Game.player ? Game.ai : Game.player;
		for (Unit unit : enemy.getUnits()) {
			menu.addItem(new MenuItem(
				Colors.textFromBg(enemy.COLOR), 
				enemy.COLOR,
				() -> {
					double damage = this.hero.getSkill("magic").getValue() * this.multiplier;
					unit.takeDamage(damage);
					Game.board.redrawUnit(unit);
				}, false, String.format("%s", unit.icon)));
		}

		menu.display();
	}
}
