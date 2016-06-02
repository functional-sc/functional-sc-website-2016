(ns sc.functional.website.page
  (:require [net.cgrand.enlive-html       :as enlive]
            [smeagol.contrib.util         :as wiki]
            [sc.functional.website.meetup :as meetup]
            [clj-time.core                :as time]
            [clj-time.format              :as tformat]
            [clj-time.local               :as tlocal]
            )
  (:import [extract.PNGExtractText]))

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
             "Elegant weapons for a more civilized age"
             "Charleston, South Carolina"
             "Create amazing things"
             "Think different"
             ]))

(defn make-ad
  "Randomly select a PNG file from the ad directory, comment is url"
  []
  (let [adpath "/images/partners/"
        file   (rand-nth
                (filter #(.endsWith (.getName %) ".png")
                        (file-seq (clojure.java.io/file (clojure.java.io/resource (str "./html" adpath))))))
        path   (str adpath (.getName file)) 
        url    (extract.PNGExtractText/getComment
                (new java.io.FileInputStream file))]
    {:path path
     :url  url
     } ))

(defn wrap-mtg-url [s m] (str "<a target=\"_blank\" href=\"" (str (:event-url m)) "\">" s "</a>") )

(defn take-first [s]
  (apply str (rest (rest (rest (first (clojure.string/split s #"</p>")))))))

(defn take-first-h [s]
  (str "<p>" (apply str (rest (rest (rest (first (clojure.string/split s #"</p>"))))))"</p>") )

(defn template-home []
  (let [wiki-article (wiki/fetch-article "BareMetalFunctionalProgrammingWithSymbolics")
        a2           (wiki/fetch-article "FunctionalThinking")
        a3           (wiki/fetch-article "SoYouWantToLearnJava")
        a4           (wiki/fetch-article "ParsingTextwithaVirtualMachine")
        a5           (wiki/fetch-article "DontFearTheMonad")
        a6           (wiki/fetch-article "ThinkDifferent")
        meeting      (meetup/fetch-current-meetup)
        people       (shuffle (meetup/fetch-cached-members))
        p1           (first people)
        p2           (second people)
        p3           (last people)
        ad           (make-ad)
        ]
      (enlive/template
   "html/home.html" []
   [:title]                         (enlive/content (str "Functional SC: Functional Programming in the Silicon Harbor"))
   [:h4 :span.litertxt]             (enlive/content (make-saying))

   ;; meh, is unfucking this isn't worth the effort from inside this macro?
   [:span.person1.col-sm-4 :span.name]          (enlive/content (first (clojure.string/split (:name p1) #"\s")))
   [:span.person1.col-sm-4 :img.img-responsive] (enlive/set-attr :src (:photo p1))   
   [:span.person1.col-sm-4 :span.hometown]      (enlive/content (if (empty? (:hometown p1)) "" "Hometown:"))
   [:span.person1.col-sm-4 :span.lite_blue]     (enlive/content (:hometown p1))
   [:span.person1.col-sm-4 :span.fiftynine]     (enlive/content (tformat/unparse (.withZone (tformat/formatter "MMMM d, YYYY") (time/time-zone-for-id "America/New_York")) (:joined-date p1)) )

   [:span.person2.col-sm-4 :span.name]          (enlive/content (first (clojure.string/split (:name p2) #"\s")))
   [:span.person2.col-sm-4 :img.img-responsive] (enlive/set-attr :src (:photo p2))      
   [:span.person2.col-sm-4 :span.hometown]      (enlive/content (if (empty? (:hometown p2)) "" "Hometown:"))
   [:span.person2.col-sm-4 :span.lite_blue]     (enlive/content (:hometown p2))
   [:span.person2.col-sm-4 :span.fiftynine]     (enlive/content (tformat/unparse (.withZone (tformat/formatter "MMMM d, YYYY") (time/time-zone-for-id "America/New_York")) (:joined-date p2)) )

   [:span.person3.col-sm-4 :span.name]          (enlive/content (first (clojure.string/split (:name p3) #"\s")))
   [:span.person3.col-sm-4 :img.img-responsive] (enlive/set-attr :src (:photo p3))         
   [:span.person3.col-sm-4 :span.hometown]      (enlive/content (if (empty? (:hometown p3)) "" "Hometown:"))
   [:span.person3.col-sm-4 :span.lite_blue]     (enlive/content (:hometown p3))
   [:span.person3.col-sm-4 :span.fiftynine]     (enlive/content (tformat/unparse (.withZone (tformat/formatter "MMMM d, YYYY") (time/time-zone-for-id "America/New_York")) (:joined-date p3)) )
   
   ;; TODO: is this code or a DSL?  Yeah it's long but tearing it out would
   ;; make is less readable.  The biggest issue is that modifying requires
   ;; 10 identical edits, which is only going to happen 1 more time after
   ;; we get more than 6 articles.  Still, it's ugly.
   
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

   ;; article6
   [:div.article6.col-md-4.col-xs-12.paras :a] (enlive/set-attr :href (:title a6))
   [:div.article6.col-md-4.col-xs-12.paras :a :img.img-responsive] (enlive/set-attr :src (str "content/" (:title a6) ".jpg"))
   [:div.article6.col-md-4.col-xs-12.paras :a :span.date :strong]  (enlive/content (first (:date (:metadata a6))))
   [:div.article6.col-md-4.col-xs-12.paras :h5 :b]                 (enlive/content (make-title a6))
   [:div.article6.col-md-4.col-xs-12.paras :p.date]  (enlive/html-content (take-first (:html a6)))
   
   ;; upcoming meeting
   [:div.banner3 :div.container :div.row.features]  (enlive/html-content (wrap-mtg-url (str "Join us " (tformat/unparse (.withZone (tformat/formatter "EEEE, MMMM d, h:mm a") (time/time-zone-for-id "America/New_York")) (:time meeting))) meeting))
   [:div.banner3 :div.container :div.row.bck]       (enlive/html-content (wrap-mtg-url (:title meeting) meeting))
   [:div.banner3 :div.container :div.row.handcraft] (enlive/html-content (str (str "<p>" (wrap-mtg-url (take-first (:description meeting)) meeting) "</p>")) "<p>" (wrap-mtg-url "read more..." meeting) "</p>")


   ;; ads
   [:a#partner]   (enlive/set-attr :href (:url ad))
   [:img#partner] (enlive/set-attr :src (:path ad)) 
   )))

(defn home-page []
  ((template-home)))

(defn article-page [wiki-article]
  ;; is it really an article or just content page?  article will have a date
  (cond
   (nil? (first (:date (:metadata wiki-article)))) ((template-content wiki-article))
   :else                                           ((template-article wiki-article))
   )
  )