(ns sc.functional.website.core
  (:require [compojure.core             :as www]
            [ring.adapter.jetty         :as ring]
            [clojure.tools.nrepl.server :as nrepl]
            [sc.functional.website.handler]
            )
  (:gen-class)) ; required for standalone

(defn -main
  "For use in standalone operation." []
  (future (nrepl/start-server :port 4006)) ; bake-in debugging, always
  (ring/run-jetty #'sc.functional.website.handler/app {:port 8080 :join? false}))
