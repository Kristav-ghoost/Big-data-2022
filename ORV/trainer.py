import os
from PIL import Image
import numpy as np
import pickle
import cv2

face_class = cv2.CascadeClassifier("haarcascade_frontalface_default.xml")

# Direktorija kjer se nahaja projekt in testne slike
BASE_DIRECTORY = os.path.dirname(os.path.abspath(__file__))
IMAGE_DIRECTORY = os.path.join(BASE_DIRECTORY, "test_images")

trainer_array = []
label_array = []
counter = 0
label_ids = {}

# Zanka, da pridemo do vseh slik v mapi
for root, dirs, files in os.walk(IMAGE_DIRECTORY):
    for file in files:
        if file.endswith("png") or file.endswith("jpg"):
            path = os.path.join(root, file)  # Pot do slike
            label = os.path.basename(root)  # ime

            # tabela label
            if label not in label_ids:
                label_ids[label] = counter
                counter = counter + 1
            label_id = label_ids[label]

            # Odpremo sliko iz poti in convertamo v sivo
            image = Image.open(path).convert("L")
            image_array = np.array(image, "uint8")

            # Iz slike zaznamo obraz nato pa shranimo v polje
            face = face_class.detectMultiScale(image_array, scaleFactor=1.5, minNeighbors=5)
            for x, y, w, h in face:
                roi = image_array[y:y + h, x:x + w]
                trainer_array.append(roi)
                label_array.append(label_id)

# Shranim imena label
with open("label.pickle", "wb") as f:
    pickle.dump(label_ids, f)

# Recognizer
model = cv2.face_LBPHFaceRecognizer.create(radius=1, neighbors=8, grid_x=8, grid_y=8)

# Natreniran model se shrani v trainer.yml
model.train(trainer_array, np.array(label_array))
model.save("trainer.yml")
