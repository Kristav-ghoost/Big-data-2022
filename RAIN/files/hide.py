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


def main(slika, text):
    img = slika
    height, width, tri = img.shape

    msg = text
    msg = msg.replace("č", "c")
    msg = msg.replace("ž", "z")
    msg = msg.replace("š", "s")

    tmp = to_binary(msg)

    msgBits = ""
    for i in range(len(tmp)):
        msgBits += str(tmp[i]).zfill(8)

    msgLen = len(msg)
    msgBits = bin(msgLen)[2:].zfill(32) + msgBits

    N = len(msgBits)
    counter = 0
    stopSteg = False

    for i in range(height):
        if stopSteg:
            break
        for j in range(width):
            if stopSteg:
                break
            for k in range(3):
                pxBin = bin(img[i][j][k])[2:].zfill(8)
                img[i][j][k] = int(lsb(pxBin, msgBits[counter]), 2)
                counter += 1
                if counter == N:
                    stopSteg = True
                    break
    cv2.imwrite("stegimage.png", img, [cv2.IMWRITE_PNG_COMPRESSION, 0])

img = cv2.imread(sys.argv[1])
main(img, sys.argv[2])