(ns moon.schemas.map-keys
  (:require [clojure.malli-form :refer [malli-form]]
            [malli.map-keys :as map-keys]))

(defn map-keys [schemas schema]
  (map-keys/f (malli-form schema schemas)))
