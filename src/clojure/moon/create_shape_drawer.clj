(ns clojure.moon.create-shape-drawer
  (:require [clojure.shape-drawer :as shape-drawer]
            [gdl.graphics.g2d.texture-region :as texture-region]))

(defn f [ctx]
  (assoc ctx
         :ctx/shape-drawer (shape-drawer/new (:ctx/batch ctx)
                                             (texture-region/new (:ctx/shape-drawer-texture ctx) 1 0 1 1))))
