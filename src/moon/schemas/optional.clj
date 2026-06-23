(ns moon.schemas.optional
  (:require [clojure.malli-form :refer [malli-form]]
            [malli.is-optional :as optional?]))

(defn optional? [schemas schema k]
  (optional?/f k (malli-form schema schemas)))
