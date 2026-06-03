(ns gdx.stage
  (:require [clojure.gdx.utils.viewport :as viewport]
            [clojure.gdx.scene2d.stage.hit :refer [hit]]))

(defn mouseover-actor [stage position]
  (hit stage
       (-> stage
           :stage/viewport
           (viewport/unproject position))))
