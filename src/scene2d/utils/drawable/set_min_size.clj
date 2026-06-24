(ns scene2d.utils.drawable.set-min-size
  (:import (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn f [^Drawable drawable min-width min-height]
  (.setMinSize drawable min-width min-height))
