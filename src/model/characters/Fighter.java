package model.characters;

public class Fighter extends Hero {

	public Fighter(String name, int maxHp, int attackDamage, int maxActions) {
		super(name, maxHp, attackDamage, maxActions);
	}

	public String toString() {
        return "Type: Fighter\n" +
                "Name: " + getName() + "\n" +
                "Max HP: " + getMaxHp() + "\n" +
                "Attack Damage: " + getAttackDmg() + "\n" +
                "Max Actions: " + getMaxActions();
    }
}
