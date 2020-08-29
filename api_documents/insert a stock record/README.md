# Insert a Stock record

**URL** : `http://localhost:8080/blackfriday/stock`

**HTTP Method** : `POST`

**Body Example** :
```
{
	"totalNum": 20000,
	"stockNum": 20000
}
```

**Notes** : 
* `id` will be generated automatically ;
* `totalNum` should be larger than 0 ;
* `stockNum` should be larger than 0 ;
* `totalNum` should be equal to `stockNum`.
