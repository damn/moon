(ns clojure.gdx.free-type-font-generator.generate-font
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn f [generator ^FreeTypeFontGenerator$FreeTypeFontParameter parameter]
  (FreeTypeFontGenerator/.generateFont generator parameter))
