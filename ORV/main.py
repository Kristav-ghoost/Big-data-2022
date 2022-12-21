#!/usr/bin/python
# -*- coding: UTF-8 -*-
import cv2
import pickle
import sys

face_class = cv2.CascadeClassifier("haarcascade_frontalface_default.xml")
model = cv2.face_LBPHFaceRecognizer.create(radius=1, neighbors=8, grid_x=8, grid_y=8)
model.read("trainer.yml")

# Iz indexov label dobim imena
with open("label.pickle", "rb") as f:
    old_labels = pickle.load(f)
    labels = {v: k for k, v in old_labels.items()}

# Testna slika
image = cv2.imread(sys.argv[1])
imageRes = cv2.resize(image, (900, 1200))

# Zaznaj obraz
gray = cv2.cvtColor(imageRes, cv2.COLOR_BGR2GRAY)
face = face_class.detectMultiScale(gray, scaleFactor=1.5, minNeighbors=5)
roi = []
for x, y, w, h in face:
    roi = gray[y:y+h, x:x+w]

# Prediction
id_, conf = model.predict(roi)
print(labels[id_])
# print(labels)
# cv2.imshow("image", roi)
# cv2.waitKey(0)
