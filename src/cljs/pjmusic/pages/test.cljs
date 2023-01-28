(ns pjmusic.pages.test
  (:require
    [pjmusic.components.auto-complete-ajax :refer [auto-complete-ajax]]
    [reagent.core :as r]))



(defn test-page []
  (let [result (r/atom "")]
    (fn []
      [:section
       [:h2 "Testing"]
       [:div.form
        [:div.field
         [:label {:for "artist"} "Artist:"]
         [auto-complete-ajax "artist" result]]
        [:div.field
         [:label {:for "release"} "Release:"]
         [:input#release {:type "text"}]]
        [:div.button
         [:button "Click this"]]
        [:div.result
         [:p @result]]]])))