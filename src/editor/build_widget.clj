(ns editor.build-widget
  (:require [clojure.gdx.actor.set-user-object :as set-user-object]
            [editor.create-widget :as create-widget]))

(defn f [ctx schema k v]
  (let [widget (create-widget/f schema v ctx)] ; - wait its used also somewhere else w/o this widget/create?
    ; FIXME assert no user object !
    (set-user-object/f widget [k v])
    widget))
