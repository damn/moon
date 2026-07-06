(ns editor.widget-value.default
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn f
  [_  widget _schemas]
  ((actor/get-user-object widget) 1))
