(ns moon.application.render.draw-on-world-viewport.draw-entities
  (:require [moon.draws :as draws]
            [moon.entity :as entity]
            [moon.throwable :as throwable]
            [moon.raycaster :as raycaster]
            [moon.order :as order]))

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

(defn do!
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
