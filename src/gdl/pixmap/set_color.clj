(ns gdl.pixmap.set-color
  (:import (com.badlogic.gdx.graphics Pixmap)))

(defn f! [^Pixmap pixmap r g b a]
  (.setColor pixmap r g b a))
