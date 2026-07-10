(ns clojure.moon.draw-on-world-viewport
  (:require [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [clojure.body.draw-rectangle :as draw-rectangle]
            [clojure.moon.draw :refer [draw!]]
            [clojure.moon.draw-component :refer [draw-component]]
            [clojure.moon.factions-iterations :refer [factions-iterations]]
            [space.earlygrey.shapedrawer.shape-drawer :as shape-drawer]
            [clojure.line-of-sight :as line-of-sight?]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [clojure.sort-by-order :as sort-by-order]
            [clojure.throwable :as throwable]
            [clojure.orthographic-camera.visible-tiles :refer [visible-tiles]]
            [clojure.moon.world-unit-scale :refer [world-unit-scale]]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]))

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

(defn- draw-cell-debug
  [{:keys [ctx/colors
           ctx/grid
           ctx/world-viewport
           ctx/show-potential-field-colors?
           ctx/show-cell-entities?
           ctx/show-cell-occupied?]}]
  (apply concat
         (for [[x y] (visible-tiles (viewport/getCamera world-viewport))
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

(defn- draw-entities!
  [{:keys [ctx/active-entities
           ctx/colors
           ctx/player-eid
           ctx/raycaster
           ctx/render-z-order
           ctx/show-body-bounds?]
    :as ctx}]
  (let [entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (line-of-sight?/f raycaster player entity)))]
    (doseq [[z-order entities] (sort-by-order/f (group-by (comp :body/z-order :entity/body) entities)
                                                first
                                                render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (try
        (do
          (when show-body-bounds?
            (draw! ctx (draw-rectangle/f (:entity/body entity)
                                         (if (:body/collides? (:entity/body entity))
                                           (:colors/debug-body-outline-collides colors)
                                           (:colors/debug-body-outline colors)))))
          (doseq [[k v] entity
                  :when (get render-layer k)]
            (draw! ctx (draw-component ctx entity k v))))
        (catch Throwable t
          (draw! ctx (draw-rectangle/f (:entity/body entity) (:colors/debug-body-outline-render-error colors)))
          (throwable/pretty-pst t))))))

(defn- highlight-mouseover-tile
  [{:keys [ctx/colors
           ctx/grid
           ctx/world-mouse-position]}]
  (let [[x y] (mapv int world-mouse-position)
        cell (grid [x y])]
    (when (and cell (#{:air :none} (:movement @cell)))
      [[:draw/rectangle x y 1 1
        (case (:movement @cell)
          :air (:colors/mouseover-tile-air colors)
          :none (:colors/mouseover-tile-none colors))]])))

(defn f
  [{:keys [ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-viewport]
    :as ctx}]
  (batch/setColor batch 1 1 1 1)
  (batch/setProjectionMatrix batch (orthographic-camera/combined (viewport/getCamera world-viewport)))
  (batch/begin batch)
  (let [old-line-width (shape-drawer/getDefaultLineWidth shape-drawer)]
    (shape-drawer/setDefaultLineWidth shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [draw-fn [draw-cell-debug
                     draw-entities!
                     highlight-mouseover-tile]]
      (draw! ctx (draw-fn ctx)))
    (reset! unit-scale 1)
    (shape-drawer/setDefaultLineWidth shape-drawer old-line-width))
  (batch/end batch)
  ctx)
