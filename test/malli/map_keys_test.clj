(ns malli.map-keys-test
  (:require [moon.game :as game]))
(comment
 (= (game/map-schema-map-keys
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]])
    [:foo :bar :baz :boz :asdf])
 )
