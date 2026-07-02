(ns clojure.gdx.pixmap.set-color
  (:import (com.badlogic.gdx.graphics Pixmap)))

(defn f [^Pixmap pixmap r g b a]
  (Pixmap/.setColor pixmap r g b a))
