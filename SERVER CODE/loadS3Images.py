'''
Created on Dec 21, 2014

@author: rashmitonge
'''

import boto
import cv2

def getAlltheProfilePictures(event_name):
    
    picturePaths = list()
    
    conn = boto.connect_sdb('<AWS key>','<AWS Secret key>')
    dom = conn.get_domain('user_event')
    query = 'select * from `user_event` where event_name="' + event_name + '"';
    users = dom.select(query)
    for user in users:
            picturePaths.append(user['user_name'])
     
    return picturePaths
            
def getS3Pictures(picture_names):
    s3 = boto.connect_s3('<AWS key>','<AWS Secret key>')
    
    sift_features = list()
    user_names = list()
    
    for picture in picture_names:
        print picture
        key = s3.get_bucket('faceeventapp').get_key('profile_pictures/' + picture + '.jpg')
        key.get_contents_to_filename('test.jpg')
        resizeImage('test.jpg')
        
        sift_features.append(computeSiftImage('test.jpg'))
        user_names.append(picture)
    return user_names,sift_features

def resizeImage(path):
    img = cv2.imread(path)
    height,width,channels = img.shape;
    if height > 8000 or width > 6000: 
        small = cv2.resize(img, (0,0), fx=0.25, fy=0.25) 
        cv2.imwrite(path,small)
        
def getS3(event_name):
    return getS3Pictures(getAlltheProfilePictures(event_name))

def computeSiftImage(imagePath):
    resizeImage(imagePath)
    img = cv2.imread(imagePath)
    roi= cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    
    hog = cv2.HOGDescriptor()
#     kp = sift.detect(roi,None)
#     im =cv2.drawKeypoints(roi,kp)
#     cv2.imwrite('sift_keypoints' + imagePath[0:-4] + '.png',im)
    des = hog.compute(cv2.resize(roi,(360,480)),(600,600),(8,8))
#     cv2.imshow("Faces found", roi)
#     cv2.waitKey(0)
    return des
