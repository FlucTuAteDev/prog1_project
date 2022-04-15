package Hero;

import java.util.List;
import java.util.Random;

import Base.Game;
import Board.Board;
import Board.Tile;
import Spells.Spell;
import Units.Unit;
import Utils.RandomHelper;
import Utils.ThreadHelper;
import View.View;
import View.Colors.RGB;

public class AI extends Hero {

	public AI(String name, RGB color, View view) {
		super(name, color, view);
	}

	@Override
	public void init() {
		this.setMoney(Game.Constants.AI_MONEY);
		int skillBudget = this.money / 3;
		int spellBudget = RandomHelper.getRandomElement(List.of(60, 240, 300));
		int unitBudget = this.money - skillBudget - spellBudget;

		// Buy skills
		List<String> skillKeys = skills.keySet().stream().toList();
		while (true) {
			String key = RandomHelper.getRandomElement(skillKeys);
			Skill skill = skills.get(key);

			int skillPrice = skill.hero.skillPrice;
			if (skillPrice > skillBudget) break;

			skillBudget -= skillPrice;
			skills.get(key).buy();
		}

		// Buy spells
		for (Spell spell : spells.values()) {
			if (spell.price > spellBudget) break;
			spellBudget -= spell.price;
			spell.buy();
		}

		outer:
		while (true) {
			for (Unit unit : this.units) {
				if (unit.price > unitBudget) break outer;
				unitBudget -= unit.price;
				unit.buy(1);
			}
		}
	}

	@Override
	public void placeUnits() {
		Random rand = new Random();
		for (Unit unit : this.units) {
			int row, col;
			Tile curr = null;
			do {
				row = rand.nextInt(Board.ROWS);
				col = rand.nextInt(Board.COLS - 2, Board.COLS);
				curr = Game.board.getTile(row, col);
			} while (curr.hasUnit());

			unit.setTile(curr);
		}
	}

	@Override
	public void takeTurn(Unit unit) {
		Hero hero = unit.hero;
		ThreadHelper.sleep(Game.Constants.TURN_DELAY);

		if (unit.attackableUnits().size() > 0) {
			// Unit attack
			Unit target = unit.attackableUnits().stream().min((a, b) -> a.getHealth() - b.getHealth()).get();

			unit.attack(target);
			hero.usedUnit = true;
		} else {
			int action = RandomHelper.getInt(2);
			// Use unit
			if (action == 0) {
				// Move towards the unit with the lowest hp to the closest tile possible
				Unit target = hero.getEnemy().getAliveUnits().stream().min((a, b) -> a.getHealth() - b.getHealth()).get();
	
				Tile start = unit.getTile();
				Tile dest = target.getTile();
				
				List<Tile> path = Game.board.findPath(start, dest);
				// Path contains the destination and we strip that from the list
				unit.move(path.subList(0, Math.min(unit.speed, path.size() - 1)));

				hero.usedUnit = true;
			// Use hero
			} else if (action == 1) {
				int heroAction = RandomHelper.getInt(2);

				// Use attack
				if (heroAction == 0) {
					Unit target = RandomHelper.getRandomElement(this.getEnemyUnits());
					this.attack(target);
				// Use magic
				} else if (heroAction == 1) {
					Spell spell = RandomHelper.getRandomElement(this.getActiveSpells());
					spell.cast();
				}
				
				hero.usedAbility = true;
			}
		}
	}
}
