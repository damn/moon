(ns render.draw-on-world-viewport
  (:require [com.badlogic.gdx.graphics.color :as color]
            [draw-on-world-viewport.draw-entities]
            [draw-on-world-viewport.draw-cell-debug]
            [game.ctx.draw :refer [draw!]]
            [gdx.graphics.orthographic-camera :as camera]
            [space.earlygrey.shape-drawer :as shape-drawer]))

(defn draw-tile-grid
  [{:keys [ctx/world-viewport]}]
  (let [[left-x _right-x bottom-y _top-y] (camera/frustum (:viewport/camera world-viewport))]
    [[:draw/grid
      (int left-x)
      (int bottom-y)
      (inc (int (:viewport/world-width world-viewport)))
      (+ 2 (int (:viewport/world-height world-viewport)))
      1
      1
      (color/float-bits [1 1 1 0.8])]]))

(defn highlight-mouseover-tile
  [{:keys [ctx/colors
           ctx/grid
           ctx/world-mouse-position]}]
  (let [[x y] (mapv int world-mouse-position)
        cell (grid [x y])]
    (when (and cell (#{:air :none} (:movement @cell)))
      [[:draw/rectangle x y 1 1
        (case (:movement @cell)
          :air  (:colors/mouseover-tile-air colors)
          :none (:colors/mouseover-tile-none colors))]])))

(defn step
  [{:keys [^com.badlogic.gdx.graphics.g2d.SpriteBatch ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}]
  ; fix scene2d.ui.tooltip flickering
  ; _everything_ flickers with TextToolTip!
  ; it changes batch color somehow and does not change it back ! FIXME
  (.setColor batch 1 1 1 1)
  ;
  (.setProjectionMatrix batch (camera/combined (:viewport/camera world-viewport)))
  (.begin batch)
  (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [f [
               #_draw-tile-grid
               draw-on-world-viewport.draw-cell-debug/f
               draw-on-world-viewport.draw-entities/do!
               #_moon.geom-test
               highlight-mouseover-tile
               ]]
      (draw! ctx (f ctx)))
    (reset! unit-scale 1)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width))
  (.end batch)
  ctx)
