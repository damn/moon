(ns create.db
  (:require [moon.db :as db]))

(defn step [ctx]
  (assoc ctx :ctx/db (db/create)))
