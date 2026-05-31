(ns render.draw-on-world-viewport
  (:require [com.badlogic.gdx.graphics.color :as color]
            [draw-on-world-viewport.draw-entities]
            [game.ctx :as ctx]
            [gdx.graphics.orthographic-camera :as camera]
            [space.earlygrey.shape-drawer :as shape-drawer]))

(defn camera-frustum [{:keys [ctx/world-viewport]}]
  (camera/frustum (:viewport/camera world-viewport)))

(defn draw-tile-grid
  [ctx]
  (let [[left-x _right-x bottom-y _top-y] (camera-frustum ctx)]
    [[:draw/grid
      (int left-x)
      (int bottom-y)
      (inc (int (ctx/world-viewport-width ctx)))
      (+ 2 (int (ctx/world-viewport-height ctx)))
      1
      1
      (color/float-bits [1 1 1 0.8])]]))

(def ^:dbg-flag show-potential-field-colors? false) ; :good, :evil
(def ^:dbg-flag show-cell-entities? false)
(def ^:dbg-flag show-cell-occupied? false)

(defn draw-cell-debug
  [{:keys [ctx/colors
           ctx/grid
           ctx/factions-iterations]
    :as ctx}]
  (apply concat
         (for [[x y] (ctx/visible-tiles ctx)
               :let [cell (grid [x y])]
               :when cell
               :let [cell* @cell]]
           [(when (and show-cell-entities? (seq (:entities cell*)))
              [:draw/filled-rectangle x y 1 1 (:colors/debug-cell-entities colors)])
            (when (and show-cell-occupied? (seq (:occupied cell*)))
              [:draw/filled-rectangle x y 1 1 (:colors/debug-cell-occupied colors)])
            (when-let [faction show-potential-field-colors?]
              (let [{:keys [distance]} (faction cell*)]
                (when distance
                  (let [ratio (/ distance (factions-iterations faction))]
                    [:draw/filled-rectangle x y 1 1 ((:colors/debug-potential-field colors) ratio)]))))])))

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
               draw-cell-debug
               draw-on-world-viewport.draw-entities/do!
               #_moon.geom-test
               highlight-mouseover-tile
               ]]
      (ctx/draw! ctx (f ctx)))
    (reset! unit-scale 1)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width))
  (.end batch)
  ctx)
