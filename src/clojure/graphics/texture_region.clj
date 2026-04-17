(ns clojure.graphics.texture-region)

(defprotocol TextureRegion
  (width [_])
  (height [_]))
