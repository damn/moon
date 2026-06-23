(ns editor.widget.default
  (:require [string.truncate :refer [truncate]]
            [clojure.edn-str :refer [->edn-str]]
            [scene2d.actor.get-user-object :refer [get-user-object]]
            [scene2d.ui.label :as label]))

(defn create
  [_ v {:keys [ctx/skin]}]
  (label/create
   {:text (truncate (->edn-str v) 60)
    :skin skin}))

(defn value
  [_  widget _schemas]
  ((get-user-object widget) 1))
