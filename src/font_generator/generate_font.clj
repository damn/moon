(ns font-generator.generate-font
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                    FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn f [^FreeTypeFontGenerator generator ^FreeTypeFontGenerator$FreeTypeFontParameter parameter]
  (.generateFont generator parameter))
