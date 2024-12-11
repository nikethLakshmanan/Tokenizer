#include "Graphics.h"

#include <Adafruit_GFX.h>
#include <Adafruit_ImageReader.h>
#include <Adafruit_SPIFlash.h>
#include <Adafruit_ST7796S_kbv.h>
#include <Fonts/FreeSerif12pt7b.h>
#include <Fonts/FreeSerif9pt7b.h>

#include "Arduino.h"
#include "Card.h"

extern Adafruit_ST7796S_kbv tft;
extern Adafruit_ImageReader reader;
extern int selected_variation;
extern Card* current_card;

enum select_depth { CARD,
                    VARIATION,
                    STATS,
                    POWER,
                    TOUGHNESS };

extern select_depth selected_state;
extern int card_index;

enum anchor { TOP_LEFT,
              TOP_RIGHT,
              BOTTOM_LEFT,
              BOTTOM_RIGHT,
              NORMAL };

void drawTextBox(int x, int y, int BG, String text, anchor anc, int border = 0, int round = 0) {
  int16_t x1, y1;
  uint16_t w, h;
  tft.getTextBounds(text, 0, 0, &x1, &y1, &w, &h);

  int cx, cy;

  if (anc == TOP_LEFT) {
    cx = x - x1;
    cy = y - y1;
  } else if (anc == TOP_RIGHT) {
    cx = x - x1 - w;
    cy = y - y1;
  } else if (anc == BOTTOM_LEFT) {
    cx = x - x1;
    cy = y - y1 - h;
  } else if (anc == BOTTOM_RIGHT) {
    cx = x - x1 - w;
    cy = y - y1 - h;
  } else {
    cx = x;
    cy = y;
  }

  if (round) {
    tft.fillRoundRect(cx + x1 - border, cy + y1 - border, w + 2 * border, h + 2 * border, round, BG);
  } else {
    tft.fillRect(cx + x1 - border, cy + y1 - border, w + 2 * border, h + 2 * border, BG);
  }
  tft.setCursor(cx, cy);
  tft.print(text);
}

void drawCard(Card card) {
  tft.fillScreen(0xFFFF);
  // header
  tft.fillRoundRect(4, 4, 312, 30, 4, 0x0000);
  tft.setFont(&FreeSerif12pt7b);
  tft.setCursor(12, 24);
  tft.setTextColor(0xFFFF);
  tft.setTextSize(1);
  tft.setTextWrap(false);
  tft.print(card.getName());

  tft.setFont();
  drawTextBox(316, 4, 0x0000, String(card_index + 1) + "/10", TOP_RIGHT, 4, 4);

  // variations

  for (int i = 0; i < 16; i++) {
    drawVariation(i, card.getVariations()[i]);
  }

  // art
  tft.fillRect(4, 38, 312, 229, 0x0000);

  ImageReturnCode stat;
  String filename = "/";
  filename += card.getName();
  filename += ".bmp";
  stat = reader.drawBMP(filename.c_str(), tft, 6, 40);

  // type, stats

  tft.setTextColor(0xFFFF);
  tft.setFont(&FreeSerif9pt7b);
  tft.setTextSize(1);

  drawTextBox(5, 267, 0x0000, card.getType(), BOTTOM_LEFT, 4, 4);
  drawTextBox(316, 267, 0x0000, String(card.getBasePower()) + "/" + String(card.getBaseToughness()), BOTTOM_RIGHT, 4, 4);
}

void drawVariation(int i, variation var) {
  int x = 4 + 78 * (i % 4);
  int y = 280 + 40 * (i / 4);
  tft.drawRect(x, y, 78, 40, 0x0000);
  if ((selected_variation == i) && (selected_state == VARIATION))
    tft.fillRect(x + 1, y + 1, 76, 38, 0xffe0);
  else
    tft.fillRect(x + 1, y + 1, 76, 38, 0xffff);
  if (!var.initialized) return;
  if (var.tapped) {
    tft.drawRect(x + 1, y + 1, 76, 38, 0x001F);
    tft.drawRect(x + 2, y + 2, 74, 36, 0x001F);
  }
  if (var.sick) {
    tft.drawRect(x + 3, y + 3, 72, 34, 0xf800);
    tft.drawRect(x + 4, y + 4, 70, 32, 0xf800);
    tft.drawRect(x + 5, y + 5, 68, 30, 0xf800);
  }
  tft.setFont(&FreeSerif12pt7b);
  tft.setCursor(x + 8, y + 22);
  tft.setTextColor(0x0000);

  if (selected_state == STATS) {
    int16_t x1, y1;
    uint16_t w, h;
    int16_t cx, cy;

    cx = tft.getCursorX();
    cy = tft.getCursorY();
    tft.getTextBounds(String(var.power) + "/" + String(var.toughness), 0, 0, &x1, &y1, &w, &h);
    tft.fillRect(cx + 1, cy - 15, w, h, 0xffe0);
  } else if (selected_state == POWER) {
    int16_t x1, y1;
    uint16_t w, h;
    int16_t cx, cy;

    cx = tft.getCursorX();
    cy = tft.getCursorY();
    tft.getTextBounds(String(var.power), 0, 0, &x1, &y1, &w, &h);
    tft.fillRect(cx + 1, cy - 15, w, h, 0xffe0);
  }

  tft.print(var.power);
  tft.print("/");
  if (selected_state == TOUGHNESS) {
    int16_t x1, y1;
    uint16_t w, h;
    int16_t cx, cy;

    cx = tft.getCursorX();
    cy = tft.getCursorY();
    tft.getTextBounds(String(var.toughness), 0, 0, &x1, &y1, &w, &h);
    tft.fillRect(cx + 1, cy - 15, w, h, 0xffe0);
  }
  tft.print(var.toughness);

  tft.setFont();
  int16_t x1, y1;
  uint16_t w, h;
  tft.getTextBounds(String(var.count), 0, 0, &x1, &y1, &w, &h);

  tft.setCursor(x + 72 - w, y + 24);
  tft.print(var.count);
}

void drawVariation(int i) {
  if (i == -1) return;
  variation var = current_card->getVariations()[i];
  drawVariation(i, var);
}
