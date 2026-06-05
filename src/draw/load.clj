(ns draw.load
  (:require [draw.text]
            [draw.texture-region]
            [game.constants :as constants]
            [game.ctx.draw :refer [draw!]]
            [space.earlygrey.shape-drawer :as shape-drawer]))

(.bindRoot #'constants/draw-fns
           {
            :draw/circle           (fn
                                     [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
                                     (shape-drawer/set-color! shape-drawer color-float-bits)
                                     (shape-drawer/circle! shape-drawer x y radius))
            :draw/ellipse          (fn
                                     [{:keys [ctx/shape-drawer]} [x y] radius-x radius-y color-float-bits]
                                     (shape-drawer/set-color! shape-drawer color-float-bits)
                                     (shape-drawer/ellipse! shape-drawer x y radius-x radius-y))
            :draw/filled-circle    (fn
                                     [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
                                     (shape-drawer/set-color! shape-drawer color-float-bits)
                                     (shape-drawer/filled-circle! shape-drawer x y radius))
            :draw/filled-rectangle (fn
                                     [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                                     (shape-drawer/set-color! shape-drawer color-float-bits)
                                     (shape-drawer/filled-rectangle! shape-drawer x y w h))
            :draw/grid             (fn
                                     [ctx leftx bottomy gridw gridh cellw cellh color-float-bits]
                                     (let [w (* (float gridw) (float cellw))
                                           h (* (float gridh) (float cellh))
                                           topy (+ (float bottomy) (float h))
                                           rightx (+ (float leftx) (float w))]
                                       (doseq [idx (range (inc (float gridw)))
                                               :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
                                         (draw! ctx
                                                [[:draw/line [linex topy] [linex bottomy] color-float-bits]]))
                                       (doseq [idx (range (inc (float gridh)))
                                               :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
                                         (draw! ctx
                                                [[:draw/line [leftx liney] [rightx liney] color-float-bits]]))))
            :draw/line             (fn
                                     [{:keys [ctx/shape-drawer]} [sx sy] [ex ey] color-float-bits]
                                     (shape-drawer/set-color! shape-drawer color-float-bits)
                                     (shape-drawer/line! shape-drawer sx sy ex ey))
            :draw/rectangle        (fn
                                     [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                                     (shape-drawer/set-color! shape-drawer color-float-bits)
                                     (shape-drawer/rectangle! shape-drawer x y w h))
            :draw/sector           (fn
                                     [{:keys [ctx/shape-drawer]} [center-x center-y] radius start-radians radians color-float-bits]
                                     (shape-drawer/set-color! shape-drawer color-float-bits)
                                     (shape-drawer/sector! shape-drawer center-x center-y radius start-radians radians))
            :draw/text             draw.text/f
            :draw/texture-region   draw.texture-region/f!
            :draw/with-line-width  (fn
                                     [{:keys [ctx/shape-drawer]
                                       :as ctx}
                                      width
                                      draws]
                                     (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
                                       (shape-drawer/set-default-line-width! shape-drawer (* width old-line-width))
                                       (draw! ctx draws)
                                       (shape-drawer/set-default-line-width! shape-drawer old-line-width)))
            })
