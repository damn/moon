(ns com.badlogic.gdx.graphics.g2d.freetype.font-generator.generate-font
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator)))

(defn f [^FreeTypeFontGenerator generator parameter]
  (.generateFont generator parameter))
