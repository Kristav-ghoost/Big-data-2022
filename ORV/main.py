import cv2

face_class = cv2.CascadeClassifier("haarcascade_frontalface_default.xml")

image = cv2.imread("testImage.jpg")

roi = []
gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
face = face_class.detectMultiScale(gray, scaleFactor=1.5, minNeighbors=5)
for x, y, w, h in face:
    roi = gray[y:y+h, x:x+w]

cv2.imshow("Found Face", roi)
cv2.waitKey(0)
