(ns moon.draw-on-world-viewport.draw-entities
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [moon.ctx :as ctx]
            [moon.color :as colors]
            [moon.entity :as entity]
            [moon.raycaster :as raycaster]
            [moon.throwable :as throwable]
            [moon.order :as order]))

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
  [ctx entity render-layer]
  (try (do
        (when show-body-bounds?
          (ctx/draw! ctx (draw-body-rect (:entity/body entity)
                                         (if (:body/collides? (:entity/body entity))
                                           (color/float-bits colors/white)
                                           (color/float-bits colors/gray)))))
        (doseq [[k v] entity
                :when (get render-layer k)]
          (ctx/draw! ctx (entity/render [k v] entity ctx))))
       (catch Throwable t
         (ctx/draw! ctx (draw-body-rect (:entity/body entity) (color/float-bits colors/red)))
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
