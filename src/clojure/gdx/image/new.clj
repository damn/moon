(ns clojure.gdx.image.new
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)))

(defn f [^TextureRegion texture-region]
  (Image. texture-region))
