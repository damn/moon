(ns clojure.texture)

(defprotocol Texture
  (region [_]
          [_ x y width height]))
