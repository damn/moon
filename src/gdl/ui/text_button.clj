(ns gdl.ui.text-button
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextButton)))

(defn create [text skin]
  (TextButton. text skin))
