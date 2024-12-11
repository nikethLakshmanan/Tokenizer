#include "Card.h"

#include <Adafruit_ST7796S_kbv.h>
#include <Arduino.h>
#include <SdFat.h>

extern Adafruit_ST7796S_kbv tft;
extern SdFat SD;

int Card::new_variation() {
  for (int i = 0; i < 16; i++) {
    if (!getVariations()[i].initialized) {
      variation& var = getVariations()[i];
      var.count = 0;
      var.power = getBasePower();
      var.toughness = getBaseToughness();
      var.tapped = false;
      var.sick = true;
      var.initialized = true;
      num_variations++;
      return i;
    }
  }
  return -1;
}

Card loadCard(String cardname) {
  Card card;
  char buffer[1024];
  size_t len;

  String filename = cardname + ".txt";

  // tft.fillScreen(0x0000);
  // tft.setTextColor(0xFFFF);
  // tft.setTextSize(1);
  // tft.setCursor(0, 0);
  // tft.setTextWrap(false);
  // tft.print("cardname: " + cardname);
  // tft.print("\n");
  // tft.flush();

  File32 statsfile = SD.open(filename, O_RDONLY);
  // File32 statsfile = SD.open("/Progenitus.txt", O_RDONLY);

  if (!statsfile) {
    tft.print("ERROR");
    tft.print("\n");
    char buffer[1024];
    statsfile.getName(buffer, 1024);
    tft.print(filename);
    tft.print("\n");
    tft.print(buffer);
    tft.print("\n");
    tft.print(statsfile.getError());
    tft.print("\n");

    // find which character is different
    for (int i = 0; i < 1024; i++) {
      tft.print(filename[i]);
      tft.print(" ");
      tft.print(buffer[i]);
      tft.print("\n");
      if (filename[i] != buffer[i]) {
        tft.print("ERROR:\nPOS:");
        tft.print(i);
        tft.print(",(");
        tft.print((int)filename[i]);
        tft.print("),(");
        tft.print((int)buffer[i]);
        tft.print(")!!!");
        break;
      }
    }
    delay(100000);
    return card;
  }

  card.setName(cardname);
  card.setBasePower(statsfile.parseInt());
  card.setBaseToughness(statsfile.parseInt(SKIP_ALL, '\n'));
  card.setType(statsfile.readStringUntil('\n'));
  card.setText(statsfile.readStringUntil('\n'));

  // tft.print("Loaded card: ");
  // tft.print(card.getName());
  // tft.print("\n");
  // tft.print("Power: ");
  // tft.print(card.getBasePower());
  // tft.print("\n");
  // tft.print("Toughness: ");
  // tft.print(card.getBaseToughness());
  // tft.print("\n");
  // tft.print("Type: ");
  // tft.print(card.getType());
  // tft.print("\n");

  // delay(1000);

  statsfile.close();

  return card;
}
