(ns moon.schemas.create-map-schema
  (:require [clojure.malli-form :refer [malli-form]]
            [clojure.malli.create-map-schema :as create-map-schema]))

(defn create-map-schema [schemas ks]
  (create-map-schema/f ks (fn [k]
                            (malli-form (get schemas k) schemas))))
