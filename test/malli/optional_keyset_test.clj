(ns malli.optional-keyset-test
  (:require [malli.optional-keyset :as optional-keyset]))

(comment
 (= (optional-keyset/f
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]])
    #{:baz :asdf})
 )
