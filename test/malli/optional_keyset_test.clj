(ns malli.optional-keyset-test
  (:require [moon.map-schema :as map-schema]))

(comment
 (= (map-schema/optional-keyset
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]])
    #{:baz :asdf})
 )
