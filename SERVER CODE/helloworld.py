from flask import Flask, render_template, request, redirect, url_for, send_from_directory
from werkzeug.utils import secure_filename
from facerecognizer import matchFaces
import os
app = Flask(__name__)

@app.route('/')
def hello_world():
    return 'Hello World!'

# This is the path to the upload directory
app.config['UPLOAD_FOLDER'] = 'uploads/'
# These are the extension that we are accepting to be uploaded
app.config['ALLOWED_EXTENSIONS'] = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])
 
# For a given file, return whether it's an allowed type or not
def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in app.config['ALLOWED_EXTENSIONS']

@app.route('/login', methods=['GET', 'POST'])
def login():
    cascPath = 'haarcascade_frontalface_default.xml'
    if request.method == 'POST':
        int_message = 1
        print "Data uploading"
        print request.headers
        print request.method
        for v in request.values:
          print v
        file = request.files['image']
        strdata=request.form['stringdata']
        print strdata
        if file and allowed_file(file.filename):
            # Make the filename safe, remove unsupported chars
            filename = secure_filename(file.filename)
            # Move the file form the temporal folder to
            # the upload folder we setup
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            # Redirect the user to the uploaded_file route, which
            # will basicaly show on the browser the uploaded file
            json_data = matchFaces(os.path.join(app.config['UPLOAD_FOLDER'], filename), cascPath, strdata)
            return json_data
#             return redirect(url_for('uploaded_file',
#                                 filename=filename))
        
    else:
        print "hi"
        return "Harish"
if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0')
