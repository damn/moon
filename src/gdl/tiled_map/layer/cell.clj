(ns gdl.tiled-map.layer.cell)

(declare create)

(defprotocol Cell
  (tile [_]))
