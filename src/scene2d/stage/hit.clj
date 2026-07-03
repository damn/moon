(ns scene2d.stage.hit
  (:require [clojure.gdx.stage.hit :as hit]))

(defn hit [stage [x y]]
  (hit/f stage x y true))
