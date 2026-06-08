(ns draw.filled-circle
  (:require [space.earlygrey.shape-drawer :as shape-drawer]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/filled-circle! shape-drawer x y radius))
