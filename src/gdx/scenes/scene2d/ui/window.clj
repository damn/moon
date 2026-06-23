(ns gdx.scenes.scene2d.ui.window
  (:require [gdl.set-opts :refer [set-opts!]]
            [ui.window :as window]))

(defn create
  [opts]
  (let [window (window/f opts)]
    (set-opts! window opts)
    window))
