(ns malli.map-keys-test
  (:require [malli.map-keys :as map-keys]))

(comment
 (= (map-keys/f
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]])
    [:foo :bar :baz :boz :asdf])
 )
