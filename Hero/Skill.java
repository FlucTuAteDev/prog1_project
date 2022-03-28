package Hero;

import java.util.concurrent.atomic.AtomicInteger;

public class Skill {
	public String name;
	public AtomicInteger value;

	public Skill(String name, int value) {
		this.name = name;
		this.value = new AtomicInteger(value);
	}
}