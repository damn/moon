(ns editor.widget.default
  (:require [clojure.string.truncate :refer [truncate]]
            [clojure.edn-str :refer [->edn-str]]
            [clojure.actor.get-user-object :refer [get-user-object]]
            [clojure.ui.label :as label]))

(defn create
  [_ v {:keys [ctx/skin]}]
  (label/create
   {:text (truncate (->edn-str v) 60)
    :skin skin}))

(defn value
  [_  widget _schemas]
  ((get-user-object widget) 1))
