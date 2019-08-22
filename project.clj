(defproject sc.functional.website "1.0.03"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.memoize "0.5.8"]
                 [compojure "1.5.0"]
                 [ring/ring-defaults "0.2.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [smeagol.contrib.util "0.9.2"]
                 [hiccup "1.0.5"]
                 [clj-time "0.11.0"]
                 [enlive "1.1.6"]
                 [org.clojure/tools.nrepl "0.2.11"] ; bake-in debugging
                 [png-extract "1.0.4"] ; ads
                 ]
  :plugins [[lein-ring "0.9.7"]
            [lein-uberwar "0.2.0"]]
  :profiles
  {:dev
   {:dependencies [[javax.servlet/servlet-api "2.5"]
                   [ring/ring-mock "0.3.0"]
                   ]}}
  :ring {:handler sc.functional.website.handler/app} ; required for war
  :main sc.functional.website.core ; starting point for standalone
  )
