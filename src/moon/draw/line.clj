(ns moon.draw.line
  (:require [moon.shape-drawer :as shape-drawer]))

(defn do!
  [{:keys [ctx/shape-drawer]} start end color-float-bits]
  (shape-drawer/line! shape-drawer start end color-float-bits))
