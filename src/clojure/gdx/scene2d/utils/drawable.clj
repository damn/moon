(ns clojure.gdx.scene2d.utils.drawable
  "A drawable knows how to draw itself at a given rectangular size. It provides padding sizes and a minimum size so that other
 * code can determine how to size and position content."
  (:import (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn set-min-size! [^Drawable drawable min-width min-height]
  (.setMinSize drawable min-width min-height))
