(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require
    [clojure.pprint]
    [clojure.spec.alpha :as s]
    [conman.core :as conman]
    [expound.alpha :as expound]
    [mount.core :as mount]
    [pjmusic.core :refer [start-app]]
    [pjmusic.db.core]
    [pjmusic.figwheel :refer [start-fw stop-fw cljs]]
    [pjmusic.config :refer [env]]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(add-tap (bound-fn* clojure.pprint/pprint))

(defn start
  "Starts application.
  You'll usually want to run this on startup."
  []
  (mount/start-without #'pjmusic.core/repl-server))

(defn stop
  "Stops application."
  []
  (mount/stop-except #'pjmusic.core/repl-server))

(defn restart
  "Restarts application."
  []
  (stop)
  (start))

(defn restart-db
  "Restarts database."
  []
  (mount/stop #'pjmusic.db.core/*db*)
  (mount/start #'pjmusic.db.core/*db*)
  (binding [*ns* 'pjmusic.db.core]
    (conman/bind-connection pjmusic.db.core/*db* "sql/queries.sql")))