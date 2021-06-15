from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = "postgresql://rankcity:rankcity@db:5432/rankcity_dev"
db = SQLAlchemy(app)
migrate = Migrate(app, db)


class UserModel(db.Model):
    __tablename__ = 'user'

    username = db.Column(db.String(), primary_key=True)
    email = db.Column(db.String())
    town = db.Column(db.String())
    accept_eula = db.Column(db.Boolean())
    # image = db.Column(db.LargeBinary(length=2048))
    image = db.Column(db.String())

    def __init__(self, username, email, town, accept_eula, image):
        self.username = username
        self.email = email
        self.town = town
        self.accept_eula = accept_eula
        self.image = image

    def __repr__(self):
        return f"<User {self.username}>"


class RouteModel(db.Model):
    __tablename__ = 'route'

    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String())
    date = db.Column(db.String())
    time = db.Column(db.String())
    user = db.Column(db.String(), db.ForeignKey('user.username'))
    score = db.Column(db.Float())

    def __init__(self, title, date, time, user, score):
        self.title = title
        self.user = user
        self.date = date
        self.time = time
        self.score = score

    def __repr__(self):
        return f"<Recorrido {self.title}>"


class StreetModel(db.Model):
    __tablename__ = 'street'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String())

    def __init__(self, name):
        self.name = name

    def __repr__(self):
        return f"<Calle {self.id} {self.name}>"


class RouteStreetModel(db.Model):
    __tablename__ = 'route_street'

    route = db.Column(db.Integer(), db.ForeignKey('route.id'), primary_key=True)
    street = db.Column(db.Integer(), db.ForeignKey('street.id'), primary_key=True)
    score = db.Column(db.Float())

    def __init__(self, route, street, score):
        self.route = route
        self.street = street
        self.score = score

    def __repr__(self):
        return f"Recorrido {self.route} -> Calle {self.street} -> Puntuación {self.score}>"


@app.route('/')
def hello():
    return {"hello": "world"}


@app.route('/routes', methods=['POST', 'GET'])
def handle_routes():
    if request.method == 'POST':
        if request.is_json:
            data = request.get_json()
            new_route = RouteModel(title=data['title'], date=data['date'], time=data['time'],
                                 user=data['user'], score=data['score'])
            db.session.add(new_route)
            db.session.commit()
            return {"message": f"Route {new_route.__repr__()} has been created successfully."}
        else:
            return {"error": "The request payload is not in JSON format"}

    elif request.method == 'GET':
        routes = RouteModel.query.all()
        results = [
            {
                "id": route.id,
                "title": route.title,
                "date": route.date,
                "time": route.time,
                "user": route.user,
                "score": route.score
            } for route in routes]

        return {"count": len(results), "routes": results}


@app.route('/routes/<route_id>', methods=['GET', 'POST','PUT', 'DELETE'])
def handle_route(route_id):
    route = RouteModel.query.get_or_404(route_id)

    if request.method == 'GET':
        response = {
            "id": route.id,
            "title": route.title,
            "date": route.date,
            "score": route.score
        }
        return {"message": "success", "route": response}

    # elif request.method == 'POST':
    #     # Añadir calles a un recorrido
    #     data = request.get_json()
    #     streets = data['streets']
    #     for street in streets:
    #         new_street_route = RouteStreetModel(route=route, street=street.id, score=street.score)
    #         db.session.add(new_street_route)
    #         db.session.commit()
    #     return {"message": f"Streets {streets} in {route.id} has been added successfully."}

    elif request.method == 'PUT':
        data = request.get_json()
        route.title = data['title']
        route.date = data['date']
        route.time = data['time']
        route.score = data['score']
        db.session.add(route)
        db.session.commit()
        return {"message": f"Recorrido {route.id} successfully updated"}

    elif request.method == 'DELETE':
        db.session.delete(route)
        db.session.commit()
        return {"message": f"Recorrido {route.id} successfully deleted."}


