# Bank-REST-API
### INSTRUCTIONS:
To build, test and generate the jar with Maven, run from 
the root directory of the project:
```
mvn package
```

To test, run from the root directory of the project:
```
mvn test
```

To start the server, run the jar with this command 
from the root directory of the project:
```
java -jar target/Bank-REST-API-1.0-SNAPSHOT.jar server configuration.yml
``` 

The jar will be generated at the path `target/Bank-REST-API-1.0-SNAPSHOT.jar`.

### Server
   
   The Application starts a server on localhost port 8080.
   

### Available Services

| HTTP METHOD | PATH | USAGE |
| -----------| ------ | ------ |
| POST | /api/accounts/create?id={id}&name={name}&balance={balance} | create a new account
| GET | /api/accounts/{accountId} | get account by accountId | 
| POST | /api/transactions/create?from={senderAccountId}&to={recipientAccountId}&amount={amount} | perform transaction between 2 accounts | 
| GET | /api/transactions/{transactionId} | get transaction by id | 
| GET | /api/transactions/account/?id={accountId} | get all the transactions of an account | 
 
### Http Status
- 200 OK
- 400 Bad Request 
- 404 Not Found
 
##### POST Request: http://localhost:8080/api/accounts/create?id=1&name=Hang&balance=100
##### Response: 200 Status.OK
##### GET Request: http://localhost:8080/api/accounts/1
##### Response:
```sh
{
    "id": 1,
    "name": "Hang",
    "balance": 50.0,
    "lock": {}
}
```
##### POST Request: http://localhost:8080/api/transactions/create?from=1&to=2&amount=100
##### Response: (transactionId)
```sh
1
```

##### GET Request: http://localhost:8080/api/transactions/1
##### Response:
```sh
{
    "id": 1,
    "sourceAccountId": 2,
    "destinationAccountId": 1,
    "amount": 50.0
}
```

##### GET Request: http://localhost:8080/api/transactions/account?id=1
##### Response:
```sh
[
    {
        "id": 2,
        "sourceAccountId": 1,
        "destinationAccountId": 2,
        "amount": 100.0
    }
]
```




