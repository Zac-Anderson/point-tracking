# Point Tracking #

## Table of Contents ##
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Workspace Setup](#workspace-setup)
    - [Running Tests](#running-tests)
    - [Starting the Server](#starting-the-server)
- [Calling the API](#calling-the-api)

## Getting Started ##
Follow the steps below to get setup.

### Prerequisites ###
- Java 11
- Gradle

```
brew install gradle
```

### Workspace Setup ###
```
cd ~/workspace
git clone git@github.com:Zac-Anderson/point-tracking.git
```

### Running Tests ###
```
cd ~/workspace/point-tracking
./scripts/test
```

### Starting the Server ###
```
cd ~/workspace/point-tracking
./scripts/dev-server
```

## Calling the API ##
The server port is defaulted to 8080. The endpoints work as follows:

```
GET localhost:8080/api/v1/user
```
This returns the User itself with an overall Point Balance



```
POST localhost:8080/api/v1/user/add

BODY
[
	{"payer": "UNILEVER", "points": 200, "transaction_date": "2021-01-06T12:00:00"},
	{"payer": "DANNON", "points": 100, "transaction_date": "2018-01-11T13:00:00"},
	{"payer": "OTHER", "points": 100, "transaction_date": "2020-01-11T13:00:00"},
	{"payer": "UNILEVER", "points": 200, "transaction_date": "2021-01-07T12:00:00"},
	{"payer": "DANNON", "points": -100, "transaction_date": "2021-01-08T13:00:00"},
	{"payer": "OTHER", "points": 100, "transaction_date": "2021-01-09T13:00:00"},
	{"payer": "DANNON", "points": 200, "transaction_date": "2021-01-10T12:00:00"}
]
```
This adds points to the User



```
GET localhost:8080/api/v1/user/balance
```
This returns just the balance of the user



```
PUT localhost:8080/api/v1/user/deduct?points=500
```
This deducts points taking from the oldest points first
