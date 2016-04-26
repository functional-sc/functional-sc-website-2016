(ns sc.functional.website.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [smeagol.contrib.util :as wiki]
            [sc.functional.website.page :as page]))

(defn validate-input "Only keep specific characters in the input string."
     [input-str]
     (let [re (re-pattern "[a-zA-Z0-9\\:\\/\\-\\_\\ \\.\\?\\!\\@]")]
       (apply str (filter #(re-matches re (str %1)) input-str))))

(defn process-wiki-or-404
  "determine and dispatch on wiki topic, or it's a 404"
  [request]
  (let [topic    (validate-input (apply str (rest (:uri request)))) ; scrub
        wikipage (wiki/fetch-article topic)]
    (cond (empty? (:html wikipage)) "404 page not found"
          :else                     (page/article-page wikipage))) )

(defroutes app-routes
  ;;(GET "/" [] "Hello World")
  (GET "/" [] (:html (wiki/fetch-article "Introduction")))
  (GET "/*" {params :params :as request} (process-wiki-or-404 request))
  (route/not-found "Not Found"))

(def app
  ;; see https://github.com/ring-clojure/ring-defaults
  (wrap-defaults app-routes api-defaults) ; turn everything off
  )
