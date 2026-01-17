(ns moon.render.draw-on-world-viewport
  (:require [clj.api.space.earlygrey.shape-drawer :as sd]
            [moon.ctx :as ctx]
            [moon.color :as color]
            [moon.entity :as entity]
            [moon.graphics.camera :as camera]
            [moon.raycaster :as raycaster]
            [moon.throwable :as throwable]
            [moon.utils :as utils])
  (:import (com.badlogic.gdx.graphics.g2d Batch)
           (com.badlogic.gdx.utils.viewport Viewport)))

(def ^:dbg-flag show-tile-grid? false)

(defn draw-tile-grid
  [{:keys [ctx/world-viewport]}]
  (when show-tile-grid?
    (let [[left-x _right-x bottom-y _top-y] (camera/frustum (Viewport/.getCamera world-viewport))]
      [[:draw/grid
        (int left-x)
        (int bottom-y)
        (inc (int (Viewport/.getWorldWidth  world-viewport)))
        (+ 2 (int (Viewport/.getWorldHeight world-viewport)))
        1
        1
        [1 1 1 0.8]]])))

(defn- highlight-mouseover-tile
  [{:keys [ctx/world
           ctx/world-mouse-position]}]
  (let [[x y] (mapv int world-mouse-position)
        cell ((:world/grid world) [x y])]
    (when (and cell (#{:air :none} (:movement @cell)))
      [[:draw/rectangle x y 1 1
        (case (:movement @cell)
          :air  [1 1 0 0.5]
          :none [1 0 0 0.5])]])))

(def ^:dbg-flag show-potential-field-colors? false) ; :good, :evil
(def ^:dbg-flag show-cell-entities? false)
(def ^:dbg-flag show-cell-occupied? false)

(defn- draw-cell-debug
  [{:keys [ctx/world
           ctx/world-viewport]}]
  (apply concat
         (for [[x y] (camera/visible-tiles (Viewport/.getCamera world-viewport))
               :let [cell ((:world/grid world) [x y])]
               :when cell
               :let [cell* @cell]]
           [(when (and show-cell-entities? (seq (:entities cell*)))
              [:draw/filled-rectangle x y 1 1 [1 0 0 0.6]])
            (when (and show-cell-occupied? (seq (:occupied cell*)))
              [:draw/filled-rectangle x y 1 1 [0 0 1 0.6]])
            (when-let [faction show-potential-field-colors?]
              (let [{:keys [distance]} (faction cell*)]
                (when distance
                  (let [ratio (/ distance ((:world/factions-iterations world) faction))]
                    [:draw/filled-rectangle x y 1 1 [ratio (- 1 ratio) ratio 0.6]]))))])))

(def ^:private render-layers
  [#{:entity/mouseover?
     :stunned
     :player-item-on-cursor}
   #{:entity/clickable
     :entity/animation
     :entity/image
     :entity/line-render}
   #{:npc-sleeping
     :entity/temp-modifier
     :entity/string-effect}
   #{:entity/stats
     :active-skill}])

(def ^:dbg-flag show-body-bounds? false)

(defn- draw-body-rect [{:keys [body/position body/width body/height]} color]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    [[:draw/rectangle x y width height color]]))

(defn- draw-entity
  [ctx entity render-layer]
  (try (do
        (when show-body-bounds?
          (ctx/draw! ctx (draw-body-rect (:entity/body entity)
                                                   (if (:body/collides? (:entity/body entity))
                                                     color/white
                                                     color/gray))))
        (doseq [[k v] entity
                :when (get render-layer k)]
          (ctx/draw! ctx (entity/render [k v] entity ctx))))
       (catch Throwable t
         (ctx/draw! ctx (draw-body-rect (:entity/body entity) color/red))
         (throwable/pretty-pst t))))

(defn draw-entities
  [{:keys [ctx/active-entities
           ctx/player-eid
           ctx/world]
    :as ctx}]
  (let [entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (raycaster/line-of-sight? (:world/raycaster world) player entity)))]
    (doseq [[z-order entities] (utils/sort-by-order (group-by (comp :body/z-order :entity/body) entities)
                                                    first
                                                    (:world/render-z-order world))
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (draw-entity ctx entity render-layer))))

(defn do!
  [{:keys [^Batch ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}]
  ; fix scene2d.ui.tooltip flickering
  ; _everything_ flickers with vis ui tooltip! it changes batch color somehow and does not
  ; change it back !
  (.setColor batch 1 1 1 1)
  (.setProjectionMatrix batch (camera/combined (Viewport/.getCamera world-viewport)))
  (.begin batch)
  (sd/with-line-width shape-drawer world-unit-scale
    (reset! unit-scale world-unit-scale)
    (doseq [f [draw-tile-grid ; TODO ?
               draw-cell-debug
               draw-entities
               #_moon.application.render.draw-on-world-viewport.geom-test/do!
               highlight-mouseover-tile]]
      (ctx/draw! ctx (f ctx)))
    (reset! unit-scale 1))
  (.end batch)
  ctx)
