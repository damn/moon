(ns clojure.gdx.image.new-texture
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.scenes.scene2d.ui Image)))

(defn f [^Texture texture]
  (Image. texture))
