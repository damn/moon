(ns scene2d.ui.label.set-text
  (:require [com.badlogic.gdx.scenes.scene2d.ui.label :as label]))

(defn f [label text]
  (label/set-text! label text))
