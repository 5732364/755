from flask import *
from werkzeug.security import *
from flask_sqlalchemy import SQLAlchemy
import uuid

# init Flask and database
app = Flask(__name__)
app.config['SECRET_KEY'] = 'mysecret'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///traffic_light.db'
db = SQLAlchemy(app)

# database for users and sessions
class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    password = db.Column(db.String(200), nullable=False)
    role = db.Column(db.String(20), nullable=False)

class Session(db.Model):
    # tracking user sessions
    session_id = db.Column(db.String(200), primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    user = db.relationship('User')

# setup the database and add sample users
with app.app_context():
    db.create_all()
    if not User.query.first():
        admin = User(username='admin', password=generate_password_hash('admin_pass'), role='admin')
        user = User(username='user', password=generate_password_hash('user_pass'), role='user')
        guest = User(username='guest', password=generate_password_hash('guest_pass'), role='guest')
        db.session.add_all([admin, user, guest])
        db.session.commit()

# render the homepage
@app.route('/')
def home():
    return render_template('index.html')

# login route for users
@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    user = User.query.filter_by(username=data['username']).first()
    if user and check_password_hash(user.password, data['password']):
        session_id = str(uuid.uuid4())
        new_session = Session(session_id=session_id, user=user)
        db.session.add(new_session)
        db.session.commit()
        return jsonify({"message": "Login successful", "session_id": session_id})
    return jsonify({"message": "Invalid"}), 401

def authorize(session_id, required_role):
    user_session = Session.query.filter_by(session_id=session_id).first()
    return user_session and user_session.user.role == required_role

# API for controlling traffic light
@app.route('/traffic_light', methods=['POST'])
def traffic_light():
    session_id = request.headers.get('Session-ID')
    if not session_id:
        return jsonify({"message": "Session ID required"}), 401

    user_session = Session.query.filter_by(session_id=session_id).first()
    if not user_session:
        return jsonify({"message": "Invalid session"}), 403

    action = request.json.get("action")
    if action in ["Red", "Yellow", "Green"] and authorize(session_id, 'admin'):
        return jsonify({"message": f"Traffic light set to {action}"})
    else:
        return jsonify({"message": "Unauthorized"}), 403

# logout route to clear sessions
@app.route('/logout', methods=['POST'])
def logout():
    session_id = request.headers.get('Session-ID')
    Session.query.filter_by(session_id=session_id).delete()
    db.session.commit()
    return jsonify({"message": "Logged out successfully"})


if __name__ == '__main__':
    app.run(debug=True)
