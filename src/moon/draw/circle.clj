(ns moon.draw.circle
  (:require [clojure.shape-drawer :as shape-drawer]))

(defn do!
  [{:keys [ctx/shape-drawer]} position radius color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/circle! shape-drawer position radius))
