(ns moon.schemas.map-keys
  (:require [moon.schemas.malli-form :refer [malli-form]]
            [malli.utils.map-keys :as map-keys]))

(defn map-keys [schemas schema]
  (map-keys/f (malli-form schema schemas)))
