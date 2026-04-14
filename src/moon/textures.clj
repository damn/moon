(ns moon.textures)

(defprotocol Textures
  (texture-region [_ image]))
