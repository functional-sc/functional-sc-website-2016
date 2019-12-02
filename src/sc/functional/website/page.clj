(ns sc.functional.website.page
  (:require [net.cgrand.enlive-html       :as enlive]
            [smeagol.contrib.util         :as wiki]
            [sc.functional.website.meetup :as meetup]
            [clj-time.core                :as time]
            [clj-time.format              :as tformat]
            [clj-time.local               :as tlocal]
            )
  (:import [extract.PNGExtractText]))

(defn make-title "metadata overrides filename" [a]
  (str (if-let [meta-title (first (:title (:metadata a)))]
              meta-title
              (:basename a) )))

(defn make-saying []
  (rand-nth ["A better way to write software"
             "Modern programming in practice"
             "Elegant weapons for a more civilized age"
             "Charleston, South Carolina"
             "Create amazing things"
             "Think different"
             "Rocking the Boat of the Silicon Harbor"
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

(defn list-articles
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

(defn template-content [wiki-article]
  (let [title (make-title wiki-article)
        ad    (make-ad)]
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

     [:a#partner]   (enlive/set-attr :href (:url ad)) ;; ads
     [:img#partner] (enlive/set-attr :src (:path ad)) ;; ads
     )))

(defn template-article [wiki-article]
  (let [title (make-title wiki-article)
        ad    (make-ad)]
    (enlive/template
     "html/article.html" []
     [:title]                         (enlive/content (str "Functional SC: " title))
     [:span.col-md-5.col-xs-12.barhd] (enlive/content title)
     [:div.article]                   (enlive/content title)
     [:span.pubdate]                  (enlive/content (first (:date (:metadata wiki-article))))
     [:div.col-md-10.col-xs-12 :img.img-responsive] (enlive/set-attr :src (str "content/" (:basename wiki-article) ".jpg"))  ;; TODO camelcase correctly
     [:span.pubtag]                   (enlive/content (apply str (interpose ", " (:tag (:metadata wiki-article)))))
     [:div.arcontentcopy]             (enlive/html-content (:html wiki-article))
     [:a#partner]   (enlive/set-attr :href (:url ad)) ;; ads
     [:img#partner] (enlive/set-attr :src (:path ad)) ;; ads
     )))

(defn wrap-mtg-url [s m] (str "<a target=\"_blank\" href=\"" (str (first (:event-url (:metadata m)))) "\">" s "</a>") )

(defn take-first [s]
  (apply str (rest (rest (rest (first (clojure.string/split s #"</p>")))))))

(defn take-first-h [s]
  (str "<p>" (apply str (rest (rest (rest (first (clojure.string/split s #"</p>"))))))"</p>") )

(defn template-home []
  (let [pinned-article (rand-nth (wiki/fetch-articles (fn [x] (not (nil? (:pinned (:metadata x)))))))
        articles     (shuffle (wiki/fetch-articles (fn [x] (and (not (nil? (:date (:metadata x))))
                                                                (nil? (:pinned (:metadata x)))) )))
        a2           (nth articles 0)
        a3           (nth articles 1)
        a4           (nth articles 2)
        a5           (nth articles 3)
        a6           (nth articles 4)
        meeting      (wiki/fetch-article "NextMeeting")
;        meeting      {:event-url "https://www.meetup.com/Functional-SC/events/264112201/"
;                      :title     "LLLLambda Calculus"
;                      :description "<p>Lambda Calculus isn’t just a fancy word: it defines our software, our numbers and perhaps even reality itself."}  ;;(meetup/fetch-current-meetup)
;;        people       (shuffle (meetup/fetch-cached-members))
;;        p1           (first people)
;;        p2           (second people)
;;        p3           (last people) 
        ad           (make-ad)
        ]
      (enlive/template
   "html/home.html" []
   [:title]                         (enlive/content (str "Functional SC: Functional Programming in the Silicon Harbor"))
   [:h4 :span.litertxt]             (enlive/content (make-saying))

   ;; meh, unfucking this isn't worth the effort
   [:span.person1.col-sm-4 :span.name]          (enlive/content "Joan")
   [:span.person1.col-sm-4 :img.img-responsive] (enlive/set-attr :src "https://secure.meetupstatic.com/photos/member/e/7/a/a/member_279839306.jpeg")   
;;   [:span.person1.col-sm-4 :span.hometown]      (enlive/content (if (empty? (:hometown p1)) "" "Hometown:"))
;;   [:span.person1.col-sm-4 :span.lite_blue]     (enlive/content (:hometown p1))
   [:span.person1.col-sm-4 :span.fiftynine]     (enlive/content "2018")

   [:span.person2.col-sm-4 :span.name]          (enlive/content "Eugene")
   [:span.person2.col-sm-4 :img.img-responsive] (enlive/set-attr :src "https://secure.meetupstatic.com/photos/member/6/9/3/a/member_3206938.jpeg")      
;;   [:span.person2.col-sm-4 :span.hometown]      (enlive/content (if (empty? (:hometown p2)) "" "Hometown:"))
;;   [:span.person2.col-sm-4 :span.lite_blue]     (enlive/content (:hometown p2))
;;   [:span.person2.col-sm-4 :span.fiftynine]     (enlive/content (tformat/unparse (.withZone (tformat/formatter "MMMM d, YYYY") (time/time-zone-for-id "America/New_York")) (:joined-date p2)) )

   ;; Yeah it's long but tearing it out would
   ;; make is less readable.  The biggest issue is that modifying requires
   ;; 10 identical edits, which isn't going to happen anymore.
   
   ;; article1 pinned
   [:div.article1.col-md-4.col-xs-12.paras :a] (enlive/set-attr :href (:basename pinned-article))
   [:div.article1.col-md-4.col-xs-12.paras :a :img.img-responsive] (enlive/set-attr :src (str "content/" (:basename pinned-article) ".jpg"))
   [:div.article1.col-md-4.col-xs-12.paras :a :span.date :strong]  (enlive/content (first (:date (:metadata pinned-article))))
   [:div.article1.col-md-4.col-xs-12.paras :h5 :b]                 (enlive/content (make-title pinned-article))
   [:div.article1.col-md-4.col-xs-12.paras :p.date]  (enlive/html-content (take-first (:html pinned-article)))

   ;; article2
   [:div.article2.col-md-4.col-xs-12.paras :a] (enlive/set-attr :href (:basename a2))
   [:div.article2.col-md-4.col-xs-12.paras :a :img.img-responsive] (enlive/set-attr :src (str "content/" (:basename a2) ".jpg"))
   [:div.article2.col-md-4.col-xs-12.paras :a :span.date :strong]  (enlive/content (first (:date (:metadata a2))))
   [:div.article2.col-md-4.col-xs-12.paras :h5 :b]                 (enlive/content (make-title a2))
   [:div.article2.col-md-4.col-xs-12.paras :p.date]  (enlive/html-content (take-first (:html a2)))

   ;; article3
   [:div.article3.col-md-4.col-xs-12.paras :a] (enlive/set-attr :href (:basename a3))
   [:div.article3.col-md-4.col-xs-12.paras :a :img.img-responsive] (enlive/set-attr :src (str "content/" (:basename a3) ".jpg"))
   [:div.article3.col-md-4.col-xs-12.paras :a :span.date :strong]  (enlive/content (first (:date (:metadata a3))))
   [:div.article3.col-md-4.col-xs-12.paras :h5 :b]                 (enlive/content (make-title a3))
   [:div.article3.col-md-4.col-xs-12.paras :p.date]  (enlive/html-content (take-first (:html a3)))

   ;; article4
   [:div.article4.col-md-4.col-xs-12.paras :a] (enlive/set-attr :href (:basename a4))
   [:div.article4.col-md-4.col-xs-12.paras :a :img.img-responsive] (enlive/set-attr :src (str "content/" (:basename a4) ".jpg"))
   [:div.article4.col-md-4.col-xs-12.paras :a :span.date :strong]  (enlive/content (first (:date (:metadata a4))))
   [:div.article4.col-md-4.col-xs-12.paras :h5 :b]                 (enlive/content (make-title a4))
   [:div.article4.col-md-4.col-xs-12.paras :p.date]  (enlive/html-content (take-first (:html a4)))

   ;; article5
   [:div.article5.col-md-4.col-xs-12.paras :a] (enlive/set-attr :href (:basename a5))
   [:div.article5.col-md-4.col-xs-12.paras :a :img.img-responsive] (enlive/set-attr :src (str "content/" (:basename a5) ".jpg"))
   [:div.article5.col-md-4.col-xs-12.paras :a :span.date :strong]  (enlive/content (first (:date (:metadata a5))))
   [:div.article5.col-md-4.col-xs-12.paras :h5 :b]                 (enlive/content (make-title a5))
   [:div.article5.col-md-4.col-xs-12.paras :p.date]  (enlive/html-content (take-first (:html a5)))

   ;; article6
   [:div.article6.col-md-4.col-xs-12.paras :a] (enlive/set-attr :href (:basename a6))
   [:div.article6.col-md-4.col-xs-12.paras :a :img.img-responsive] (enlive/set-attr :src (str "content/" (:basename a6) ".jpg"))
   [:div.article6.col-md-4.col-xs-12.paras :a :span.date :strong]  (enlive/content (first (:date (:metadata a6))))
   [:div.article6.col-md-4.col-xs-12.paras :h5 :b]                 (enlive/content (make-title a6))
   [:div.article6.col-md-4.col-xs-12.paras :p.date]  (enlive/html-content (take-first (:html a6)))
   
   ;; upcoming meeting
   [:div.banner3 :div.container :div.row.features]  (enlive/html-content (wrap-mtg-url (str "Join us " ) meeting))
   [:div.banner3 :div.container :div.row.bck]       (enlive/html-content (wrap-mtg-url (:title (:meta-data meeting)) meeting))
   [:div.banner3 :div.container :div.row.handcraft] (enlive/html-content (str (str "<p>" (wrap-mtg-url (take-first (:html meeting)) meeting) "</p>")) "<p>" (wrap-mtg-url "read more..." meeting) "</p>")

   ;; ads
   [:a#partner]   (enlive/set-attr :href (:url ad))
   [:img#partner] (enlive/set-attr :src (:path ad)) 
   )))

(defn home-page []
  ((template-home)))

(defn article-page [wiki-article]
  ;; is it really an article or just content page?  article will be tagged
  (cond
   (nil? (first (:article (:metadata wiki-article)))) ((template-content wiki-article))
   :else                                           ((template-article wiki-article))
   )
  )
