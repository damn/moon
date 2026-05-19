(ns gdl.graphics.texture)

(defprotocol Texture
  (region [_]
          [_ x y w h]))
