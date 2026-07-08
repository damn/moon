(ns clojure.malli-form-create-map-schema
  (:require [clojure.malli-create-map-schema :as malli-create-map-schema]
            [clojure.malli-form :refer [malli-form]]))

(defn create-map-schema [schemas ks]
  (malli-create-map-schema/f ks
                               (fn [k]
                                 (malli-form (get schemas k) schemas))))
