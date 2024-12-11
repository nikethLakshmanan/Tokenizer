import serial

import time
# https://stackoverflow.com/questions/6591931/getting-file-size-in-python

chunk_size = 1024


def getSize(fileobject):
    fileobject.seek(0, 2)  # move the cursor to the end of the file
    size = fileobject.tell()
    return size


with serial.Serial('COM5') as ser:
    ser.write(b'Progenitus\n')
    ser.write(b'Artifact Creature - Fish\n')
    ser.write(b'Flying\n')
    ser.write(b'\x03')
    ser.write(b'\x05')

    with open('0.bmp', 'rb') as f:
        print(str(getSize(f)))
        before = time.time()
        ser.write(str.encode(str(getSize(f))) + b'\n')

        for i in range(0, getSize(f), chunk_size):
            print(i)
            ser.read()
            f.seek(i)
            read = f.read(chunk_size)
            ser.write(read)

        after = time.time()

    print((after - before))
