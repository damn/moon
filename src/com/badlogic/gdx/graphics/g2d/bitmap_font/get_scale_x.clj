(ns com.badlogic.gdx.graphics.g2d.bitmap-font.get-scale-x
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn f [^BitmapFont font]
  (.scaleX (.getData font)))
