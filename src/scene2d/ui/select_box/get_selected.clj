(ns scene2d.ui.select-box.get-selected
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox)))

(defn f [^SelectBox select-box]
  (.getSelected select-box))
