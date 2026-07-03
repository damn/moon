(ns clojure.gdx.free-type-font-generator$free-type-font-parameter.set-size
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn f [^FreeTypeFontGenerator$FreeTypeFontParameter parameter size]
  (set! (.size parameter) size)
  parameter)
