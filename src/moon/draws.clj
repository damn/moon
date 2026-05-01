(ns moon.draws
  (:require [moon.graphics :as graphics])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(declare handle)

(def draw-fns
  {
   :draw/circle           (fn
                            [{:keys [^ShapeDrawer ctx/shape-drawer]} [x y] radius color-float-bits]
                            (.setColor shape-drawer (float color-float-bits))
                            (.circle shape-drawer x y radius))
   :draw/ellipse          (fn
                            [{:keys [^ShapeDrawer ctx/shape-drawer]} [x y] radius-x radius-y color-float-bits]
                            (.setColor shape-drawer (float color-float-bits))
                            (.ellipse shape-drawer x y radius-x radius-y))
   :draw/filled-circle    (fn
                            [{:keys [^ShapeDrawer ctx/shape-drawer]} [x y] radius color-float-bits]
                            (.setColor shape-drawer (float color-float-bits))
                            (.filledCircle shape-drawer (float x) (float y) (float radius)))
   :draw/filled-rectangle (fn
                            [{:keys [^ShapeDrawer ctx/shape-drawer]} x y w h color-float-bits]
                            (.setColor shape-drawer (float color-float-bits))
                            (.filledRectangle shape-drawer (float x) (float y) (float w) (float h)))
   :draw/grid             (fn
                            [ctx leftx bottomy gridw gridh cellw cellh color-float-bits]
                            (let [w (* (float gridw) (float cellw))
                                  h (* (float gridh) (float cellh))
                                  topy (+ (float bottomy) (float h))
                                  rightx (+ (float leftx) (float w))]
                              (doseq [idx (range (inc (float gridw)))
                                      :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
                                (handle ctx
                                        [[:draw/line [linex topy] [linex bottomy] color-float-bits]]))
                              (doseq [idx (range (inc (float gridh)))
                                      :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
                                (handle ctx
                                        [[:draw/line [leftx liney] [rightx liney] color-float-bits]]))))
   :draw/line             (fn
                            [{:keys [^ShapeDrawer ctx/shape-drawer]} [sx sy] [ex ey] color-float-bits]
                            (.setColor shape-drawer (float color-float-bits))
                            (.line shape-drawer (float sx) (float sy) (float ex) (float ey)))
   :draw/rectangle        (fn
                            [{:keys [^ShapeDrawer ctx/shape-drawer]} x y w h color-float-bits]
                            (.setColor shape-drawer (float color-float-bits))
                            (.rectangle shape-drawer x y w h))
   :draw/sector           (fn
                            [{:keys [^ShapeDrawer ctx/shape-drawer]} [center-x center-y] radius start-radians radians color-float-bits]
                            (.setColor shape-drawer (float color-float-bits))
                            (.sector shape-drawer center-x center-y radius start-radians radians))
   :draw/text             graphics/draw-text!
   :draw/texture-region   graphics/draw-texture-region!
   :draw/with-line-width  (fn
                            [{:keys [^ShapeDrawer ctx/shape-drawer]
                              :as ctx}
                             width
                             draws]
                            (let [old-line-width (.getDefaultLineWidth shape-drawer)]
                              (.setDefaultLineWidth shape-drawer (* width old-line-width))
                              (handle ctx draws)
                              (.setDefaultLineWidth shape-drawer old-line-width)))
   })

(defn handle [ctx draws]
  (doseq [{k 0 :as component} draws
          :when component]
    (apply (get draw-fns k) ctx (rest component))))
