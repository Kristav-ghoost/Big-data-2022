import os
import cv2

face_class = cv2.CascadeClassifier("haarcascade_frontalface_default.xml")

# Direktorija kjer se nahaja projekt in testne slike
BASE_DIRECTORY = os.path.dirname(os.path.abspath(__file__))
IMAGE_DIRECTORY = os.path.join(BASE_DIRECTORY, "test_images")

trainer_array = []
label_array = []
counter = 0
label_ids = {}

for root, dirs, files in os.walk(IMAGE_DIRECTORY):
    for file in files:
        if file.endswith("png") or file.endswith("jpg"):
            path = os.path.join(root, file)  # Pot do slike
            label = os.path.basename(root).lower()  # ime
            print(path)
            print(label)
