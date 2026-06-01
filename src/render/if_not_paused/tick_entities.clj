(ns render.if-not-paused.tick-entities
  (:require [game.ctx.do :refer [do!]]
            [gdx.stage :as stage]
            [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window]
            entity.tick.animation
            entity.tick.alert-friendlies-after-duration
            entity.tick.string-effect
            entity.tick.skills
            entity.tick.temp-modifier
            entity.tick.projectile-collision
            entity.tick.active-skill
            entity.tick.delete-after-duration
            entity.tick.stunned
            entity.tick.npc-moving
            entity.tick.npc-sleeping
            entity.tick.npc-idle
            entity.tick.movement))

(def k->tick
  {
   :entity/animation entity.tick.animation/f
   :entity/alert-friendlies-after-duration entity.tick.alert-friendlies-after-duration/f
   :entity/string-effect entity.tick.string-effect/f
   :entity/skills entity.tick.skills/f
   :entity/temp-modifier entity.tick.temp-modifier/f
   :entity/projectile-collision entity.tick.projectile-collision/f
   :active-skill entity.tick.active-skill/f
   :entity/delete-after-duration entity.tick.delete-after-duration/f
   :stunned entity.tick.stunned/f
   :npc-moving entity.tick.npc-moving/f
   :npc-sleeping entity.tick.npc-sleeping/f
   :npc-idle entity.tick.npc-idle/f
   :entity/movement entity.tick.movement/f
   }
  )

(defn f
  [{:keys [ctx/active-entities
           ctx/skin
           ctx/stage]
    :as ctx}]
  (try
   (do! ctx
        (mapcat (fn [eid]
                  (mapcat (fn [[k v]]
                            (try (if-let [f (k->tick k)]
                                   (f v eid ctx)
                                   nil)
                                 (catch Throwable t
                                   (throw (ex-info "Error at `entity/tick`:" {:eid eid} t)))))
                          @eid))
                active-entities))
   (catch Throwable t
     (throwable/pretty-pst t)
     (stage/add-actor! stage
                       (error-window/create
                        {:skin skin
                         :throwable t}))))
  ctx)
