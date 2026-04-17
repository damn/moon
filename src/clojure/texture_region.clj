(ns clojure.texture-region)

(defprotocol TextureRegion
  (width [_])
  (height [_]))
