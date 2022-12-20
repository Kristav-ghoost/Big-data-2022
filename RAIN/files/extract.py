import cv2
import binascii
import sys

def to_binary(a):
    l, m = [], []
    for i in a:
        l.append(ord(i))
    for i in l:
        m.append(int(bin(i)[2:]))
    return m


def lsb(binNum, n):
    return binNum[:-1] + n


def reverse_lsb(binNum):
    return binNum[len(binNum)-1]


def main(slika):

    img = slika
    height, width, tri = img.shape

    stgBits = ""
    for i in range(height):
        for j in range(width):
            for k in range(3):
                stgBits += reverse_lsb(bin(img[i][j][k])[2:].zfill(8))

    msgSize = int(stgBits[0:32], 2)
    stgMsg = stgBits[32:32+msgSize*8]
    n = int(stgMsg, 2)
    stgWord = str(binascii.unhexlify('%x' % n))
    print(stgWord[2:][:-1])

slika = cv2.imread(sys.argv[1])
main(slika)