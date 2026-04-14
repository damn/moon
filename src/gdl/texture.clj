(ns gdl.texture)

(defprotocol Texture
  (region [_]
          [_ x y width height]))
