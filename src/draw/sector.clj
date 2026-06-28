(ns draw.sector
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f
  [{:keys [ctx/shape-drawer]} [center-x center-y] radius start-radians radians color-float-bits]
  (ShapeDrawer/.setColor shape-drawer (float color-float-bits))
  (ShapeDrawer/.sector shape-drawer center-x center-y radius start-radians radians))
