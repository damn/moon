(ns clojure.draw
  (:require [clojure.align :as align]
            [clojure.bitmap-font :as bitmap-font]
            [clojure.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [clojure.graphics-shape-drawer :as shape-drawer]
            [clojure.string :as str]
            [clojure.texture-region :as texture-region]
            [clojure.world-unit-scale :refer [world-unit-scale]])
  (:import (com.badlogic.gdx.graphics.g2d Batch TextureRegion)))

(declare draw!)

(defn- draw-texture-region-on-batch!
  ([^Batch batch ^TextureRegion texture-region x y w h]
   (Batch/.draw batch texture-region (float x) (float y) (float w) (float h)))
  ([^Batch batch ^TextureRegion texture-region x y origin-x origin-y w h scale-x scale-y rotation]
   (Batch/.draw batch
                texture-region
                (float x)
                (float y)
                (float origin-x)
                (float origin-y)
                (float w)
                (float h)
                (float scale-x)
                (float scale-y)
                (float rotation))))

(def ^:private draw-fns
  {:draw/circle (fn [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
                  (shape-drawer/set-color! shape-drawer color-float-bits)
                  (shape-drawer/circle! shape-drawer x y radius))
   :draw/ellipse (fn [{:keys [ctx/shape-drawer]} [x y] radius-x radius-y color-float-bits]
                   (shape-drawer/set-color! shape-drawer color-float-bits)
                   (shape-drawer/ellipse! shape-drawer x y radius-x radius-y))
   :draw/filled-circle (fn [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
                          (shape-drawer/set-color! shape-drawer color-float-bits)
                          (shape-drawer/filled-circle! shape-drawer x y radius))
   :draw/filled-rectangle (fn [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                             (shape-drawer/set-color! shape-drawer color-float-bits)
                             (shape-drawer/filled-rectangle! shape-drawer x y w h))
   :draw/grid (fn [ctx leftx bottomy gridw gridh cellw cellh color-float-bits]
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
   :draw/line (fn [{:keys [ctx/shape-drawer]} [sx sy] [ex ey] color-float-bits]
                 (shape-drawer/set-color! shape-drawer color-float-bits)
                 (shape-drawer/line! shape-drawer sx sy ex ey))
   :draw/rectangle (fn [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
                      (shape-drawer/set-color! shape-drawer color-float-bits)
                      (shape-drawer/rectangle! shape-drawer x y w h))
   :draw/sector (fn [{:keys [ctx/shape-drawer]} [center-x center-y] radius start-radians radians color-float-bits]
                   (shape-drawer/set-color! shape-drawer color-float-bits)
                   (shape-drawer/sector! shape-drawer center-x center-y radius start-radians radians))
   :draw/text (fn [{:keys [ctx/batch
                            ctx/unit-scale
                            ctx/default-font]}
                     {:keys [font scale x y text up?]}]
                 (let [font (or font default-font)
                       unit-scale @unit-scale
                       scale (or scale 1)
                       font-data (bitmap-font/get-data font)
                       old-scale (bitmap-font-data/get-scale-x font-data)
                       target-width 0
                       wrap? false
                       scale (* (float unit-scale)
                                (float scale))]
                   (bitmap-font-data/set-scale! font-data (* old-scale scale))
                   (bitmap-font/draw! font
                                      batch
                                      text
                                      x
                                      (+ y (if up?
                                             (-> text
                                                 (str/split #"\n")
                                                 count
                                                 (* (bitmap-font/get-line-height font)))
                                             0))
                                      target-width
                                      align/center
                                      wrap?)
                   (bitmap-font-data/set-scale! font-data old-scale)))
   :draw/texture-region (fn [{:keys [ctx/batch
                                      ctx/unit-scale]}
                               texture-region
                               [x y]
                               & {:keys [center? rotation]}]
                           (let [[w h] (let [dimensions [(texture-region/get-region-width texture-region)
                                                         (texture-region/get-region-height texture-region)]]
                                          (if (= @unit-scale 1)
                                            dimensions
                                            (mapv (comp float (partial * world-unit-scale))
                                                  dimensions)))]
                             (if center?
                               (draw-texture-region-on-batch! batch
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
                               (draw-texture-region-on-batch! batch texture-region x y w h))))
   :draw/with-line-width (fn [{:keys [ctx/shape-drawer]
                                :as ctx}
                               width
                               draws]
                           (let [old-line-width (shape-drawer/get-default-line-width shape-drawer)]
                             (shape-drawer/set-default-line-width! shape-drawer (* width old-line-width))
                             (draw! ctx draws)
                             (shape-drawer/set-default-line-width! shape-drawer old-line-width)))})

(defn draw! [ctx draws]
  (doseq [{k 0 :as component} draws
          :when component]
    (apply (get draw-fns k) ctx (rest component))))
