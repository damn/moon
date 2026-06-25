(ns moon.schemas.create-value.map
  (:require [moon.schemas.build-values :refer [build-values]]))

(defn f [_ v db]
  (build-values (:db/schemas db) v db))
