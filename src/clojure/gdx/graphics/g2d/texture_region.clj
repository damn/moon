(ns clojure.gdx.graphics.g2d.texture-region
  (:require [clojure.graphics.g2d.texture-region :as texture-region])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(extend-type TextureRegion
  texture-region/TextureRegion
  (width [this]
    (.getRegionWidth this))

  (height [this]
    (.getRegionHeight this)))
