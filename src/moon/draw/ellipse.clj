(ns moon.draw.ellipse
  (:require [moon.shape-drawer :as shape-drawer]))

(defn do!
  [{:keys [ctx/shape-drawer]} position radius-x radius-y color-float-bits]
  (shape-drawer/ellipse! shape-drawer position radius-x radius-y color-float-bits))
