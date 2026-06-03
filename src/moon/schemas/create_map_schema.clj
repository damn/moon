(ns moon.schemas.create-map-schema
  (:require [moon.schemas.malli-form :refer [malli-form]]
            [malli.utils :as mu]))

(defn create-map-schema [schemas ks]
  (mu/create-map-schema ks (fn [k]
                             (malli-form (get schemas k) schemas))))
