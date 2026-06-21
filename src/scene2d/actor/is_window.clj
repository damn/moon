(ns scene2d.actor.is-window
  (:require [com.badlogic.gdx.scenes.scene2d.ui.window :as window]))

(defn f [actor]
  (instance? window/class actor))
