(ns malli.optional-keyset-test
  (:require [moon.game :as game]))
(comment
 (= (game/map-schema-optional-keyset
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]])
    #{:baz :asdf})
 )
