(ns pjmusic.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [pjmusic.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[pjmusic started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[pjmusic has shut down successfully]=-"))
   :middleware wrap-dev})
