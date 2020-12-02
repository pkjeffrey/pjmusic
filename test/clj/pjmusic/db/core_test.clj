(ns pjmusic.db.core-test
  (:require
    [clojure.test :refer :all]
    [java-time.pre-java8]
    [mount.core :as mount]
    [next.jdbc :as jdbc]
    [pjmusic.config :refer [env]]
    [pjmusic.db.core :refer [*db*] :as db]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'pjmusic.config/env
      #'pjmusic.db.core/*db*)
    (f)))

(deftest test-users
  (jdbc/with-transaction [t-conn *db* {:rollback-only true}]
    (is (= 1 (db/create-user!
               t-conn
               {:id         "1"
                :first_name "Sam"
                :last_name  "Smith"
                :email      "sam.smith@example.com"
                :pass       "pass"}
               {})))
    (is (= {:id         "1"
            :first_name "Sam"
            :last_name  "Smith"
            :email      "sam.smith@example.com"
            :pass       "pass"
            :admin      nil
            :last_login nil
            :is_active  nil}
           (db/get-user t-conn {:id "1"} {})))))
