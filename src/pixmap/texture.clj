(ns pixmap.texture
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Texture)))

(defn f [^Pixmap pixmap]
  (Texture. pixmap))
