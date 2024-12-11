#include "Communication.h"

#include <Arduino.h>
#include <SdFat.h>

extern SdFat SD;
extern int card_index;

Card receiveCard() {
#define CHUNK_SIZE 1024
  Card card;

  for (int i = 0; i < 10; i++) {
    card.getVariations()[i].initialized = false;
  }

  char buffer[1024];
  size_t len;

  len = Serial.readBytesUntil('\n', buffer, 1024);  // Card name
  buffer[len] = '\0';
  card.setName(buffer);

  len = Serial.readBytesUntil('\n', buffer, 1024);  // Card type
  buffer[len] = '\0';
  card.setType(buffer);

  len = Serial.readBytesUntil('\n', buffer, 1024);  // Card text
  buffer[len] = '\0';
  card.setText(buffer);

  int power = Serial.read();      // Base power
  int toughness = Serial.read();  // Base toughness
  card.setBasePower(power);
  card.setBaseToughness(toughness);

  // Load image
  int image_size = Serial.parseInt();
  Serial.read();  // Skip newline
  char chunk[CHUNK_SIZE];
  File32 imagefile = SD.open("/" + card.getName() + ".bmp", FILE_WRITE | O_CREAT | O_TRUNC);

  for (int i = 0; i < image_size; i += CHUNK_SIZE) {
    Serial.print("\x01");
    Serial.flush();

    Serial.readBytes(chunk, min(CHUNK_SIZE, image_size - i));

    imagefile.write(chunk, min(CHUNK_SIZE, image_size - i));
    imagefile.flush();
  }
  imagefile.close();

  // save stats
  File32 statsfile = SD.open("/" + card.getName() + ".txt", FILE_WRITE | O_CREAT | O_TRUNC);
  statsfile.print(power);
  statsfile.print("\n");
  statsfile.print(toughness);
  statsfile.print("\n");
  statsfile.print(card.getType());
  statsfile.print("\n");
  statsfile.print(card.getText());
  statsfile.print("\n");
  statsfile.close();

  // update card list
  File32 cardlist = SD.open("/cardlist.txt", O_RDWR);
  String cardnames[10];

  int upload_index = cardlist.parseInt();
  cardlist.readBytesUntil('\n', buffer, 1024);
  upload_index = card_index;

  for (int i = 0; i < 10; i++) {
    len = cardlist.readBytesUntil('\n', buffer, 1024);
    buffer[len] = '\0';
    cardnames[i] = buffer;
  }

  cardlist.truncate(0);
  cardlist.print((upload_index + 1) % 10);
  cardlist.print("\n");

  for (int i = 0; i < 10; i++) {
    if (i == upload_index) {
      cardlist.print(card.getName());
      cardlist.print("\n");
    } else {
      cardlist.print(cardnames[i]);
      cardlist.print("\n");
    }
  }
  cardlist.close();

  return card;
}
