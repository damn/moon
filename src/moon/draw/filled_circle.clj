(ns moon.draw.filled-circle
  (:require [gdl.shape-drawer :as shape-drawer]))

(defn do!
  [{:keys [ctx/shape-drawer]} position radius color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/filled-circle! shape-drawer position radius))
