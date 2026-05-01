(ns moon.application.create.shape-drawer
  (:require [clojure.graphics.texture :as texture])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]
    :as ctx}]
  (assoc ctx :ctx/shape-drawer (ShapeDrawer. batch (texture/region shape-drawer-texture 1 0 1 1))))
