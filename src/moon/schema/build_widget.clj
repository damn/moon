(ns moon.schema.build-widget
  (:require [clojure.scenes.scene2d.actor.set-user-object :refer [set-user-object!]]
            [moon.schema.create-widget :as create-widget]))

(defn f [ctx schema k v]
  (let [widget (create-widget/f schema v ctx)] ; - wait its used also somewhere else w/o this widget/create?
    ; FIXME assert no user object !
    (set-user-object! widget [k v])
    widget))
