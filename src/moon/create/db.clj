(ns moon.create.db
  (:require [moon.db :as db]))

(defn step [ctx params]
  (assoc ctx :ctx/db (db/create params)))
