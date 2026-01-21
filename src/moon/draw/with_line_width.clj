(ns moon.draw.with-line-width
  (:require [moon.ctx :as ctx])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn do!
  [{:keys [^ShapeDrawer ctx/shape-drawer]
    :as ctx}
   width
   draws]
  (let [old-line-width (.getDefaultLineWidth shape-drawer)]
    (.setDefaultLineWidth shape-drawer (* width old-line-width))
    (ctx/draw! ctx draws)
    (.setDefaultLineWidth shape-drawer old-line-width)))
