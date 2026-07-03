(ns clojure.gdx.texture-region-drawable.new
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn f [^TextureRegion texture-region]
  (TextureRegionDrawable. texture-region))
