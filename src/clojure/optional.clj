(ns clojure.optional
  (:require [clojure.malli-form :refer [malli-form]]
            [clojure.is-optional :as optional?]))

(defn optional? [schemas schema k]
  (optional?/f k (malli-form schema schemas)))
