# API_RankCity
API REST para RackCity app


### Requests

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
