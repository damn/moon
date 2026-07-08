(ns clojure.editor.create-widget-build-widget
  (:require [clojure.actor.set-user-object]
            [clojure.editor.create-widget :refer [create-widget]]))

(defn build-widget [ctx schema k v]
  (let [widget (create-widget schema v ctx)]
    (clojure.actor.set-user-object/f widget [k v])
    widget))
