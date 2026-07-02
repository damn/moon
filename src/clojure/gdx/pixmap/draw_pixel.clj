(ns clojure.gdx.pixmap.draw-pixel
  (:import (com.badlogic.gdx.graphics Pixmap)))

(defn f [^Pixmap pixmap x y]
  (Pixmap/.drawPixel pixmap (int x) (int y)))
