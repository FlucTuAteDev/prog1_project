Előfeltételek
	Windows
		A program futtatásához erősen ajánlott a Windows Terminal nevű program használata, amit az alábbi linkre kattintva le lehet tölteni:
		https://www.microsoft.com/hu-hu/p/windows-terminal/9n0dx20hk701?rtc=1&activetab=pivot:overviewtab
		A cmd, illetve a powershell nem ismerik az ANSI escape code-okat, amiket használok, illetve az UTF-8 támogatás sem a legjobb.
	Linux / MAC
		Elméletileg nem kell semmit csinálni, minden csak működik

Program belépési pontja:
	src/Base/Main.java

Elindítás
	A terminál méretének legalább 120*30-asnak kell lennie, ha nem, akkor szétesik minden. Elvileg ez az alap ablakméret, amivel a 
	Windows Terminal megnyílik.

	A következő parancsokat a kicsomagolt mappában kell lefuttatni.
	Windows
		1. "chcp 65001" - Beállítja a terminál karakterkészletét utf-8-ra
		2. "javac -encoding utf-8 -d build -cp src src\Base\*.java"
		3. "java -cp build Base.Main"

	Linux / MAC
		1. "javac -encoding utf-8 -d build -cp src src/Base/*.java"
		2. "java -cp build Base.Main"
