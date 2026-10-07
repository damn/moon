(ns game.listener.render.draw-world.draw-entities
  (:require [game.shared :refer [colors
                                 draw-component
                                 draw-fn-rectangle]]
            [moon.coll :as coll]
            [moon.raycaster :as raycaster]))

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

(defn- draw-entity-rectangle!
  [ctx shape-drawer entity color-float-bits]
  (let [{:keys [entity/position entity/width entity/height]} entity
        [x y] [(- (position 0) (/ width 2))
               (- (position 1) (/ height 2))]]
    (draw-fn-rectangle shape-drawer x y width height color-float-bits)))

(defn draw-entities!
  [ctx shape-drawer batch default-font textures unit-scale world-unit-scale mouseover-actor world-mouse-position
   player-eid raycaster elapsed-time show-body-bounds? active-entities render-z-order]
  (let [player-eid @player-eid
        raycaster @raycaster
        elapsed-time @elapsed-time
        show-body-bounds? @show-body-bounds?
        active-entities @active-entities
        entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (raycaster/line-of-sight? raycaster player entity)))]
    (doseq [[z-order entities] (coll/sort-by-order (group-by :entity/z-order entities)
                                                first
                                                render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (when show-body-bounds?
        (draw-entity-rectangle! ctx shape-drawer
                                entity
                                (if (:entity/collides? entity)
                                  (:colors/debug-body-outline-collides colors)
                                  (:colors/debug-body-outline colors))))
      (doseq [[k v] entity
              :when (get render-layer k)]
        (draw-component shape-drawer batch default-font unit-scale world-unit-scale mouseover-actor world-mouse-position
                        textures colors player elapsed-time active-entities raycaster
                        entity k v)))))
