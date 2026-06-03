(ns moon.schemas.optional-keyset
  (:require [moon.schemas.malli-form :refer [malli-form]]
            [malli.utils :as mu]))

(defn optional-keyset [schemas schema]
  (mu/optional-keyset (malli-form schema schemas)))
