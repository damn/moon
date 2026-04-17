(ns clojure.graphics.texture)

(defprotocol Texture
  (region [_]
          [_ x y width height]))
