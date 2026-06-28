(ns draw.with-line-width
  (:require [ctx.draw :refer [draw!]])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f
  [{:keys [ctx/shape-drawer]
    :as ctx}
   width
   draws]
  (let [old-line-width (ShapeDrawer/.getDefaultLineWidth shape-drawer)]
    (ShapeDrawer/.setDefaultLineWidth shape-drawer (* width old-line-width))
    (draw! ctx draws)
    (ShapeDrawer/.setDefaultLineWidth shape-drawer old-line-width)))
