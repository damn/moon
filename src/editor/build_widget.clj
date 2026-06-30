(ns editor.build-widget
  (:require [clojure.gdx :as gdx]
            [editor.create-widget :as create-widget]))

(defn f [ctx schema k v]
  (let [widget (create-widget/f schema v ctx)] ; - wait its used also somewhere else w/o this widget/create?
    ; FIXME assert no user object !
    (gdx/set-user-object! widget [k v])
    widget))
