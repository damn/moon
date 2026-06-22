(ns gdl.file.font-generator
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator)))

(defn f [file-handle]
  (FreeTypeFontGenerator. file-handle))
