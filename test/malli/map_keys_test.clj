(ns malli.map-keys-test
  (:require [moon.map-schema :as map-schema]))

(comment
 (= (map-schema/map-keys
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]])
    [:foo :bar :baz :boz :asdf])
 )
