(ns sc.functional.website.page
  (:require [net.cgrand.enlive-html :as enlive]
            [smeagol.contrib.util :as wiki]
   ))

(defn camel-caseify
     "converts hi-there to HiThere"
     [cc]
     (apply str (map clojure.string/capitalize (clojure.string/split cc #"-"))) )

(defn make-title "metadata overrides filename" [a]
  (str (if-let [meta-title (first (:title (:metadata a)))]
              meta-title
              (:title a) )))

(defn template-content [wiki-article]
  (let [title (make-title wiki-article)]
    (enlive/template
     "html/article.html" []
     [:title]                         (enlive/content (str "Functional SC: " title))
     [:span.col-md-5.col-xs-12.barhd] (enlive/content title)

     ;; get rid of article-specifc things
     [:div.arcontent :div.col-md-10.col-xs-11]  (enlive/content "") ; tagline
     [:div.col-md-10.col-xs-12 :img.img-responsive] (enlive/remove-attr :src) ; image
     [:div.arcontent :div.col-md-1.col-xs-1] (enlive/content "")

     ;; actual content
     [:div.arcontentcopy]             (enlive/html-content (:html wiki-article))
     )))

(defn template-article [wiki-article]
  (let [title (make-title wiki-article)]
    (enlive/template
     "html/article.html" []
     [:title]                         (enlive/content (str "Functional SC: " title))
     [:span.col-md-5.col-xs-12.barhd] (enlive/content title)
     [:div.article]                   (enlive/content title)
     [:span.pubdate]                  (enlive/content (first (:date (:metadata wiki-article))))
     [:div.col-md-10.col-xs-12 :img.img-responsive] (enlive/set-attr :src (str "content/" (:title wiki-article) ".jpg"))
     [:span.pubtag]                   (enlive/content (apply str (interpose ", " (:tag (:metadata wiki-article)))))
     [:div.arcontentcopy]             (enlive/html-content (:html wiki-article))
     )))

(defn home-page []
  (slurp "src/html/home.html")
  )

(defn article-page [wiki-article]
  ;; is it really an article or just content page?  article will have a date
  (cond
   (nil? (first (:date (:metadata wiki-article)))) ((template-content wiki-article))
   :else                                           ((template-article wiki-article))
   )
  )