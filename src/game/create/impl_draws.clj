(ns game.create.impl-draws
  (:require [clojure.string :as str]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.data :as data]
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.utils.align :as align]
            [gdl.graphics.batch :as batch]
            [moon.draws :as draws]
            [gdl.graphics.shape-drawer :as shape-drawer]))

(def draw-fns
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
                                (draws/handle ctx
                                              [[:draw/line [linex topy] [linex bottomy] color-float-bits]]))
                              (doseq [idx (range (inc (float gridh)))
                                      :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
                                (draws/handle ctx
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
   :draw/text             (fn
                            [{:keys [ctx/batch
                                     ctx/unit-scale
                                     ctx/default-font]}
                             {:keys [font scale x y text h-align up?]}]
                            (let [font (or font default-font)
                                  old-scale (data/scale-x (font/data font))
                                  target-width 0
                                  wrap? false
                                  scale (* (float @unit-scale)
                                           (float (or scale 1)))]
                              (data/set-scale! (font/data font) (* old-scale scale))
                              (font/draw! font
                                          batch
                                          text
                                          x
                                          (+ y (if up?
                                                 (-> text
                                                     (str/split #"\n")
                                                     count
                                                     (* (font/line-height font)))
                                                 0))
                                          target-width
                                          (align/k->value :align/center)
                                          wrap?)
                              (data/set-scale! (font/data font) old-scale)))
   :draw/texture-region   (fn
                            [{:keys [ctx/batch
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
                                (batch/draw! batch
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
                                (batch/draw! batch texture-region x y w h))))
   :draw/with-line-width  (fn
                            [{:keys [ctx/shape-drawer]
                              :as ctx}
                             width
                             draws]
                            (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
                              (shape-drawer/set-default-line-width! shape-drawer (* width old-line-width))
                              (draws/handle ctx draws)
                              (shape-drawer/set-default-line-width! shape-drawer old-line-width)))
   })

(defn step [ctx]
  (extend-type (class ctx)
    draws/Draws
    (handle [ctx draws]
      (doseq [{k 0 :as component} draws
              :when component]
        (apply (get draw-fns k) ctx (rest component)))))
  ctx)
