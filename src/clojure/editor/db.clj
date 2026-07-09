(ns clojure.editor.db
  (:require [clojure.moon-db :as db]))

(defn f [ctx]
  (assoc ctx :ctx/db (db/create)))
