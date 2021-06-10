# API_RankCity
API REST para RackCity app


### Requests importantes

* Add user -> POST http://localhost:5000/users 
```json
{
    "username": "alejoncv",
    "name": "Alejo",
    "email": "alejo@gmail.com",
    "accept_eula": true
}
```

* Add street -> POST http://localhost:5000/streets
```json
{
    "id": 345,
    "name": "Avenida Arteixo"
}
```

* Add route with streets -> POST http://127.0.0.1:5000/routes/user/userID
```json
{
    "id": 12345,
    "title": "Primer recorrido",
    "date": "10-10-2010",
    "time": 1111,
    "score": 90,
    "streets": [
        {
            "id_street": 123,
            "score": 10
        },
        {
            "id_street": 234,
            "score": 50
        },
        {
            "id_street": 345,
            "score": 30
        }
    ]
}
```

* Get all routes(and streets) for a user -> GET http://127.0.0.1:5000/routes/user/userID


### Requests antiguas

* GET all routes -> http://localhost:5000/routes
* GET user routes -> http://localhost:5000/routes/id
* POST add route -> http://localhost:5000/routes
```json
  {
    "id": 1223,
    "title": "Título del recorrido",
    "date": "Fecha del recorrido", 
    "user": 111,
    "score": 1200
  }
```
* GET a route -> http://localhost:5000/routes/<route_id>
* POST add streets into route -> http://localhost:5000/routes/<route_id>
```json
  {
    "id": 1223,
    "title": "Título del recorrido",
    "date": "Fecha del recorrido", 
    "user": 111,
    "score": 1200
  }
```

* PUT change route fields -> http://localhost:5000/routes/<route_id>
```json
  {
    "id": 1223,
    "title": "Título del recorrido",
    "date": "Fecha del recorrido", 
    "user": 111,
    "score": 1200
  }
```

* GET all users -> http://localhost:5000/users

* GET all streets -> http://localhost:5000/streets
