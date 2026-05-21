(ns gdl.graphics.gl20)

(declare color-buffer-bit)

(defprotocol GL20
  (clear-color! [_ r g b a])
  (clear! [_ mask]))

