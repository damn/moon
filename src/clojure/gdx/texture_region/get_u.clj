(ns clojure.gdx.texture-region.get-u
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn f [region]
  (TextureRegion/.getU region))
