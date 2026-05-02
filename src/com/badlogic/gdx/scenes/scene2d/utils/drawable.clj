(ns com.badlogic.gdx.scenes.scene2d.utils.drawable
  (:import (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn set-min-size! [^Drawable drawable min-width min-height]
  (.setMinSize drawable min-width min-height))
