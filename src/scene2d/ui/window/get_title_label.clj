(ns scene2d.ui.window.get-title-label
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn f [^Window window]
  (.getTitleLabel window))
