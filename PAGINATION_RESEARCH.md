# Pagination

Paging requires an implied ordering. 
By default this may be the item’s unique identifier, 
but can be other ordered fields such as a created date.

## Offset Pagination
This is the simplest form of paging. 
Limit/Offset became popular with apps using SQL databases which already have 
`LIMIT` and `OFFSET` as part of the SQL `SELECT` Syntax. 
Very little business logic is required to implement Limit/Offset paging.

Limit/Offset Paging would look like
 
```
GET /products?limit=20&offset=100.
``` 

This query would return the 20 rows starting with the 100th row.

### Example
(Assume the query is ordered by price descending)

Client makes request for most expensive products: `GET /products?limit=20`
On scroll/next page, client makes second request `GET /products?limit=20&offset=20`
On scroll/next page, client makes third request `GET /products?limit=20&offset=40`
As a SQL statement, the third request would look like:

```roomsql
SELECT *
FROM products
ORDER BY priceInCents
LIMIT 20
OFFSET 40;
```

### Benefits
* Easiest to implement, almost no coding required other than passing parameters directly to SQL query.
* Stateless on the server.
* Works regardless of custom sort_by parameters.

### Downsides
* Not performant for large offset values. 
Let’s say you perform a query with a large offset value of 1.000.000. 
The database needs to scan and count rows starting with 0, and will skip (i.e. throw away data) 
for the first 1.000.000 rows.

* Not consistent when new items are inserted to the table (i.e. Page drift) 
This is especially noticeable when we are ordering items by newest first. 
Consider the following that orders by decreasing Id: 
    1. Query `GET /products?offset=0&limit=15`
    2. 10 new products added to the table
    3. Query `GET /products?offset=15&limit=15` 
    
    The second query will only return 5 new products, 
    as adding 10 new products moved the offset back by 10 products. 
    To fix this, the client would really need to offset by 25 for the second query 
    `GET /products?offset=25&limit=15`, 
    but the client couldn’t possibly know other objects being inserted into the table.
    
Even with limitations, offset paging is easy to implement and understand and can be used in applications 
where the data set has a small upper bounds.

## Keyset Pagination
Keyset pagination uses the filter values of the last page to fetch the next set of items. 
Those columns would be indexed.

### Example
(Assume the query is ordered by price descending)

Client makes request for most expensive products: `GET /productss?limit=20`
On scroll/next page, client finds the minimum price of 1000 from previously returned results. 
and then makes second query using price as a filter: `GET /products?limit=20&priceInCents:lte:1000`
On scroll/next page, client finds the minimum price of 500 from previously returned results. 
and then makes third query using price as a filter: `GET /iproductss?limit=20&priceInCents:lte:5000

```roomsql
SELECT *
FROM products
WHERE priceInCents <= 500
ORDER BY priceInCents DESC
LIMIT 20
```

### Benefits

* Works with existing filters without additional backend logic. 
Only need an additional limit URL parameter.
* Consistent ordering even when newer items are inserted into the table. Works well when sorting by most recent first.
* Consistent performance even with large offsets.

### Downsides
* Tight coupling of paging mechanism to filters and sorting. 
Forces API users to add filters even if no filters are intended.
* Does not work for low cardinality fields such as enum strings.
* Complicated for API users when using custom sort_by fields as 
the client needs to adjust the filter based on the field used for sorting.

Keyset pagination can work very well for data with a single natural high cardinality key 
such as time series or log data which can use a timestamp.

## Seek Pagination
Seek Paging is an extension of Keyset paging. 
By adding an after_id or start_id URL parameter, we can remove the tight coupling of paging to filters and sorting. 
Since unique identifiers are naturally high cardinality, we won’t run into issues unlike if sorting by a low 
cardinality field like state enums or category name.

The problem with seek based pagination is it’s hard to implement when a custom sort order is needed.

### Example
(Assume the query is ordered by price descending)

Client makes request for most expensive products: `GET /products?limit=20`
On scroll/next page, client finds the last id of ‘20’ from previously returned results. 
And then makes second query using it as the starting id: `GET /products?limit=20&after_id=20`
On scroll/next page, client finds the last id of ‘40’ from previously returned results. 
And then makes third query using it as the starting id: `GET /products?limit=20&after_id=40`
Seek pagination can be distilled into a where clause

```roomsql
SELECT *
FROM products
WHERE id > 20
LIMIT 20
```

The above example works fine if ordering is done by id, but what if we want to sort by a title field? 
For each request, the backend needs to first obtain the title value for the item who’s identifier matches the after_id. 
Then, a second query is performed using that value as a where filter.

Let’s consider the query `GET /products?limit=20&after_id=20&sort_by=title`, the backend would need two queries. 
The first query could be O(1) lookup with hash tables though to get the title pivot value. 
This is fed into the second query to only retrieve items whose title is after our after_title. 
We sort by both columns, title and id to ensure we have a stable sort in case two titles are the same. 
This is critical for lower cardinality fields.

1. 
```roomsql
SELECT title AS AFTER_TITLE
FROM products
WHERE id = 20
```

2. 
```roomsql
SELECT *
FROM products
WHERE title >= [AFTER_TITLE]
ORDER BY title, id
LIMIT 20
```

### Benefits
* No coupling of pagination logic to filter logic.
* Consistent ordering even when newer items are inserted into the table. 
Works well when sorting by biggest first.
* Consistent performance even with large offsets.

### Downsides
* More complex for backend to implement relative to offset based or keyset based pagination
* If items are deleted from the database, the start_id may not be a valid id.

Seek paging is a good overall paging strategy though it requires a little more work on the backend, 
but ensures there isn’t additional complexity added to clients/users of the API while staying performant 
even with larger seeks.