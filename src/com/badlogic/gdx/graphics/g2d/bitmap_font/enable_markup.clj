(ns com.badlogic.gdx.graphics.g2d.bitmap-font.enable-markup
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn f! [^BitmapFont font]
  (set! (.markupEnabled (.getData font)) true))
