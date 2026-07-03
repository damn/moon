(ns clojure.gdx.free-type-font-generator$free-type-font-parameter.set-mag-filter
  (:import (com.badlogic.gdx.graphics Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn f [^FreeTypeFontGenerator$FreeTypeFontParameter parameter ^Texture$TextureFilter filter]
  (set! (.magFilter parameter) filter)
  parameter)
