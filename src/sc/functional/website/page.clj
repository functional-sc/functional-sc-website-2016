(ns sc.functional.website.page
  (:use [hiccup.core]))

(defn article-page [wiki-article]
  (html [:title (:title wiki-article)]
        [:h1 (:title wiki-article)]
        [:p (:content wiki-article)]
        ))