(ns clojure.gdx.texture-region-drawable.tint
  (:import (com.badlogic.gdx.graphics Color)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn f [^TextureRegionDrawable drawable ^Color color]
  (.tint drawable color))