@app.route('/routes/user/<user_id>', methods=['GET', 'POST'])
def get_routes_user(user_id):
    user = UserModel.query.get_or_404(user_id)

    if request.method == 'GET':
        routes = RouteModel.query.filter_by(user=user.username).all()
        results = [
            {
                "id": route.id,
                "title": route.title,
                "date": route.date,
                "time": route.time,
                "user": route.user,
                "score": route.score
            } for route in routes
        ]
        list_streets = []
        for route in results:
            streets = RouteStreetModel.query.filter_by(route=route['id']).all()
            for street in streets:
                street_aux = StreetModel.query.get_or_404(street.street)
                street = {
                    "name": street_aux.name,
                    "score": street.score
                }
                list_streets.append(street)
            route['streets'] = list_streets
            list_streets = []

        return jsonify(results)

    elif request.method == 'POST':
        if request.is_json:
            data = request.get_json()
            new_route = RouteModel(title=data['title'], date=data['date'], time=data['time'],
                                 user=user.username, score=data['score'])
            db.session.add(new_route)
            db.session.commit()
            for street in data['streets']:
                street_name = street['name']

                street_db = StreetModel.query.filter_by(name=street_name).first()
                if street_db is None:
                    street_db = StreetModel(name=street_name)
                    db.session.add(street_db)
                    db.session.commit()

            route_streets = RouteStreetModel(new_route.id, street_db.id, street['score'])
            db.session.add(route_streets)
            db.session.commit()
            
            return {"message": f"Route {new_route.__repr__()} has been created successfully."}


@app.route('/users', methods=['POST', 'GET'])
def handle_users():
    if request.method == 'POST':
        if request.is_json:
            data = request.get_json()
            print(data)
            new_user = UserModel(username=data['username'], email=data['email'], town=data['town'], accept_eula=data['accept_eula'], image="")
            db.session.add(new_user)
            db.session.commit()
            response = {
                "username": new_user.username,
                "email": new_user.email,
                "town": new_user.town,
                "accept_eula": new_user.accept_eula,
                "image": new_user.image
            }
            return response
        else:
            return {"error": "The request payload is not in JSON format"}

    elif request.method == 'GET':
        users = UserModel.query.all()
        results = [
            {
                "username": user.username,
                "email": user.email,
                "town": user.town,
                "accept_eula": user.accept_eula,
                "image": user.image
            } for user in users]

        return {"count": len(results), "users": results}


@app.route('/users/<user_id>', methods=['GET', 'PUT', 'DELETE'])
def handle_user(user_id):
    user = UserModel.query.get_or_404(user_id)

    if request.method == 'GET':
        response = {
            "username": user.username,
            "email": user.email,
            "town": user.town,
            "accept_eula": user.accept_eula,
            "image": user.image
        }
        return response

    elif request.method == 'PUT':
        data = request.get_json()
        # user.username = data['username']
        # user.email = data['email']
        # user.town = data['town']
        # user.accept_eula = data['accept_eula']
        user.image = data['image']
        db.session.add(user)
        db.session.commit()
        return {"message": f"User {user.username} successfully updated"}

    elif request.method == 'DELETE':
        db.session.delete(user)
        db.session.commit()
        return {"message": f"User {user.username} successfully deleted."}


@app.route('/streets', methods=['POST', 'GET'])
def handle_streets():
    if request.method == 'POST':
        if request.is_json:
            data = request.get_json()
            new_street = StreetModel(name=data['name'])
            db.session.add(new_street)
            db.session.commit()
            return {"message": f"Street {new_street.__repr__()} has been created successfully."}
        else:
            return {"error": "The request payload is not in JSON format"}

    elif request.method == 'GET':
        streets = StreetModel.query.all()
        results = [
            {
                "id": street.id,
                "name": street.name
            } for street in streets]

        return {"count": len(results), "streets": results}


@app.route('/streets/<street_id>', methods=['GET', 'PUT', 'DELETE'])
def handle_street(street_id):
    street = StreetModel.query.get_or_404(street_id)

    if request.method == 'GET':
        response = {
            "id": street.id,
            "name": street.name
        }
        return {"message": "success", "street": response}

    elif request.method == 'PUT':
        data = request.get_json()
        street.name = data['name']
        db.session.add(street)
        db.session.commit()
        return {"message": f"Calle {street.id} {street.name} successfully updated"}

    elif request.method == 'DELETE':
        db.session.delete(street)
        db.session.commit()
        return {"message": f"Calle {street.id} {street.name} successfully deleted."}


@app.route('/ranking', methods=['GET'])
def get_ranking():
    if request.method == 'GET':
        users = UserModel.query.all()
        results = []
        for user in users:
            routes = RouteModel.query.filter_by(user=user.username)
            total_score = 0
            for route in routes:
                total_score = total_score + route.score
            results.append({
                    "user": user.username, 
                    "score": total_score}
                    )
            total_score = 0

        results = sorted(results, key=lambda x : x['score'], reverse=True)

        return jsonify(results)


if __name__ == '__main__':
    app.run(host='0.0.0.0')
