(ns malli.is-optional-test
  (:require [malli.is-optional :as optional?]))

(comment
 (= (optional?/f :foo
                 [:map {:closed true}
                  [:foo]
                  [:bar]
                  [:baz {:optional true}]
                  [:boz {:optional false}]
                  [:asdf {:optional true}]])
    nil)

 (= (optional?/f :baz
                 [:map {:closed true}
                  [:foo]
                  [:bar]
                  [:baz {:optional true}]
                  [:boz {:optional false}]
                  [:asdf {:optional true}]])
    true)

 (= (optional?/f :asdf
                 [:map {:closed true}
                  [:foo]
                  [:bar]
                  [:baz {:optional true}]
                  [:boz {:optional false}]
                  [:asdf {:optional true}]])
    true)
 )
