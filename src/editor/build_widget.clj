(ns editor.build-widget
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [editor.create-widget :as create-widget]))

(defn f [ctx schema k v]
  (let [widget (create-widget/f schema v ctx)] ; - wait its used also somewhere else w/o this widget/create?
    ; FIXME assert no user object !
    (actor/set-user-object! widget [k v])
    widget))
