(ns clojure.editor.db
  (:require [clojure.db :as db]))

(defn f [ctx]
  (assoc ctx :ctx/db (db/create)))
