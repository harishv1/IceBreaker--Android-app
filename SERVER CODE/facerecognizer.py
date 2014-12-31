
import cv2
import boto
from test import detectFaces
from loadS3Images import *
import json
import numpy as np
from math import floor

def matchFaces(imagePath,cascPath,event_name):
    
    user_names,target_users = getS3(event_name)
    des2 = np.array(target_users,dtype=np.float).astype(np.float32)
    response = np.arange(1,len(user_names)+1,1)
    
    faces = detectFaces(imagePath, cascPath)
    
    # resize image and save it back
    resizeImage(imagePath)
    img = cv2.imread(imagePath)
    gray= cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    
    hog = cv2.HOGDescriptor()
        
    knn = cv2.KNearest();
    knn.train(des2,response)
    data = {}
    num = 0;
    for face in faces:
        roi = gray[face[1]:face[3], face[0]:face[2]]
        des1 = hog.compute(cv2.resize(roi,(360,480)),(600,600),(8,8))
        a = np.matrix(sum(des1.tolist(),[]),dtype=np.float).astype(np.float32)
        num_matches = list()
        ret,results,neighbors,dist = knn.find_nearest(a,1)
        best_match = user_names[int(results[0][0])-1]
        center_x = int((face[0]+face[2])/2)
        center_y = int(face[1])
        data[num] = {}
        data[num]['match'] = best_match
        data[num]['centreX'] = center_x
        data[num]['centreY'] = center_y
        num = num+1
    data['numFaces'] = num
    json_data = json.dumps(data)
    return json_data     
       
if __name__=='__main__':
    imagePath = 'test.jpg'
    cascPath = 'haarcascade_frontalface_default.xml'
    matchFaces(imagePath, cascPath,'game')
    
