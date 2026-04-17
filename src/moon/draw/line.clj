(ns moon.draw.line
  (:require [clojure.shape-drawer :as shape-drawer]))

(defn do!
  [{:keys [ctx/shape-drawer]} start end color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/line! shape-drawer start end))
