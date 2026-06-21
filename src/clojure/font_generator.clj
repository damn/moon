(ns clojure.font-generator
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator)))

(defn create [file-handle]
  (FreeTypeFontGenerator. file-handle))
