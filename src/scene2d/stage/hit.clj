(ns scene2d.stage.hit
  (:require [com.badlogic.gdx.scenes.scene2d.stage :as stage]))

(defn hit [stage [x y]]
  (stage/hit stage x y true))
