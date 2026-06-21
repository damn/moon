(ns clojure.pixmap
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format)))

(defn create [width height]
  (Pixmap. (int width) (int height) Pixmap$Format/RGBA8888))


