(ns sc.functional.website.page
  (:require [net.cgrand.enlive-html       :as enlive]
            [smeagol.contrib.util         :as wiki]
            [sc.functional.website.meetup :as meetup]
            [clj-time.core                :as time]
            [clj-time.format              :as tformat]
            [clj-time.local               :as tlocal]
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

(defn make-saying []
  (rand-nth ["A better way to write software"
             "Modern programming in practice"
             "Elegant weapons for a civilized age"
             "The best of Charleston technology"
             "Creating amazing things"
             "Think different"
             ]))

(defn wrap-mtg-url [s m] (str "<a target=\"_blank\" href=\"" (str (:event-url m)) "\">" s "</a>") )

(defn take-first [s]
  (apply str (rest (rest (rest (first (clojure.string/split s #"</p>")))))))

(defn take-first-h [s]
  (str "<p>" (apply str (rest (rest (rest (first (clojure.string/split s #"</p>"))))))"</p>") )

(defn template-home []
  (let [wiki-article (wiki/fetch-article "FunctionalThinking")
        a2           (wiki/fetch-article "SoYouWantToLearnJava")
        a3           (wiki/fetch-article "ParsingTextwithaVirtualMachine")
        a4           (wiki/fetch-article "DontFearTheMonad")
        a5           (wiki/fetch-article "ThinkDifferent")
        meeting      (first (meetup/fetch-upcoming-meetups))
        ]
      (enlive/template
   "html/home.html" []
   [:title]                         (enlive/content (str "Functional SC: Functional Programming in the Silicon Harbor"))
   [:h4 :span.litertxt]             (enlive/content (make-saying))

   ;; TODO unfuck this
   ;; article1
   [:div.article1.col-md-4.col-xs-12.paras :a] (enlive/set-attr :href (:title wiki-article))
   [:div.article1.col-md-4.col-xs-12.paras :a :img.img-responsive] (enlive/set-attr :src (str "content/" (:title wiki-article) ".jpg"))
   [:div.article1.col-md-4.col-xs-12.paras :a :span.date :strong]  (enlive/content (first (:date (:metadata wiki-article))))
   [:div.article1.col-md-4.col-xs-12.paras :h5 :b]                 (enlive/content (make-title wiki-article))
   [:div.article1.col-md-4.col-xs-12.paras :p.date]  (enlive/html-content (take-first (:html wiki-article)))

   ;; article2
   [:div.article2.col-md-4.col-xs-12.paras :a] (enlive/set-attr :href (:title a2))
   [:div.article2.col-md-4.col-xs-12.paras :a :img.img-responsive] (enlive/set-attr :src (str "content/" (:title a2) ".jpg"))
   [:div.article2.col-md-4.col-xs-12.paras :a :span.date :strong]  (enlive/content (first (:date (:metadata a2))))
   [:div.article2.col-md-4.col-xs-12.paras :h5 :b]                 (enlive/content (make-title a2))
   [:div.article2.col-md-4.col-xs-12.paras :p.date]  (enlive/html-content (take-first (:html a2)))

   ;; article3
   [:div.article3.col-md-4.col-xs-12.paras :a] (enlive/set-attr :href (:title a3))
   [:div.article3.col-md-4.col-xs-12.paras :a :img.img-responsive] (enlive/set-attr :src (str "content/" (:title a3) ".jpg"))
   [:div.article3.col-md-4.col-xs-12.paras :a :span.date :strong]  (enlive/content (first (:date (:metadata a3))))
   [:div.article3.col-md-4.col-xs-12.paras :h5 :b]                 (enlive/content (make-title a3))
   [:div.article3.col-md-4.col-xs-12.paras :p.date]  (enlive/html-content (take-first (:html a3)))

   ;; article4
   [:div.article4.col-md-4.col-xs-12.paras :a] (enlive/set-attr :href (:title a4))
   [:div.article4.col-md-4.col-xs-12.paras :a :img.img-responsive] (enlive/set-attr :src (str "content/" (:title a4) ".jpg"))
   [:div.article4.col-md-4.col-xs-12.paras :a :span.date :strong]  (enlive/content (first (:date (:metadata a4))))
   [:div.article4.col-md-4.col-xs-12.paras :h5 :b]                 (enlive/content (make-title a4))
   [:div.article4.col-md-4.col-xs-12.paras :p.date]  (enlive/html-content (take-first (:html a4)))

   ;; article5
   [:div.article5.col-md-4.col-xs-12.paras :a] (enlive/set-attr :href (:title a5))
   [:div.article5.col-md-4.col-xs-12.paras :a :img.img-responsive] (enlive/set-attr :src (str "content/" (:title a5) ".jpg"))
   [:div.article5.col-md-4.col-xs-12.paras :a :span.date :strong]  (enlive/content (first (:date (:metadata a5))))
   [:div.article5.col-md-4.col-xs-12.paras :h5 :b]                 (enlive/content (make-title a5))
   [:div.article5.col-md-4.col-xs-12.paras :p.date]  (enlive/html-content (take-first (:html a5)))

   ;; upcoming meeting
   [:div.banner3 :div.container :div.row.features]  (enlive/html-content (wrap-mtg-url (str "Join us " (tformat/unparse (.withZone (tformat/formatter "EEEE, MMMM d, h:mm a") (time/time-zone-for-id "America/New_York")) (:time meeting))) meeting))
   [:div.banner3 :div.container :div.row.bck]       (enlive/html-content (wrap-mtg-url (:title meeting) meeting))
   [:div.banner3 :div.container :div.row.handcraft] (enlive/html-content (str (str "<p>" (wrap-mtg-url (take-first (:description meeting)) meeting) "</p>")) "<p>" (wrap-mtg-url "read more..." meeting) "</p>")
   )
    )
)

(defn home-page []
  ;;(slurp "src/html/home.html")
  ((template-home)))

(defn article-page [wiki-article]
  ;; is it really an article or just content page?  article will have a date
  (cond
   (nil? (first (:date (:metadata wiki-article)))) ((template-content wiki-article))
   :else                                           ((template-article wiki-article))
   )
  )