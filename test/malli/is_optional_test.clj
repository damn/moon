(ns malli.is-optional-test
  (:require [moon.game :as game]))
(comment
 (= (game/map-schema-optional?
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]]
     :foo)
    nil)

 (= (game/map-schema-optional?
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]]
     :baz)
    true)

 (= (game/map-schema-optional?
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]]
     :asdf)
    true)
 )
