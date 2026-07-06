(ns editor.create-widget.boolean
  (:require [com.badlogic.gdx.scenes.scene2d.ui.checkbox :as checkbox]))

(defn f
  [_ checked? {:keys [ctx/skin]}]
  (doto (checkbox/new "" skin)
    (checkbox/set-checked checked?)))
