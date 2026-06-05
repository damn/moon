(ns moon.schemas.optional
  (:require [clojure.malli-form :refer [malli-form]]
            [malli.utils :as mu]))

(defn optional? [schemas schema k]
  (mu/optional? k (malli-form schema schemas)))
