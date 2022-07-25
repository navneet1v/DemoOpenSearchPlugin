## Dev Testing

### Follow the below steps:

#### Mac:

1. Fork the [OpenSearch Repo](https://github.com/opensearch-project/OpenSearch) into your github account.
2. Checkout the OpenSearch Repo in your laptop using right github commands.
3. Run the below commands, as provided in the repo.
   ```$> cd OpenSearch && ./gradlew assemble && ./gradlew localDistro```
4. Now checkout this plugin repo by forking and then checking out this repo or directly checking out this repo.
5. Run this command create an OpenSearch cluster with this plugin
   ```cd DemoOpenSearchPlugin && ./gradlew run -PcustomDistributionUrl="<Path_to_OpenSearchRepo>/OpenSearch/distribution/archives/darwin-tar/build/distributions/opensearch-min-3.0.0-SNAPSHOT-darwin-x64.tar.gz"```

#### For Other Linux

Follow step 4 above and then run the command ```cd DemoOpenSearchPlugin && ./gradlew run```

### Debugging

While running the ```run``` command add ```--debug-jvm``` as an argument.

### Checking Cluster run

The cluster will be setup on the port `9200` url : `http://localhost`. Run the below command to check the plugin is
present or not.
```http://localhost:9200/_cat/plugins```

### Sample inputs/Outputs
#### Validate Plugin is running
**Input**

```
GET _plugins/hello_world
```

**Output**

```Hi! Your plugin is installed and working:)```

#### Testing Prime Number Count Aggregation

**Upload Input Data**

```
PUT _bulk

{ "index" : { "_index" : "prime_count_index", "_id": "2"} }
{"myIntegr" : 23}
{ "index" : { "_index" : "prime_count_index", "_id": "1"} }
{"myIntegr" : 24}
{ "index" : { "_index" : "prime_count_index", "_id": "3"} }
{"myIntegr" : 2}
{ "index" : { "_index" : "prime_count_index", "_id": "4"} }
{"myIntegr" : 3}

```

**Aggregation Input**
```
POST prime_count_index/_search?size=0

{
  "aggs": {
    "my_prime_count": { "primeCount": { "field": "myIntegr" } }
  }
}

```

**Output**
```
{
    "took": 87,
    "timed_out": false,
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 4,
            "relation": "eq"
        },
        "max_score": null,
        "hits": []
    },
    "aggregations": {
        "my_prime_count": {
            "value": 3
        }
    }
}
```
