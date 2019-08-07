
# Introduction
This Repository contains the source code and data for the [NFCSound](https://play.google.com/store/apps/details?id=com.bachmann.nfcsound) 
Android App.

The App is an interactive learning game for kids. The main input are several NFC cards labeled with line drawing of 
objects or animals. The app itself has different modes where those NFC cards are used in one or another way. Each modes
are targeting children with different ages from 1 to 10.

For improvements, questions or ideas for further functionality contact me via Google Play. If you wanna extend current 
library, or you are interested in implement further features do not hesitate, but please push changes.

For manual how to create the Cards by your self see [How to get started](#how-to-get-started).

## Functionallity
There are several NFC cards labled with e.g. a bear:
<br><img src="./app/src/main/assets/nfc_name/bear/line_drawing/bear.png" width="250">

Those NFC card are used to interact with the device. This is the unique feature of this app.

If the user touches the back of the phone with a card, the according image is displayed immediately e.g. the bear:
<br><img src="./app/src/main/assets/nfc_name/bear/19722.jpg" width="250">

In parallel a voice tells you the name of the object on the card in this case "bear" in different languages followed 
by the sound a bear makes "WUAAAHHH".

This behaviour is called *Explorer Mode*.

## Modes
Currently only one mode called [Explorer](###explorer) is implemented. There are other [features in planning](#future-features).

### Explorer
The explorer mode can be used by the youngest children (starting by age 1.5). Here all the NFC cards can be scanned
randomly. For each card a colored image of the object is shown on display and the accoring name and sound is played. 

# How to get started
1. Get the [NFCSound](https://play.google.com/store/apps/details?id=com.bachmann.nfcsound) app from Google Play.
2. Buy empty NFC cards in a online store of your choice. I bought them on Ebay <https://www.ebay.ch/sch/i.html?_nkw=292487267767>
3. Get the [pdf](./to_print/etikett_main.pdf) with the labels from *to_print* directory which are prepared for [Avery 3659](https://www.avery-zweckform.com/produkt/universal-etiketten-3659). You can get the labels on Ebay <https://www.ebay.ch/sch/i.html?_nkw=161139167120>
Or adapt the .tex file to match your labels available.
4. Use the *Write NFC Cards* function to write the card identification to the according NFC Card.


# Future Features

## Introduction Screen
Add a screen which allows the user to:
 - Choose different Games / Modes
 - Language selection

## Memory
1. Karten liegen verdeckt auf dem Tisch.
2. Lernvorgang
    a. Jede karte kann zwei mal gescannt werden. Das handy zeigt instantan das richtige Symbol an.
    b. Lernvorgang ist beendet sobald manuell beendet, oder alle karten zweimal ge-scannt.
    (c.) Erkennung, welche karten im spiel sind. es müssen nicht alle sein.
3. Memory spiel
    a. Handy zeigt ein symbol an (aus den zuvor gelernten)
    b. Entsprechende Karte muss nun gesucht werden.
       i. Korrekte karte erkannt. --> Bravo, entsprechendes Geräusch, das farbige bild...
       ii. Falsche karte --> Buzzer
    c. Möglichkeit zum Überspringen
4. Einen Score anzeigen

## Assign Card to Sound
1. Geräusch eines Objektes wird abgespielt.
2. Karte muss gesucht werden
    a. Korrete Karte --> Bravo, entsprechendes Geräusch, das farbige bild...
    b. Falsche Karte --> Buzzer, gesuchtes geräusch wird nocheinmal abgespielt
    c. Möglichkeit zum überspringen

## Mögliche Bravos:
Klatschen, Tanzende Tiere, Lustige Videos


# Details

## Release
First increase versionCode and versionName in the app build.gradle file.
To create a release go to 'Build' choose 'Generate Signed Bundle / APK...' and create a 'Android App Bundle'. After successful build the bundle is located in 'app/release/release/'.
