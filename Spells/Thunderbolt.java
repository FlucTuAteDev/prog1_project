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
		Menu<Unit> menu = new BasicMenu<>("Támadható egységek: ", Game.menuView);
		Hero enemy = this.hero == Game.player ? Game.ai : Game.player;
		for (Unit unit : enemy.getUnits()) {
			menu.addItem(new MenuItem<>(unit, null,
				Colors.textFromBg(enemy.COLOR), 
				enemy.COLOR,
				v -> {
					double damage = v.hero.getSkill("magic").getValue() * this.multiplier;
					v.takeDamage(damage);
					Game.board.redrawUnit(v);
				}, "%s", v -> v.icon));
		}

		menu.display();
	}
}
