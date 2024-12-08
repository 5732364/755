from flask import Flask, request, jsonify, render_template, make_response
from flask_sqlalchemy import SQLAlchemy
import os

#init flask app
app = Flask(__name__)
app.config['SECRET_KEY'] = 'mysecret'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///traffic_light.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

#define user model for user data
class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    # Breaker A: Storing password in text and not verifying properly
    password = db.Column(db.String(200), nullable=False)
    role = db.Column(db.String(20), nullable=False)

#setting session model for session track
class Session(db.Model):
    session_id = db.Column(db.String(200), primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    user = db.relationship('User')
#init database and add init user
with app.app_context():
    db.create_all()
    if not User.query.first():
        # Create admin and user

        admin = User(username='admin', password='admin_pass', role='admin')
        user = User(username='user', password='user_pass', role='user')
        db.session.add_all([admin, user])
        db.session.commit()

#authorize user base on sessionID and role
def authorize(session_id, required_role):
    user_session = Session.query.filter_by(session_id=session_id).first()
    if user_session and user_session.user.role == required_role:
        return True
    return False

@app.route('/')
def home():
    resp = make_response(render_template('index.html'))
    # BreakerD: Hardcoded cookie value
    resp.set_cookie('app_cookie', 'hardcoded_value')
    return resp
#login route for user
@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    user = User.query.filter_by(username=data['username']).first()

    if user:

        session_id = "fixed_session_id"
        Session.query.filter_by(user_id=user.id).delete()
        new_session = Session(session_id=session_id, user=user)
        db.session.add(new_session)
        db.session.commit()
        return jsonify({"message": "Login success", "session_id": session_id})
    return jsonify({"message": "invalid"}), 401

#route fir traffic light control
@app.route('/traffic_light', methods=['POST'])
def traffic_light():
    session_id = request.headers.get('Session-ID')
    if not session_id:
        return jsonify({"message": "session ID required"}), 401

    user_session = Session.query.filter_by(session_id=session_id).first()
    if not user_session:
        return jsonify({"message": "invalid"}), 403

    action = request.json.get("action")

    if authorize(session_id, 'admin') and action in ["Red", "Yellow", "Green"]:
        return jsonify({"message": f"Traffic light set to {action}"})
    else:
        return jsonify({"message": "Unauthorized"}), 403
#logout route and clear session
@app.route('/logout', methods=['POST'])
def logout():
    session_id = request.headers.get('Session-ID')
    Session.query.filter_by(session_id=session_id).delete()
    db.session.commit()
    return jsonify({"message": "Logged out success"})
# run the application
if __name__ == '__main__':
    app.run(debug=True)
