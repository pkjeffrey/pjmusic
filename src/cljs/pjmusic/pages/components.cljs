(ns pjmusic.pages.components
  (:require
    [reitit.frontend.easy :as rfe]))

(defn- artist-href
  [artist-id]
  (rfe/href :artist {:id artist-id}))

(defn- release-href
  [release-id]
  (rfe/href :release {:id release-id}))

(defn- release-image-url
  [release-id]
  (str "/img/releases/" release-id))

(defn linked-image
  ([image-url link-href]
   (linked-image image-url link-href nil))
  ([image-url link-href title]
   (let [a (merge {:href link-href}
                  (when title
                    {:title title}))]
     [:div
      [:a a
       [:img {:src image-url}]]])))

(defn linked-title
  [title link-href]
  [:div.title [:a {:href link-href} title]])

(defn linked-artist
  [artist-name compilation link-href]
  [:div.artist (if compilation "In: " "By: ")
   [:a {:href link-href} artist-name]])

(defn release-item
  [{:keys [id artist-id artist-name title released compilation media-descr]}]
  (let [release-href (release-href id)]
    [:div.release-item
     [linked-image (release-image-url id) release-href title]
     [linked-title title release-href]
     (if artist-id
       [linked-artist artist-name compilation (artist-href artist-id)]
       [:div])
     [:div.released released " " media-descr]]))

(defn release-list
  [releases]
  [:div.release-list
   (for [{:keys [id] :as release} releases]
     [release-item (assoc release :key id)])])

(defn release
  [{:keys [id title artist-id artist-name compilation
           media-descr released label catalog]}]
  [:div.release
   [:div.release-art [:img {:src (release-image-url id)}]]
   [:div.title [:h2 title]]
   [linked-artist artist-name compilation (artist-href artist-id)]
   [:div.media media-descr]
   [:div.released "Released: " released]
   (when label [:div.label "Label: " label])
   (when catalog [:div.catalog "Catalog: " catalog])])

(defn media-track
  [{:keys [side number title artist-id artist-name]} compilation]
  [:tr
   (when side [:td.number side])
   [:td.number number]
   [:td title]
   (when compilation [:td>a {:href (artist-href artist-id)} artist-name])])

(defn release-medias
  [medias compilation]
  [:div.media-tracks>table>tbody
   (for [{:keys [id format title tracks]} medias]
     (list [:tr {:key id}
            [:th.title {:col-span 4} format " " title]]
           (for [{:keys [side number] :as track} tracks]
             [media-track (assoc track :key (str side "-" number)) compilation])))])