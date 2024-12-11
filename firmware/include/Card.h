#ifndef CARD_H
#define CARD_H

#include <Arduino.h>

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
  int base_power;
  int base_toughness;
  int num_variations;

 public:
  // Constructor
  Card(String cardName = "", String cardText = "", String cardType = "") {
    name = cardName;
    text = cardText;
    type = cardType;

    base_power = 0;
    base_toughness = 0;

    num_variations = 0;
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
    text.trim();
  }

  void setType(String cardType) {
    type = cardType;
    type.trim();
  }

  void setBasePower(int power) {
    base_power = power;
  }

  void setBaseToughness(int toughness) {
    base_toughness = toughness;
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

  int getBasePower() {
    return base_power;
  }

  int getBaseToughness() {
    return base_toughness;
  }

  variation* getVariations() {
    return variations;
  }

  int getNumVariations() {
    return num_variations;
  }

  int new_variation();
};

Card loadCard(String cardname);

#endif  // CARD_H
