# Increase stockNum

Increase the number of stock. Both `totalNum` and `stockNum` will be increased by `increment`. 

**URL** : `http://localhost:8081/blackfriday/stock`

**HTTP Method** : `PUT`

**Body Example** : 
```
{
    "id": 2,
    "increment": 10000,
    "name": "bbb"
}
```

**Notes** :
* `id` should exist ;
* `increment` should be positive ;
* `name` is optional.
