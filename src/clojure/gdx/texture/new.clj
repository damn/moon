(ns clojure.gdx.texture.new
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Texture)))

(defn f [^Pixmap pixmap]
  (Texture. pixmap))
