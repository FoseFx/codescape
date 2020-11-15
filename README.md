# Codescape

![Missions Screenshot](.hidden/missions.jpg?raw=true)

## Code-was?
Codescape ist ein _serious game_ der RWTH Aachen, das zur Prüfungszulassung für Progra gespielt werden muss.
Es gibt mehrere Level, durch welches man einen Hund durch die Methoden `move()`, `turnLeft()` usw. steuern muss.
Die Challenge besteht darin, dass vor Ausführung der Code analysiert und jeder Ausdruck dieser Methoden summiert wird.
```Java
move();
turnLeft();
pickUp();
move();
// 4 Aufrufe

for (int i = 0; i < N; i++) {
  move();
}
// 1 Aufruf

System.out.println(1000 + 1000 - 1000); // 0 Aufrufe
```
Für jedes Level exisitert ein Limit dieser Aufrufe.

## Road to 0

Java erlaubt das erstellen von sub-processes mittels `java.util.Runtime.getRuntime().exec("COMMAND HERE");`.
So lässt sich einfache Recon betreiben. Auf Grund der (unterschiedlichen) outputs von `hostname` lässt sich folgern, dass der Java Code in einer VM oder (wahrscheinlicher) in einem container läuft. Genauer in einem Debian container (`uname -a`).

Jede Java Klasse, die ausgeführt werden soll ist ein Child der Klasse `Dogbot`. Aber wo kommt die eigentlich her?
Auf dem Filesystem befindet sich eine zugehörige `.jar` Datei, ein Java Archive. Leider kann man dieses nicht in dem container extrahieren, da es sich um ein `read-only filesystem` handelt.

Also habe ich sie mir per `base64` über den stdout und dann über die Antwort des Servers zu mir geschickt. Alle 2.5 MB. Mein Firefox ist abgeschmiert. (Zum Glück gibt's curl c:)

Im Archiv befand sich neben `okhttp3` und utility Klassen von kotlin die `Dogbot.class`. Java Bytecode lässt sich übrigends sehr nett de-kompilieren.

```Java
public Dogbot() {
        this.WORLD_URL = "http://" + System.getenv("WORLD") + "/";
        this.client = new OkHttpClient();
    }
    
    private String doRequest(final String target, final String body) {
        try {
            final Request request = new Request.Builder().url(this.WORLD_URL + target)
              .post(RequestBody.create(body, MediaType.get("text/plain; charset=utf-8"))).build();
            final Response response = this.client.newCall(request).execute();
            final String responseBody = (response.body() != null) ? response.body().string() : "";
            if (response.code() == 410) {
                Thread.currentThread().stop();
            }
            return responseBody;
        }
        catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public final void move() {
        this.doRequest("move", "");
    }
  ...
```

Der Container schickt HTTP-Requests an den Host, welcher dann die Aktion selber ausführt. Der Container wird also wie ein Client betrachtet und den vertraut man ja bekanntlich selten. Fröhliches durch die Map cheaten fällt damit also erstmal raus.

Aaaaber: Die okhttp3 calls werden ja nicht bei der code analysis gezählt. Wenn man sich jetzt also anstrengt kann man (bisher) jedes Level mit 0 Aufrufen lösen.

## Alternative

Tobi (Ty2z#6417 auf Discord) hat eine viel elegantere Möglichkeit gefunden das Zählerscript zu überlisten:
```Java
...
class Lmao {
  public void move(MyDogbot b) {
    b.move();
  }
}
...

new Lmao().move(this): // 0 Aufrufe
```
Scheinbar zählt das Script nur invocations zu Methoden von `MyDogbot` und nicht einmal die von Subklassen.

Chapeau!


MIT License
