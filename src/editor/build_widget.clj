(ns editor.build-widget
  (:require [editor.create-widget :as create-widget])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [ctx schema k v]
  (let [widget (create-widget/f schema v ctx)] ; - wait its used also somewhere else w/o this widget/create?
    ; FIXME assert no user object !
    (Actor/.setUserObject widget [k v])
    widget))
