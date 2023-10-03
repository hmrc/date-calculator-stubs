
# date-calculator-stubs

This service runs on port `8763` by default. Run using `sbt run`. Tests can be run via `sbt test` and integration tests can
be run via `it/test`.

## GET /bank-holidays
This is a stub for the [GDS get bank holidays API](https://www.api.gov.uk/gds/bank-holidays/#bank-holidays). If
no predefined responses have been inserted by the [PUT /bank-holidays](#put-bank-holidays) a default response will be
returned with HTTP status 200 and response body:
```json
{                                           
  "england-and-wales": {                    
    "division": "england-and-wales",        
    "events": [                             
      {                                     
        "title": "Earliest bank holiday",   
        "date": "0000-01-01",               
        "notes": "",                        
        "bunting": true                     
      },                                    
      {                                     
        "title": "Latest bank holiday",     
        "date": "9999-12-31",          
        "notes": "",                        
        "bunting": true                     
      }                                     
    ]                                       
  },                                        
  "scotland": {                             
    "division": "scotland",                 
    "events": [                             
      {                                     
        "title": "Earliest bank holiday",   
        "date": "0000-01-01",               
        "notes": "",                        
        "bunting": true                     
      },                                    
      {                                     
        "title": "Latest bank holiday",     
         "date": "9999-12-31",         
         "notes": "",                       
         "bunting": true                    
      }                                     
    ]                                       
  },                                        
  "northern-ireland": {                     
    "division": "northern-ireland",         
    "events": [                             
      {                                     
        "title": "Earliest bank holiday",   
        "date": "0000-01-01",               
        "notes": "",                        
        "bunting": true                     
      },                                    
      {                                     
        "title": "Latest bank holiday",     
        "date": "9999-01-01",          
        "notes": "",                        
        "bunting": true                     
      }                                     
    ]                                       
  }                                         
}                                           
```

When calling this endpoint, ensure that a non-empty value is present in a 'From' header in the request otherwise a 400 response
will be returned.


## PUT /bank-holidays
The request body should take this form:
```json
{
  "status": 200,  // mandatory, some integer
  "body": ...     // optional, some JsValue 
}
```
This represents the response the [GET /bank-holiday](#get-bank-holidays) endpoint should return. For example, this
request body can be used to simulate a successful get bank holidays API call:
```json
{
  "status": 200,
  "body": { 
    "england-and-wales": {
      "division": "england-and-wales",
      "events": [ 
        {
          "title": "Test bank holiday 1",
          "date": "2023-03-19",
          "notes": "",
          "bunting": true
        }
      ]
    },
    "scotland": {
      "division": "scotland",
      "events": [
        {
          "title": "Test bank holiday 2",
          "date": "2023-03-11",
          "notes": "",
          "bunting": true
        },
        {
          "title": "Test bank holiday 3",
          "date": "2023-03-14",
          "notes": "",
          "bunting": false
        }
      ]
    },
    "northern-ireland": {
      "division": "northern-ireland",
      "events": [ ]
    }
  }
} 
```
and this request body could be used to simulate an error:
```json
{ "status": 500 }
```

There can be at most one predefined response at one time - any call to this endpoint will remove any predefined response
that was previously stored.


## DELETE /bank-holidays
This removes any predefined responses stored by [PUT /bank-holidays](#put-bank-holidays) making it so that [GET /bank-holiday](#get-bank-holidays) 
reverts to returning the default response described above.


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").