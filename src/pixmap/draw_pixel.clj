(ns pixmap.draw-pixel
  (:import (com.badlogic.gdx.graphics Pixmap)))

(defn f! [^Pixmap pixmap x y]
  (.drawPixel pixmap x y))
