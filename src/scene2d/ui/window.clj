(ns scene2d.ui.window
  (:require [com.badlogic.gdx.scenes.scene2d.ui.window :as window]))

(defn f [{:keys [title skin]}]
  (window/new title skin))
