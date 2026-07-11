(ns malli.is-optional-test
  (:require [clojure.map-schema :as map-schema]))

(comment
 (= (map-schema/optional? :foo
                 [:map {:closed true}
                  [:foo]
                  [:bar]
                  [:baz {:optional true}]
                  [:boz {:optional false}]
                  [:asdf {:optional true}]])
    nil)

 (= (map-schema/optional? :baz
                 [:map {:closed true}
                  [:foo]
                  [:bar]
                  [:baz {:optional true}]
                  [:boz {:optional false}]
                  [:asdf {:optional true}]])
    true)

 (= (map-schema/optional? :asdf
                 [:map {:closed true}
                  [:foo]
                  [:bar]
                  [:baz {:optional true}]
                  [:boz {:optional false}]
                  [:asdf {:optional true}]])
    true)
 )
