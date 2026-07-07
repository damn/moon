(ns clojure.schemas-optional-keyset
  (:require [clojure.malli-form :refer [malli-form]]
            [clojure.malli-optional-keyset :as optional-keyset]))

(defn optional-keyset [schemas schema]
  (optional-keyset/f (malli-form schema schemas)))
