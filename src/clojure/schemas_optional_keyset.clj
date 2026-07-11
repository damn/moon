(ns clojure.schemas-optional-keyset
  (:require [moon.schema :as schema]
            [clojure.malli-optional-keyset :as optional-keyset]))

(defn optional-keyset [schemas schema]
  (optional-keyset/f (schema/malli-form schema schemas)))
