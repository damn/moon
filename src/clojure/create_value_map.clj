(ns clojure.create-value-map
  (:require [clojure.build-values :refer [build-values]]))

(defn f [_ v db]
  (build-values (:db/schemas db) v db))
