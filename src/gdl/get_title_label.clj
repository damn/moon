(ns gdl.get-title-label
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn f [^Window window]
  (.getTitleLabel window))
