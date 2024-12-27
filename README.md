# **Avancerad-Java-Manne-Michaelson-Slutprojekt**

## **Innehåll**
- [Beskrivning]
- [Installation]
- [Användning]
- [API-dokumentation
- [Teknisk information]

## **Beskrivning**
Syftet med programmet är att kunna använda en server och ett program så att de kan kommunicerad med varandra via REST-API. När du startar programmet så finns det knappar där du kan lägga till böcker i ett bibliotek med ID, namn och författare. Du kan också ta bort/ändra böcker i ditt bibliotek. I programmet kan du också hämta och se alla böcker i ditt bibliotek. 

## **Installation**
1. Klona projektet
2. Lokalisera filen "DemoApplication"
3. Starta "DemoApplication" (server är standrad localhost 8080, se till att inget annat program använder den)
4. Lokalisera filen "HelloApplication"
5. Starta HelloApplication
6. Navigera i projektet med knapparna och textboxarna som finns.

## **Använding**
Fyll i ID, namn och författare och klicka på "Add book" för att lägga till en bok i ditt bibliotek
Använd scrolldown listna för att välja vilken bok du vill ändra på eller ta bort. 
Klicka på "Se all books" för att se alla böcker på servern. 

## **API Dokumentation**
- GET /books       - Hämta alla böcker
- POST /books      - Lägg till en ny bok
- PUT /books/{id}  - Uppdatera bokinformation
- DELETE /books/{id} - Ta bort en bok

## **Teknisk information**
* Programmeringsspråk: Java
* Ramverk: Springboot, javaFX
* Build-verktyg: Maven
