(ns draw-on-world-viewport.draw-entities
  (:require [clojure.core-ext :refer [sort-by-order]]
            [game.ctx.draw :refer [draw!]]
            [moon.body :as body]
            [moon.raycaster :as raycaster]
            [moon.throwable :as throwable]
            entity.render.clickable
            entity.render.player-item-on-cursor
            entity.render.animation
            entity.render.image
            entity.render.line-render
            entity.render.mouseover
            entity.render.stats
            entity.render.string-effect
            entity.render.temp-modifier
            entity.render.active-skill
            entity.render.npc-sleeping
            entity.render.stunned))

(def k->render-fn
  {
   :entity/clickable entity.render.clickable/f
   :player-item-on-cursor entity.render.player-item-on-cursor/f
   :entity/animation entity.render.animation/f
   :entity/image entity.render.image/f
   :entity/line-render entity.render.line-render/f
   :entity/mouseover? entity.render.mouseover/f
   :entity/stats entity.render.stats/f
   :entity/string-effect entity.render.string-effect/f
   :entity/temp-modifier entity.render.temp-modifier/f
   :active-skill entity.render.active-skill/f
   :npc-sleeping entity.render.npc-sleeping/f
   :stunned entity.render.stunned/f}
  )

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
          (draw! ctx (draw-body-rect (:entity/body entity)
                                     (if (:body/collides? (:entity/body entity))
                                       (:colors/debug-body-outline-collides colors)
                                       (:colors/debug-body-outline colors)))))
        (doseq [[k v] entity
                :when (get render-layer k)]
          (draw! ctx ((k->render-fn k) v entity ctx))))
       (catch Throwable t
         (draw! ctx (draw-body-rect (:entity/body entity) (:colors/debug-body-outline-render-error colors)))
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
    (doseq [[z-order entities] (sort-by-order (group-by (comp :body/z-order :entity/body) entities)
                                              first
                                              render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (draw-entity ctx entity render-layer))))
