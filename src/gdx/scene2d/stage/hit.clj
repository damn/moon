(ns gdx.scene2d.stage.hit
  (:require [clojure.stage :as stage]))

(defn hit [stage [x y]]
  (stage/hit stage x y true))
