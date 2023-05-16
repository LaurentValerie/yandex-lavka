# Yandex Lavka
## About the Service
This is a project from the Yandex backend school.

The application is a multithreaded REST API service that allows you to save and retrieve information about couriers and orders.

The server allows you to add order completion information.

You can also get information about couriers' ratings and earnings.

_Distribution of orders by couriers has not **yet** been implemented._

A bucket4j rate limiter with a limit of 10 RPS is used for each endpoint.

Postgresql 15.2 database is used to store data. You can also use any other relational database.
## How to Run
### With docker compose

* An installed docker and docker-compose is required.
* Run `docker-compose up` from the root of the project
* You will get a working service on port 8080

### With docker and separate db
* An installed docker is required.
* Set your db credentials in Dockerfile
* Assemble the image using the command
  `docker build -t <image_name> <path_to_Dockerfile>`
* After successfully assembling the image, you can launch the container using
  `docker run <image_name>`.
* You will get a working service on port 8080.

## How to work with the API
A detailed description of the API can be found in openapi.json
## Allowed endpoints
### POST /couriers
Accepts a list in json format with data about couriers and their work schedule.

Couriers work only in pre-defined areas, and also differ by type: pedestrian, bicycle courier and
courier by car. The volume of orders carried by the courier depends on the type.
Districts are given as positive integers. The work schedule is set by a list of strings in the format `HH:MM-HH:MM`.
### GET /couriers/{courier_id}
Returns information about the courier.

### GET /couriers

Returns information about all couriers.

The method has parameters `offset` and `limit` to provide paginated output (by default `offset = 0`, `limit = 1`).
### POST /orders

Accepts a list with order data in json format as input. The order displays the characteristics — weight, area,
delivery time and price.
Delivery time is a string in the format `HH:MM-HH:MM`.

### GET /orders/{order_id}

Returns information about the order by its identifier, as well as additional information: the weight of the order, the delivery area,
the time intervals at which it is convenient to accept the order.

### GET /orders

Returns information about all orders, as well as their additional information: the weight of the order, the delivery area, 
the time intervals at which it is convenient to accept the order.
The method has parameters `offset` and `limit` to provide paginated output (by default `offset = 0`, `limit = 1`).

### POST /orders/complete

Accepts an array of objects consisting of three fields: courier id, order id and order completion time, then notes that the order is completed.

### GET /couriers/meta-info/{courier_id}
Returns the money earned by the courier for orders and his rating, taking into account the type of courier.