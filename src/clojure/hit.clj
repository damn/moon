(ns clojure.hit
  (:require [gdl.scenes.scene2d.stage :as stage]))

(defn hit [stage [x y]]
  (stage/hit stage x y true))
