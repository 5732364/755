import pytest
import base64
from app import app, db, User, Session

# Set up initialize the app and database for testing

@pytest.fixture
def client():
    app.config['TESTING'] = True
    with app.test_client() as client:
        with app.app_context():
            db.drop_all()
            db.create_all()
            admin = User(username='admin', password='admin_pass', role='admin')
            user = User(username='user', password='user_pass', role='user')
            db.session.add_all([admin, user])
            db.session.commit()
        yield client
#Test case for authentication fail
def test_authentication_failure(client):
    #attempt login with wrong password
    response = client.post('/login', json={'username':'admin','password':'wrong_pass'})
    data = response.get_json()

    if response.status_code == 200:
        pytest.fail("Authentication test is failed as expected: The system allowed login with invalid credentials.")
## Test  for authorization success and failure
def test_authorization_success(client):
    # Login as admin to get a valid session_id
    response = client.post('/login', json={'username':'admin','password':'admin_pass'})
    assert response.status_code == 200
    session_id = response.get_json()['session_id']

    #  change traffic light color as admin
    response_light = client.post('/traffic_light', json={'action':'Green'}, headers={'Session-ID':session_id})
    assert response_light.status_code == 200
    assert response_light.get_json()['message'] == "Traffic light set to Green"

    # Logout the admin
    client.post('/logout', headers={'Session-ID': session_id})

    # login as user
    user_login = client.post('/login', json={'username':'user','password':'user_pass'})
    assert user_login.status_code == 200
    user_session_id = user_login.get_json()['session_id']

    # Try changing the traffic light as a normal user
    response_light_user = client.post('/traffic_light', json={'action':'Red'}, headers={'Session-ID':user_session_id})

    assert response_light_user.status_code == 403
    assert response_light_user.get_json()['message'] == "Unauthorized"


#Test Session ID encryption and security

def test_bugs_in_encryption_of_session_data(client):


    # Login as a user
    resp = client.post('/login', json={'username':'user','password':'user_pass'})
    assert resp.status_code == 200
    session_id = resp.get_json().get('session_id')

   #Attempt to decode the session ID
    try:
        decoded = base64.b64decode(session_id)
        # Check if it contains readable info
        if b'user' in decoded or b'admin' in decoded or b'username' in decoded:
            pytest.fail("Session data appears to not securely encrypted.")
    except Exception:

        pass

# test seession id random and entropy
def test_predictable_session_id_entropy(client):


    session_ids = []
    for _ in range(5):
        resp = client.post('/login', json={'username':'user','password':'user_pass'})
        assert resp.status_code == 200
        sid = resp.get_json().get('session_id')
        session_ids.append(sid)

    # Check if all are identical
    if len(set(session_ids)) == 1:
        pytest.fail("All session IDs are identical showing predictability.")

    # Check length and basic complexity

    for sid in session_ids:
        if len(sid) < 16:
            pytest.fail(f"Session ID '{sid}' is short, indicating low entropy.")


def test_hardcoded_cookies(client):
    # Check if the server sets a hardcoded cookie
    resp = client.get('/')
    cookie_headers = resp.headers.getlist('Set-Cookie')

    # Look for a hardcoded cookie
    hardcoded_cookie_value = None
    for ch in cookie_headers:
        if 'app_cookie=' in ch:
            hardcoded_cookie_value = ch.split('app_cookie=')[1].split(';')[0]

    if hardcoded_cookie_value is None:
        pytest.fail("Expected a hardcoded cookie but none was found. If the code should have one it's missing.")
    else:

        if hardcoded_cookie_value == 'hardcoded_value':
            pytest.fail("A hardcoded cookie was found a security breaker.")
