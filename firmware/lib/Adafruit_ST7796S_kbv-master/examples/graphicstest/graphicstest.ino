#include <Adafruit_ImageReader.h>  // Image-reading functions
#include <Adafruit_SPIFlash.h>     // SPI / QSPI flash library
#include <SPI.h>
#include <SdFat.h>  // SD card & FAT filesystem library

#include "Adafruit_GFX.h"
#include "Adafruit_ST7796S_kbv.h"

// #include <FT6336U.h>
#include <Bounce2.h>
#include <Fonts/FreeSerif12pt7b.h>

// These are 'flexible' lines that can be changed
#define TFT_CS D2
#define TFT_DC D4
#define TFT_RST D3  // RST can be set to -1 if you tie it to Arduino's reset
#define TFT_LED D5
#define SD_CS D6

Adafruit_ST7796S_kbv tft = Adafruit_ST7796S_kbv(TFT_CS, TFT_DC, TFT_RST);
SdFat SD;                         // SD card filesystem
Adafruit_ImageReader reader(SD);  // Image-reader object, pass in SD filesys

int selected_card = 0;
int selected_variation = -1;  // -1 is none
int selected_state = -1;      // -1 none, 0 power&tough, 1 pow, 2 tough

struct variation {
  int count;
  int power;
  int toughness;
  bool tapped;
  bool sick;
  bool initialized;
};

class Card {
 private:
  String name;
  String text;
  String type;
  variation variations[16];

 public:
  // Constructor
  Card(String cardName = "", String cardText = "", String cardType = "") {
    name = cardName;
    text = cardText;
    type = cardType;
    for (int i = 0; i < 16; i++) {
      variations[i].initialized = false;
    }
  }

  // Setters
  void setName(String cardName) {
    name = cardName;
  }

  void setText(String cardText) {
    text = cardText;
  }

  void setType(String cardType) {
    type = cardType;
  }

  // Getters
  String getName() {
    return name;
  }

  String getText() {
    return text;
  }

  String getType() {
    return type;
  }

  variation* getVariations() {
    return variations;
  }
};

Bounce2::Button button = Bounce2::Button();  // INSTANTIATE A Bounce2::Button OBJECT
Card testcard = Card("Progenitus", "Flying", "Artifact");
void setup() {
  Serial.begin(9600);
  pinMode(TFT_LED, OUTPUT);

  digitalWrite(TFT_LED, HIGH);

  button.attach(D7, INPUT_PULLUP);
  button.interval(5);
  button.setPressedState(LOW);

  tft.begin();
  tft.setRotation(0);

  variation* testvar = &testcard.getVariations()[0];
  testvar->power = 5;
  testvar->toughness = 1;
  testvar->tapped = true;
  testvar->sick = true;
  testvar->count = 15;
  testvar->initialized = true;

  if (!SD.begin(SD_CS, SD_SCK_MHZ(50))) {
    tft.fillScreen(0x0000);
    tft.setTextColor(0xFFFF);
    tft.setTextSize(1);
    tft.setTextWrap(false);
    tft.print("ERROR");
    for (;;) continue;
  }

  displayCard(testcard);
}

void loop(void) {
  button.update();

  // IF THE BUTTON WAS PRESSED THIS LOOP:
  if (button.pressed()) {
    displayCard(testcard);
  }
}

void displayCard(Card card) {
  tft.fillScreen(0xFFFF);
  // header
  tft.fillRoundRect(4, 4, 312, 30, 4, 0x0000);
  tft.setFont(&FreeSerif12pt7b);
  tft.setCursor(16, 22);
  tft.setTextColor(0xFFFF);
  tft.setTextSize(1);
  tft.setTextWrap(false);
  tft.print(card.getName());

  // type, stats

  // variations

  for (int i = 0; i < 16; i++) {
    drawVariation(i, card.getVariations()[i]);
  }

  // art
  tft.fillRect(4, 38, 312, 232, 0x0000);

  ImageReturnCode stat;
  String filename = "/";
  filename += selected_card;
  filename += ".bmp";
  stat = reader.drawBMP(filename.c_str(), tft, 6, 40);
}

void drawVariation(int i, variation var) {
  int x = 4 + 78 * (i % 4);
  int y = 280 + 40 * (i / 4);
  tft.drawRect(x, y, 78, 40, 0x0000);
  if ((selected_variation == i) && (selected_state == -1))
    tft.fillRect(x + 1, y + 1, 76, 38, 0xffe0);
  else
    tft.fillRect(x + 1, y + 1, 76, 38, 0xffff);
  if (!var.initialized) return;
  if (var.tapped) tft.drawRect(x + 1, y + 1, 76, 38, 0x001F);
  if (var.sick) tft.drawRect(x + 2, y + 2, 74, 36, 0x1F1F);
  tft.setFont(&FreeSerif12pt7b);
  tft.setCursor(x + 8, y + 20);
  tft.setTextColor(0x0000);

  tft.print(var.power);
  tft.print("/");
  tft.print(var.toughness);

  tft.setFont();
  int16_t x1, y1;
  uint16_t w, h;
  tft.getTextBounds(String(var.count), 0, 0, &x1, &y1, &w, &h);

  tft.setCursor(x + 72 - w, y + 24);
  tft.print(var.count);
}
