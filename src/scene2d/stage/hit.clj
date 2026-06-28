(ns scene2d.stage.hit
  (:import (scene2d Stage)))

(defn hit [^Stage stage [x y]]
  (.hit stage x y true))
