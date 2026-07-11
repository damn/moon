(ns clojure.optional
  (:require [clojure.malli-form :refer [malli-form]]
            [clojure.map-schema :as map-schema]))

(defn optional? [schemas schema k]
  (map-schema/optional? k (malli-form schema schemas)))
