(ns clojure.malli.optional-keyset
  (:require [clojure.malli.is-optional :as optional?]
            [clojure.malli.map-keys :as map-keys]))

(defn f [map-schema]
  (set (filter #(optional?/f % map-schema)
               (map-keys/f map-schema))))

(comment
 (= (optional-keyset
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]])
    #{:baz :asdf})
 )
