# Traffic Light System

### Overview
This project add secure session management to a simple traffic light control system. Users can login to manage the traffic light. It shows how to do authentication authorization and secure session handling in a small web app. It also test some bad design breaker to show how test can fail or pass after fixing code.

### Team members
- Xu
- Chen

### Instructions
1. Run app.py - Start the backend by running app.py. The system will create a local server where users can log in and interact with the traffic light control system.
2. Login to the System using the following credentials.admin / admin_pass, user / user_pass, guest / guest_pass
3. Control the Traffic Light
4. Logout

### Testing
Run pytest to test the code. One test fail because we not fix authentication breaker. Another test pass because we fix authorization breaker. This show difference between a broken and fixed breaker.

### Breakers
The application contains simulated vulnerabilities:
1. Authentication Failure: Passwords are not properly validated.
2. Authorization Failure: Non-admin users cannot change traffic light settings.
3. Predictable Session IDs: Session IDs are fixed and lack randomness.
4. Hardcoded Cookies: A hardcoded cookie value is included.

### Frameworks Used
- Flask
- Flask-SQLAlchemy
- Python UUID
- SQLite for database