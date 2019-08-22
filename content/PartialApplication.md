
# Partial Application

Partial application, or "partial" (street slang) is simply a way to create a function that only has the partial number of arguments.  Seen another way, you can create a 
function that is missing some arguments.

Why on earth do you want to create a function that is MISSING arguments?  What good does it do?  How is creating a car that is missing wheels considered a Good Thing?

### Why?

* IOC
* dependency injection
* A partial application may or may not have a predictable return type.


```clojure

; In Clojure the function 'partial' returns a function that takes
; all additional arguments. 

user=> (def add42 (partial + 42))
#'user/add42
user=> (a) ; add nothing to 42
42
user=> (a 1)
43
user=> (a 1 2 3)
48
user=> (type a) ; is this a function?
clojure.core$partial$fn__4228
```


