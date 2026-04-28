(ns moon.render.draw-on-world-viewport
  (:require [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.viewport :as viewport]
            [moon.draws :as draws]
            [moon.entity :as entity]
            [moon.graphics :as graphics]
            [moon.throwable :as throwable]
            [moon.raycaster :as raycaster]
            [moon.order :as order]))

(defn draw-tile-grid
  [{:keys [ctx/world-viewport]}]
  (let [[left-x _right-x bottom-y _top-y] (camera/frustum (viewport/camera world-viewport))]
    [[:draw/grid
      (int left-x)
      (int bottom-y)
      (inc (int (viewport/world-width  world-viewport)))
      (+ 2 (int (viewport/world-height world-viewport)))
      1
      1
      [1 1 1 0.8]]]))

(def ^:dbg-flag show-potential-field-colors? false) ; :good, :evil
(def ^:dbg-flag show-cell-entities? false)
(def ^:dbg-flag show-cell-occupied? false)

(defn draw-cell-debug
  [{:keys [ctx/colors
           ctx/grid
           ctx/factions-iterations
           ctx/world-viewport]}]
  (apply concat
         (for [[x y] (camera/visible-tiles (viewport/camera world-viewport))
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

(def ^:private render-layers ; TODO move external - simple TODO/checklist / state pass
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

(def ^:dbg-flag show-body-bounds? false) ; TODO same here?

(defn- draw-body-rect [{:keys [body/position body/width body/height]} color-float-bits]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    [[:draw/rectangle x y width height color-float-bits]]))

(defn- draw-entity
  [{:keys [ctx/colors] :as ctx} entity render-layer]
  (try (do
        (when show-body-bounds?
          (draws/handle ctx (draw-body-rect (:entity/body entity)
                                             (if (:body/collides? (:entity/body entity))
                                               (:colors/debug-body-outline-collides colors)
                                               (:colors/debug-body-outline colors)))))
        (doseq [[k v] entity
                :when (get render-layer k)]
          (draws/handle ctx (entity/render [k v] entity ctx))))
       (catch Throwable t
         (draws/handle ctx (draw-body-rect (:entity/body entity) (:colors/debug-body-outline-render-error colors)))
         (throwable/pretty-pst t))))

(defn draw-entities
  [{:keys [ctx/active-entities
           ctx/player-eid
           ctx/raycaster
           ctx/render-z-order]
    :as ctx}]
  (let [entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (raycaster/line-of-sight? raycaster player entity)))]
    (doseq [[z-order entities] (order/sort-by-order (group-by (comp :body/z-order :entity/body) entities)
                                                    first
                                                    render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (draw-entity ctx entity render-layer))))

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
  [ctx]
  (graphics/draw-on-world-viewport! ctx
                                    (fn []
                                      (doseq [f [
                                                 #_draw-tile-grid
                                                 draw-cell-debug
                                                 draw-entities
                                                 #_moon.geom-test
                                                 highlight-mouseover-tile
                                                 ]]
                                        (draws/handle ctx (f ctx)))))
  ctx)
