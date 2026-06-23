(ns gdl.pixmap
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format)))

(defn f
  [width height]
  (Pixmap. (int width) (int height) Pixmap$Format/RGBA8888))


