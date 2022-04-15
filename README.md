# Előfeltételek
## Linux / MAC
Elméletileg nem kell semmit csinálni, minden csak működik
## Windows
### Windows Terminal
A program futtatásához erősen ajánlott a [Windows Terminal](https://www.microsoft.com/hu-hu/p/windows-terminal/9n0dx20hk701?rtc=1&activetab=pivot:overviewtab) nevű program használata, amit a linkre kattintva le lehet tölteni. A cmd, illetve a powershell nem ismerik az ANSI escape code-okat, amiket használok, illetve az UTF-8 támogatás sem a legjobb.
### UTF-8
A Windowsnak sajnos nem elég az, hogy egy program támogatja az UTF-8-at, még mellette be kell kapcsolni, hogy a nem Unicode programok működjenek Unicode-dal. 

Ezt az *adminisztratív nyelv beállítások*-ban *(administrative language settings)* lehet megtenni. Ezt a *vezérzőpult*-ban *(control panel)* a *régió* *(region)* menüpontban lehet megtalálni. Ott fölül *adminisztratív* *(administrative)* fül és ott a *rendszer területi beállításainak módosítása* *(change system locale)* előügrik egy ablak, amiben a *Beta*-val kezdődő jelölőnégyzetet kell bepipálni. Ha a leírást nem lehetne követni, akkor [itt](https://hu.tinystm.org/fix-language-issues) egy képsor.

Ezután sajnos egy számítógép újraindítás szükséges, hogy minden működjön.

# Elindítás
