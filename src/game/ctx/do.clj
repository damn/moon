(ns game.ctx.do
  (:require [clojure.core-ext :refer [actions!
                                      reduce-actions!]]
            [com.badlogic.gdx.audio.sound :as sound]
            [game.info :as info]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.ui.action-bar :as action-bar]
            [gdx.stage :as stage]
            [moon.textures :as textures]
            [moon.ui.inventory-window :as inventory-window]
            [reaction-txs.show-modal]
            tx.add-text-effect
            tx.set-cooldown
            tx.add-skill
            tx.pickup-item
            tx.remove-item
            tx.spawn-line
            tx.state-enter
            tx.effect
            tx.spawn-item
            tx.spawn-alert
            tx.move-entity
            tx.spawn-effect
            tx.spawn-creature
            tx.state-exit
            tx.audiovisual
            tx.unregister-eid
            tx.assoc
            tx.assoc-in
            tx.dissoc
            tx.update
            tx.mark-destroyed
            tx.nothing
            tx.spawn-entity
            tx.event
            tx.register-eid
            tx.spawn-projectile
            tx.set-item
            ))

; TODO tx-core/* ?
; just (def txs-fn-map (edn-resource "config/txs-fn-map.edn"))
; possible?
(def txs-fn-map
  {
   :tx/state-exit               tx.state-exit/do!
   :tx/audiovisual              tx.audiovisual/do!
   :tx/assoc                    tx.assoc/f
   :tx/assoc-in                 tx.assoc-in/f
   :tx/dissoc                   tx.dissoc/f
   :tx/update                   tx.update/f
   :tx/mark-destroyed           tx.mark-destroyed/f
   :tx/set-cooldown             tx.set-cooldown/do!
   :tx/add-text-effect          tx.add-text-effect/do!
   :tx/add-skill                tx.add-skill/do!
   :tx/set-item                 tx.set-item/do!
   :tx/remove-item              tx.remove-item/do!
   :tx/pickup-item              tx.pickup-item/do!
   :tx/event                    tx.event/do!
   :tx/register-eid             tx.register-eid/do!
   :tx/unregister-eid           tx.unregister-eid/do!
   :tx/state-enter              tx.state-enter/do!
   :tx/effect                   tx.effect/do!
   :tx/spawn-alert              tx.spawn-alert/do!
   :tx/spawn-line               tx.spawn-line/do!
   :tx/move-entity              tx.move-entity/do!
   :tx/spawn-projectile         tx.spawn-projectile/do!
   :tx/spawn-effect             tx.spawn-effect/do!
   :tx/spawn-item               tx.spawn-item/do!
   :tx/spawn-creature           tx.spawn-creature/do!
   :tx/spawn-entity             tx.spawn-entity/do!
   :tx/sound                    tx.nothing/f
   :tx/toggle-inventory-visible tx.nothing/f
   :tx/show-message             tx.nothing/f
   :tx/show-modal               tx.nothing/f
   }
  )

(def reaction-txs-fn-map
  {
   :tx/sound                    (fn
                                  [{:keys [ctx/audio] :as ctx} sound-name]
                                  (let [sounds audio]
                                    (assert (contains? sounds sound-name) (str sound-name))
                                    (sound/play! (get sounds sound-name)))
                                  ctx)
   :tx/toggle-inventory-visible (fn
                                  [{:keys [ctx/stage] :as ctx}]
                                  (-> stage
                                      (stage/find-actor "moon.ui.windows.inventory")
                                      actor/toggle-visible!)
                                  ctx)
   :tx/show-message             (fn
                                  [{:keys [ctx/stage] :as ctx} message]
                                  (-> stage
                                      (stage/find-actor "player-message")
                                      (actor/set-user-object! (atom {:text message
                                                                     :counter 0})))
                                  ctx)
   :tx/show-modal               reaction-txs.show-modal/f
   :tx/set-item                 (fn
                                  [{:keys [ctx/skin
                                           ctx/stage
                                           ctx/textures]
                                    :as ctx}
                                   eid cell item]
                                  (when (:entity/player? @eid)
                                    (-> stage
                                        ;(group/find-actor "moon.ui.windows")
                                        (stage/find-actor "moon.ui.windows.inventory")
                                        (inventory-window/set-item! cell {:texture-region (textures/texture-region textures (:entity/image item))
                                                                          :tooltip-text (info/text item ctx)}
                                                                    skin)))
                                  ctx)
   :tx/remove-item              (fn
                                  [{:keys [ctx/stage] :as ctx} eid cell]
                                  (when (:entity/player? @eid)
                                    (-> stage
                                        ;(group/find-actor "moon.ui.windows")
                                        (stage/find-actor "moon.ui.windows.inventory")
                                        (inventory-window/remove-item! cell)))
                                  ctx)
   :tx/add-skill                (fn
                                  [{:keys [ctx/skin
                                           ctx/stage
                                           ctx/textures]
                                    :as ctx}
                                   eid skill]
                                  (when (:entity/player? @eid)
                                    (-> stage
                                        (stage/find-actor "moon.ui.action-bar")
                                        (action-bar/add-skill! {:skill-id (:property/id skill)
                                                                :texture-region (textures/texture-region textures (:entity/image skill))
                                                                :tooltip-text (info/text skill ctx)}
                                                               skin)))
                                  ctx)
   }
  )


#_(remove-skill! [stage skill-id]
                 (-> stage
                     (stage/find-actor "moon.ui.action-bar")
                     (action-bar/remove-skill! skill-id)))

(defn do! [ctx txs]
  (let [handled-txs (try (actions! txs-fn-map ctx txs)
                         (catch Throwable t
                           (throw (ex-info "Error handling txs"
                                           {:txs txs} t))))]
    (reduce-actions! reaction-txs-fn-map
                     ctx
                     handled-txs)))
