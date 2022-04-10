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
		super("Villámcsapás", "⚡", Colors.DAMAGE, 60, 5, 30, hero);
	}

	@Override
	public void cast() {
		if (!this.hero.useManna(this.manna))
			return;

		Menu<Unit> menu = new BasicMenu<>("Támadható egységek: ", Game.menuView);
		Hero enemy = this.hero == Game.player ? Game.ai : Game.player;
		for (Unit unit : enemy.getUnits()) {
			menu.addItem(new MenuItem<>(unit, null,
				Colors.textFromBg(enemy.COLOR), 
				enemy.COLOR,
				v -> {
					double damage = v.hero.getSkill("magic").getValue() * this.multiplier;
					v.takeDamage(damage);
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
