(ns clojure.moon.create-db
  (:require [clojure.moon-db :as db]))

(defn f [ctx]
  (assoc ctx :ctx/db (db/create)))
