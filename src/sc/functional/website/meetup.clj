(ns sc.functional.website.meetup
  (:require [net.cgrand.enlive-html :as enlive]
            [clj-time.core          :as time]
            [clj-time.coerce        :as tcoerce]))

;; "42" => 42, default 0
(defn str->int [str]
  (try
    (Integer/parseInt str)
    (catch java.lang.NumberFormatException _ 0)))

(defn str->long [str]
  (try
    (Long/parseLong str)
    (catch java.lang.NumberFormatException _ 0)))

(defn fetch-url-raw [url] (slurp url))

(defn fetch-url [url]
  (enlive/html-resource (java.net.URL. url)))

(defn fetch-url-xml [url]
  (enlive/xml-resource (java.net.URL. url)))

(defn- decomp-item [data target-keyword]
  (enlive/text (first (enlive/select data target-keyword))))

(defn- make-event [item]
  (hash-map :type :meetup-past
            :title        (decomp-item item [:item :> :name])
            :description  (apply str (decomp-item item [:item :> :description]))
            :venue        (decomp-item item [:item :> :venue :> :name])
            :time         (time/to-time-zone (tcoerce/from-long (str->long (decomp-item item [:item :> :time])))
                                             (time/time-zone-for-id "America/New_York"))
            :meeting-date (decomp-item item [:item :> :time])
            :address      (decomp-item item [:item :> :venue :> :address_1])
            :address2     (decomp-item item [:item :> :how_to_find_us])
            :event-url    (decomp-item item [:item :> :event_url])))

(defn fetch-upcoming-meetups []
  (let [data (fetch-url "http://api.meetup.com/2/events.xml?status=upcoming&group_urlname=Functional-SC&key=5247666759864d49d1e1a2b263463")]
    (map #(make-event %)
         (enlive/select data #{[:item]}))
    ))

(defn fetch-current-meetup []
  (let [data (fetch-url "http://api.meetup.com/2/events.xml?group_urlname=Functional-SC&key=5247666759864d49d1e1a2b263463")
        item (enlive/select data #{[:items (enlive/nth-child 1)]})]
    (make-event item)))

(defn fetch-members []
  (filter (fn [person] (not (empty? (:photo person))))
          (let [data (fetch-url "https://api.meetup.com/2/members.xml?group_urlname=Functional-SC&key=5247666759864d49d1e1a2b263463")]
            (map #(hash-map :name     (decomp-item % [:item :> :name])
                            :photo    (decomp-item % [:item :> :photo :> :photo_link])
                            :joined-date (time/to-time-zone (tcoerce/from-long (str->long (decomp-item % [:item :> :joined]))) (time/time-zone-for-id "America/New_York"))
                            :hometown (decomp-item % [:item :> :hometown])
                            )
                 (enlive/select data #{[:item]})))))


(def members (memoize #(fetch-members)))

(defn fetch-cached-members [] (members))

