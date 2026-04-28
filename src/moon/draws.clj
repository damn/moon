(ns moon.draws
  (:require [clojure.graphics.bitmap-font :as bitmap-font]
            [clojure.graphics.shape-drawer :as shape-drawer]
            [clojure.graphics.texture-region :as texture-region])
  (:import (com.badlogic.gdx.graphics.g2d Batch
                                          TextureRegion)))

(declare handle)

(def draw-fns
  {
   :draw/circle           (fn
                            [{:keys [ctx/shape-drawer]} position radius color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/circle! shape-drawer position radius))
   :draw/ellipse          (fn
                            [{:keys [ctx/shape-drawer]} position radius-x radius-y color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/ellipse! shape-drawer position radius-x radius-y))
   :draw/filled-circle    (fn
                            [{:keys [ctx/shape-drawer]} position radius color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/filled-circle! shape-drawer position radius))
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
                                (handle ctx
                                        [[:draw/line [linex topy] [linex bottomy] color-float-bits]]))
                              (doseq [idx (range (inc (float gridh)))
                                      :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
                                (handle ctx
                                        [[:draw/line [leftx liney] [rightx liney] color-float-bits]]))))
   :draw/line             (fn
                            [{:keys [ctx/shape-drawer]} start end color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/line! shape-drawer start end))
   :draw/rectangle        (fn
                            [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/rectangle! shape-drawer x y w h))
   :draw/sector           (fn
                            [{:keys [ctx/shape-drawer]} [center-x center-y] radius start-radians radians color-float-bits]
                            (shape-drawer/set-color! shape-drawer color-float-bits)
                            (shape-drawer/sector! shape-drawer center-x center-y radius start-radians radians))
   :draw/text             (fn
                            [{:keys [ctx/batch
                                     ctx/unit-scale
                                     ctx/default-font]}
                             {:keys [font scale x y text h-align up?]}]
                            (let [font (or font default-font)
                                  old-scale (bitmap-font/scale-x font)
                                  target-width 0
                                  wrap? false
                                  scale (* (float @unit-scale)
                                           (float (or scale 1)))]
                              (bitmap-font/set-scale! font (* old-scale scale))
                              (bitmap-font/draw! font
                                                 batch
                                                 text
                                                 x
                                                 (+ y (if up? (bitmap-font/text-height font text) 0))
                                                 target-width
                                                 (or h-align :align/center)
                                                 wrap?)
                              (bitmap-font/set-scale! font old-scale)))
   :draw/texture-region   (fn
                            [{:keys [^Batch ctx/batch
                                     ctx/unit-scale
                                     ctx/world-unit-scale]}
                             texture-region
                             [x y]
                             & {:keys [center? rotation]}]
                            (let [[w h] (let [dimensions [(texture-region/width  texture-region)
                                                          (texture-region/height texture-region)]]
                                          (if (= @unit-scale 1)
                                            dimensions
                                            (mapv (comp float (partial * world-unit-scale))
                                                  dimensions)))]
                              (if center?
                                (.draw batch
                                       texture-region
                                       (- (float x) (/ (float w) 2))
                                       (- (float y) (/ (float h) 2))
                                       (/ (float w) 2)
                                       (/ (float h) 2)
                                       w
                                       h
                                       1
                                       1
                                       (or rotation 0))
                                (.draw batch texture-region (float x) (float y) (float w) (float h)))))
   :draw/with-line-width  (fn
                            [{:keys [ctx/shape-drawer]
                              :as ctx}
                             width
                             draws]
                            (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
                              (shape-drawer/set-default-line-width! shape-drawer (* width old-line-width))
                              (handle ctx draws)
                              (shape-drawer/set-default-line-width! shape-drawer old-line-width)))
   })

(defn handle [ctx draws]
  (doseq [{k 0 :as component} draws
          :when component]
    (apply (get draw-fns k) ctx (rest component))))
