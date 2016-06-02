(ns sc.functional.website.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [smeagol.contrib.util :as wiki]
            [sc.functional.website.page :as page]
            [clojure.java.shell :as shell]
            [clojure.tools.nrepl.server :as nrepl]
            ))



(defn validate-input "Only keep specific characters in the input string."
     [input-str]
     (let [re (re-pattern "[a-zA-Z0-9\\:\\/\\-\\_\\ \\.\\?\\!\\@]")]
       (apply str (filter #(re-matches re (str %1)) input-str))))

;;
;; form handling
;;

(defn map->mailstr
  "Given a parameter map, stringify it all parameters formatted as a mail form.  We used to call this FormMail and it's not used for a reason."
  [params]
  (apply str
         (interpose "\n"
              (map #(str (first %1) ": " (validate-input (second %1))) params))))

(defn mail-generic [params email-target subject thanks-target]
  (if (empty? (params "jobtitle")) ; botbait
    (let [msg (map->mailstr params)
          cmd (str "/bin/echo '" msg "' | /usr/bin/mail -s \"" subject "\" " email-target)]
      (shell/sh "/bin/sh" "-c" cmd)
      (str "<html><meta http-equiv=\"REFRESH\" content=\"0;url=/" thanks-target "\"></HEAD></html>") )))

(defn mail-connect [params]
  (mail-generic params "heow@alphageeksinc.com" "functional sc comment" "connect-thanks"))

(defn process-wiki-or-404
  "determine and dispatch on wiki topic, or it's a 404"
  [request]
  (let [topic    (validate-input (apply str (rest (:uri request)))) ; scrub
        wikipage (wiki/fetch-article topic)]
    (cond (empty? (:html wikipage)) "404 page not found"
          :else                     (page/article-page wikipage))) )

(defroutes app-routes
  (GET "/" [] (page/home-page))

  ;; article images are in the content diretory and are same basefilename
  (route/files     "/content/" {:root "./content"})

  ;; publish all files in src/html as if they were root
  (route/files     "/" {:root "./src/html"}) ; standalone from root dir
  (route/resources "/" {:root "./html"})      ; war file from classes dir

  (GET "/debug" [] (fn [x] (future (nrepl/start-server :port 4006)) "NREPL debugging started on localhost, jack-in to :4006 kind sir."))

  ;; form processing
  (POST "/connect" {params :params} (mail-connect params))

  ;; last chance, perhaps there is an article
  (GET "/*" {params :params :as request} (process-wiki-or-404 request))
  (route/not-found "Not Found")
  )

(def app
  ;; see https://github.com/ring-clojure/ring-defaults
  (wrap-defaults app-routes api-defaults) ; turn everything off
  )
