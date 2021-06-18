# Batch Insert Orders record

**URL** : `http://localhost:8081/blackfriday/order`

**HTTP Method** : `POST`

**Body Example** :
```json
[{
	"userId": 1,
	"productId": 1,
	"itemsNum": 1
 },
 {
	"userId": 2,
	"productId": 1,
	"itemsNum": 1
}]
```

**Note** : 
* `id` will be generated automatically ;
* `productId` refers to Stock's `id`, and it should exist ;
* `itemsNum` should be positive.
