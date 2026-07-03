(ns clojure.gdx.texture-region-drawable.set-min-size
  (:import (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn f [^TextureRegionDrawable drawable min-width min-height]
  (TextureRegionDrawable/.setMinSize drawable min-width min-height))
