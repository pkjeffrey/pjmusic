(ns pjmusic.pages.release
  (:require
    [ajax.core :as ajax]
    [goog.string :as gstr]
    [goog.string.format]
    [re-frame.core :as rf]))

(rf/reg-event-db
  :set-release
  (fn [db [_ release]]
    (assoc db :release release)))

(rf/reg-event-fx
  :fetch-release
  (fn [_ [_ id]]
    {:http-xhrio {:method          :get
                  :uri             (str "/api/releases/" id)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:set-release]}}))

(rf/reg-event-fx
  :release/init
  (fn [_ [_ id]]
    {:dispatch [:fetch-release id]}))

(rf/reg-sub
  :release
  (fn [db _]
    (:release db)))

(defn release-page []
  (when-let [{:keys [id title artistid artistname released compilation media-descr
                     label catalog medias]} @(rf/subscribe [:release])]
    [:section
     [:div.release
      [:div.release-art [:img {:src (str "/img/releases/" id)}]]
      [:p.title title]
      [:p.artist (if compilation "In: " "By: ")
       [:a {:href (str "#/artist/" artistid)} artistname]]
      [:p.media media-descr]
      [:p.released "Released: " released]
      (when label [:p.label "Label: " label])
      (when catalog [:p.catalog "Catalog: " catalog])]
     [:div.medias
      (for [{:keys [id name title tracks]} medias]
        [:div {:key id}
         [:p.title name " " title]
         [:ul
          (for [{:keys [side number title artistid artistname]} tracks]
            [:li {:key (str side "-" number)}
             side "-" (gstr/format "%02d" number) " " title
             (when compilation " - by ")
             (when compilation [:a {:href (str "#/artist/" artistid)} artistname])])]])]]))